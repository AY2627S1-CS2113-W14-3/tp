package seedu.whatscooking.command;

import java.util.logging.Level;
import java.util.logging.Logger;

import seedu.whatscooking.AppLogger;
import seedu.whatscooking.WhatsCookingException;
import seedu.whatscooking.recipe.Ingredient;
import seedu.whatscooking.recipe.Recipe;
import seedu.whatscooking.recipe.RecipeList;
import seedu.whatscooking.ui.Ui;

/**
 * Adds a new recipe to the list from one line of input:
 * {@code add TITLE[, TYPE] [i/INGREDIENTS] [st/STEP]... [n/NOTE]}.
 * <p>
 * The whole line is validated before anything is saved, so a malformed
 * command never leaves a half-built recipe in the list.
 */
public class AddCommand extends Command {
    private static final Logger logger = AppLogger.getLogger(AddCommand.class);

    //Each prefix starts with a space so that "i/" inside a word (e.g. "wasabi/") is not matched
    private static final String INGREDIENT_PREFIX = " i/";
    private static final String STEP_PREFIX = " st/";
    private static final String NOTE_PREFIX = " n/";

    private static final String MESSAGE_WRONG_FORMAT =
            "WRONG FORMAT! Add requires "
            + "\"add RECIPE_TITLE[, TYPE] [i/INGREDIENTS] [st/STEP]... [n/NOTE]\" "
            + "e.g. add egg taco, snack i/3 eggs, 5 tortillas st/Scramble egg st/Put egg in tortilla n/Serve hot";

    private static final String MESSAGE_WRONG_INGREDIENTS =
            "Wrong format! Ingredients require "
            + "\"QUANTITY INGREDIENT, QUANTITY INGREDIENT\" e.g. 3 eggs, 5 tortillas";

    private static final String MESSAGE_EMPTY_STEP = "A step cannot be empty. e.g. Scramble egg in pan until yellow";
    private static final String MESSAGE_EMPTY_TYPE =
            "The type after the comma cannot be empty. e.g. add egg taco, snack";
    private static final String MESSAGE_EMPTY_NOTE = "A note cannot be empty. e.g. n/Best eaten hot";
    private static final String MESSAGE_DUPLICATE_INGREDIENTS =
            "Only one i/ is allowed. List all ingredients after it, separated by commas.";
    private static final String MESSAGE_DUPLICATE_NOTE = "Only one n/ is allowed per recipe.";
    private static final String MESSAGE_NOTE_NOT_LAST =
            "The note (n/) must come last, after all ingredients and steps.";
    private static final String MESSAGE_INGREDIENTS_AFTER_STEPS =
            "Ingredients (i/) must come before steps (st/).";

    private final String arguments;

    /**
     * Creates an add command for the given argument string.
     *
     * @param arguments everything the user typed after "add"; never null
     */
    public AddCommand(String arguments) {
        assert arguments != null : "Parser should pass \"\" rather than null when add has no arguments";
        this.arguments = arguments;
    }

    @Override
    public void execute(RecipeList recipes, Ui ui) throws WhatsCookingException {
        assert recipes != null : "Recipe list should never be null";
        assert ui != null : "Ui should never be null";
        logger.log(Level.FINE, "Executing add with arguments \"{0}\"", arguments);

        //Leading space lets a prefix match even when it is the first thing typed
        String input = " " + arguments;

        //Peel off the note first, since it must be the last part: [everything else, note]
        String[] noteSplit = input.split(NOTE_PREFIX, 2);
        //Cut off everything from the first " st/" onwards: [before steps, steps]
        String[] stepSplit = noteSplit[0].split(STEP_PREFIX, 2);
        //Cut off the ingredients: [title and type, ingredients]
        String[] ingredientSplit = stepSplit[0].split(INGREDIENT_PREFIX, 2);
        //Split the title from the type at the first comma: [title, type]
        String[] titleSplit = ingredientSplit[0].split(",", 2);

        String title = titleSplit[0].trim();
        if (title.isEmpty()) {
            logger.log(Level.INFO, "Add rejected: missing title");
            throw new WhatsCookingException(MESSAGE_WRONG_FORMAT);
        }

        String type = "";
        if (titleSplit.length > 1) {
            type = titleSplit[1].trim();
            if (type.isEmpty()) {
                throw new WhatsCookingException(MESSAGE_EMPTY_TYPE);
            }
        }
        Recipe recipe = new Recipe(title, type);

        if (ingredientSplit.length > 1) {
            addIngredients(recipe, ingredientSplit[1]);
        }

        if (stepSplit.length > 1) {
            addSteps(recipe, stepSplit[1]);
        }

        if (noteSplit.length > 1) {
            recipe.setNote(parseNote(noteSplit[1]));
        }

        //Only reached if every part was valid, so an invalid half-finished recipe is never saved
        int sizeBefore = recipes.size();
        recipes.add(recipe);
        assert recipes.size() == sizeBefore + 1 : "Adding a recipe should grow the list by exactly one";

        logger.log(Level.INFO, "Added recipe \"{0}\"", title);
        ui.showMessage(getRecipeLabel(recipe) + " added!");
    }

