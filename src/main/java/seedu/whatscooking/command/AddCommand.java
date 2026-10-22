package seedu.whatscooking.command;

import static seedu.whatscooking.command.RecipeInputParser.INGREDIENT_PREFIX;
import static seedu.whatscooking.command.RecipeInputParser.NOTE_PREFIX;
import static seedu.whatscooking.command.RecipeInputParser.SERVINGS_PREFIX;
import static seedu.whatscooking.command.RecipeInputParser.STEP_PREFIX;

import java.util.logging.Level;
import java.util.logging.Logger;

import seedu.whatscooking.AppLogger;
import seedu.whatscooking.WhatsCookingException;
import seedu.whatscooking.recipe.Recipe;
import seedu.whatscooking.recipe.RecipeList;
import seedu.whatscooking.ui.Ui;

/**
 * Adds a new recipe to the list from one line of input:
 * {@code add TITLE[, TYPE] [s/SERVINGS] [i/INGREDIENTS] [st/STEP]... [n/NOTE]}.
 * <p>
 * The whole line is validated before anything is saved, so a malformed
 * command never leaves a half-built recipe in the list.
 */
public class AddCommand extends Command {
    private static final Logger logger = AppLogger.getLogger(AddCommand.class);

    private static final String MESSAGE_WRONG_FORMAT =
            "WRONG FORMAT! Add requires "
                    + "\"add RECIPE_TITLE[, TYPE] [s/SERVINGS] [i/INGREDIENTS] [st/STEP]... [n/NOTE]\" "
                    + "e.g. add egg taco, snack s/2 i/3 eggs, 5 tortillas st/Scramble egg st/Put egg in tortilla";
    private static final String MESSAGE_DUPLICATE_TITLE =
            "A recipe called \"%s\" already exists. Use a different title, or edit the existing recipe.";
    private static final String MESSAGE_DUPLICATE_SERVINGS = "Only one s/ is allowed per recipe.";
    private static final String MESSAGE_SERVINGS_NOT_AFTER_TITLE =
            "The serving size (s/) must come right after the title and type, before i/, st/ and n/.";
    private static final String MESSAGE_DUPLICATE_NOTE = "Only one n/ is allowed per recipe.";
    private static final String MESSAGE_NOTE_NOT_LAST =
            "The note (n/) must come last, after all ingredients and steps.";

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
        //Cut off the ingredients: [title, type and servings, ingredients]
        String[] ingredientSplit = stepSplit[0].split(INGREDIENT_PREFIX, 2);
        //Cut off the serving size: [title and type, servings]
        String[] servingsSplit = ingredientSplit[0].split(SERVINGS_PREFIX, 2);
        //Split the title from the type at the first comma: [title, type]
        String[] titleSplit = servingsSplit[0].split(",", 2);

        if (titleSplit[0].isBlank()) {
            logger.log(Level.INFO, "Add rejected: missing title");
            throw new WhatsCookingException(MESSAGE_WRONG_FORMAT);
        }
        String title = RecipeInputParser.parseTitle(titleSplit[0]);
        if (recipes.containsTitle(title)) {
            logger.log(Level.INFO, "Add rejected: duplicate title \"{0}\"", title);
            throw new WhatsCookingException(String.format(MESSAGE_DUPLICATE_TITLE, title));
        }

        String type = "";
        if (titleSplit.length > 1) {
            type = RecipeInputParser.parseType(titleSplit[1]);
        }
        Recipe recipe = new Recipe(title, type);

        if (servingsSplit.length > 1) {
            recipe.setServingSize(parseServings(servingsSplit[1]));
        }

        if (ingredientSplit.length > 1) {
            rejectMisplacedServings(ingredientSplit[1]);
            RecipeInputParser.addIngredients(recipe, ingredientSplit[1]);
        }

        if (stepSplit.length > 1) {
            rejectMisplacedServings(stepSplit[1]);
            RecipeInputParser.addSteps(recipe, stepSplit[1]);
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
     * Returns the serving size typed after {@code s/}.
     *
     * @param servingsText everything after the first " s/" and before " i/"
     * @return the positive serving size
     * @throws WhatsCookingException if s/ is repeated or the value is not a positive whole number
     */
    private int parseServings(String servingsText) throws WhatsCookingException {
        if ((" " + servingsText).contains(SERVINGS_PREFIX)) {
            throw new WhatsCookingException(MESSAGE_DUPLICATE_SERVINGS);
        }
        return RecipeInputParser.parseServingSize(servingsText);
    }

    /**
     * Rejects an {@code s/} typed after the ingredients or steps have started.
     *
     * @param text the ingredient or step text to check
     * @throws WhatsCookingException if the text contains " s/"
     */
    private void rejectMisplacedServings(String text) throws WhatsCookingException {
        if (text.contains(SERVINGS_PREFIX)) {
            throw new WhatsCookingException(MESSAGE_SERVINGS_NOT_AFTER_TITLE);
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
        if (paddedNote.contains(STEP_PREFIX) || paddedNote.contains(INGREDIENT_PREFIX)
                || paddedNote.contains(SERVINGS_PREFIX)) {
            throw new WhatsCookingException(MESSAGE_NOTE_NOT_LAST);
        }
        return RecipeInputParser.parseNote(noteText);
    }

    /** Returns "title, type", or just "title" when no type was given. */
    private String getRecipeLabel(Recipe recipe) {
        if (recipe.getType().isEmpty()) {
            return recipe.getTitle();
        }
        return recipe.getTitle() + ", " + recipe.getType();
    }
}