    /**
     * Parses each comma-separated ingredient and adds it to the recipe being built.
     *
     * @param recipe the recipe being built, which has not been saved to the list yet
     * @param ingredientText the comma-separated ingredient descriptions
     * @throws WhatsCookingException if any ingredient is missing or malformed
     */
    private void addIngredients(Recipe recipe, String ingredientText) throws WhatsCookingException {
        if (ingredientText.contains(INGREDIENT_PREFIX)) {
            throw new WhatsCookingException(MESSAGE_DUPLICATE_INGREDIENTS);
        }
        //split() drops an empty item at the end, so "3 eggs, " must be caught separately
        if (ingredientText.trim().isEmpty() || ingredientText.trim().endsWith(",")) {
            throw new WhatsCookingException(MESSAGE_WRONG_INGREDIENTS);
        }

        for (String ingredient : ingredientText.split(",")) {
            String trimmed = ingredient.trim();
            if (trimmed.isEmpty()) {
                throw new WhatsCookingException(MESSAGE_WRONG_INGREDIENTS);
            }
            try {
                recipe.addIngredient(new Ingredient(trimmed));
            } catch (IllegalArgumentException e) {
                //Name the bad ingredient and pass on Ingredient's specific reason (e.g. unsupported unit)
                logger.log(Level.INFO, "Add rejected: bad ingredient \"{0}\"", trimmed);
                throw new WhatsCookingException("Invalid ingredient \"" + trimmed + "\": " + e.getMessage()
                        + System.lineSeparator() + MESSAGE_WRONG_INGREDIENTS);
            }
        }
    }

    /**
     * Adds each step in order, e.g. {@code Scramble egg st/Put egg in tortilla}.
     * Each loop takes the text up to the next " st/" as one step.
     *
     * @param recipe the recipe being built
     * @param stepText everything after the first " st/"
     * @throws WhatsCookingException if any step is empty or ingredients appear after the steps
     */
    private void addSteps(Recipe recipe, String stepText) throws WhatsCookingException {
        if (stepText.contains(INGREDIENT_PREFIX)) {
            throw new WhatsCookingException(MESSAGE_INGREDIENTS_AFTER_STEPS);
        }

        String remaining = stepText;
        boolean hasMoreSteps = true;
        while (hasMoreSteps) {
            String[] parts = remaining.split(STEP_PREFIX, 2);
            String step = parts[0].trim();
            if (step.isEmpty()) {
                throw new WhatsCookingException(MESSAGE_EMPTY_STEP);
            }
            recipe.addStep(step);

            if (parts.length > 1) {
                remaining = parts[1];
            } else {
                hasMoreSteps = false;
            }
        }
    }

    /**
     * Validates the text after " n/" and returns it trimmed.
     *
     * @param noteText everything after the first " n/"
     * @return the trimmed note
     * @throws WhatsCookingException if the note is empty, repeated, or followed by other parts
     */
    private String parseNote(String noteText) throws WhatsCookingException {
        String paddedNote = " " + noteText;
        if (paddedNote.contains(NOTE_PREFIX)) {
            throw new WhatsCookingException(MESSAGE_DUPLICATE_NOTE);
        }
        if (paddedNote.contains(STEP_PREFIX) || paddedNote.contains(INGREDIENT_PREFIX)) {
            throw new WhatsCookingException(MESSAGE_NOTE_NOT_LAST);
        }
        String note = noteText.trim();
        if (note.isEmpty()) {
            throw new WhatsCookingException(MESSAGE_EMPTY_NOTE);
        }
        return note;
    }

    /** Returns "title, type", or just "title" when no type was given. */
    private String getRecipeLabel(Recipe recipe) {
        if (recipe.getType().isEmpty()) {
            return recipe.getTitle();
        }
        return recipe.getTitle() + ", " + recipe.getType();
    }
}
