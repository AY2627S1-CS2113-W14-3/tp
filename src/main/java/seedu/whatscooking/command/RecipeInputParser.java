package seedu.whatscooking.command;

import java.util.logging.Level;
import java.util.logging.Logger;

import seedu.whatscooking.AppLogger;
import seedu.whatscooking.WhatsCookingException;
import seedu.whatscooking.recipe.Ingredient;
import seedu.whatscooking.recipe.Recipe;

/**
 * Checks and converts the parts of a recipe that users type, such as the title,
 * type, serving size, ingredients, steps and note.
 * <p>
 * Shared by {@link AddCommand} and {@link EditCommand} so that both commands
 * accept exactly the same input and show the same error messages.
 */
final class RecipeInputParser {
    //Each prefix starts with a space so that "i/" inside a word (e.g. "wasabi/") is not matched
    static final String SERVINGS_PREFIX = " s/";
    static final String INGREDIENT_PREFIX = " i/";
    static final String STEP_PREFIX = " st/";
    static final String NOTE_PREFIX = " n/";

    static final String MESSAGE_EMPTY_TITLE = "A recipe title cannot be empty. e.g. egg taco";
    static final String MESSAGE_EMPTY_TYPE = "The type cannot be empty. e.g. snack";
    static final String MESSAGE_INVALID_SERVINGS = "Serving size (s/) must be a positive whole number. e.g. s/2";
    static final String MESSAGE_WRONG_INGREDIENTS =
            "Wrong format! Ingredients require "
                    + "\"QUANTITY INGREDIENT, QUANTITY INGREDIENT\" e.g. 3 eggs, 5 tortillas";
    static final String MESSAGE_DUPLICATE_INGREDIENTS =
            "Only one i/ is allowed. List all ingredients after it, separated by commas.";
    static final String MESSAGE_INGREDIENTS_AFTER_STEPS = "Ingredients (i/) must come before steps (st/).";
    static final String MESSAGE_EMPTY_STEP = "A step cannot be empty. e.g. Scramble egg in pan until yellow";
    static final String MESSAGE_EMPTY_NOTE = "A note cannot be empty. e.g. n/Best eaten hot";

    private static final Logger logger = AppLogger.getLogger(RecipeInputParser.class);
    private static final String WHOLE_NUMBER_PATTERN = "[0-9]+";

    private RecipeInputParser() {
        // Utility class: all methods are static.
    }

    /**
     * Returns the trimmed title.
     *
     * @param text the title as typed
     * @return the trimmed title
     * @throws WhatsCookingException if the title is blank
     */
    static String parseTitle(String text) throws WhatsCookingException {
        assert text != null : "Callers should pass \"\" rather than null";
        String title = text.trim();
        if (title.isEmpty()) {
            throw new WhatsCookingException(MESSAGE_EMPTY_TITLE);
        }
        return title;
    }

    /**
     * Returns the trimmed type.
     *
     * @param text the type as typed
     * @return the trimmed type
     * @throws WhatsCookingException if the type is blank
     */
    static String parseType(String text) throws WhatsCookingException {
        assert text != null : "Callers should pass \"\" rather than null";
        String type = text.trim();
        if (type.isEmpty()) {
            throw new WhatsCookingException(MESSAGE_EMPTY_TYPE);
        }
        return type;
    }

    /**
     * Converts the text after {@code s/} into a positive serving size.
     *
     * @param text the serving size as typed
     * @return the serving size
     * @throws WhatsCookingException if the text is not a positive whole number
     */
    static int parseServingSize(String text) throws WhatsCookingException {
        assert text != null : "Callers should pass \"\" rather than null";
        String trimmed = text.trim();
        if (!trimmed.matches(WHOLE_NUMBER_PATTERN)) {
            throw new WhatsCookingException(MESSAGE_INVALID_SERVINGS);
        }
        try {
            int servingSize = Integer.parseInt(trimmed);
            if (servingSize <= 0) {
                throw new WhatsCookingException(MESSAGE_INVALID_SERVINGS);
            }
            return servingSize;
        } catch (NumberFormatException e) {
            //Only reached when the number is too large to fit in an int
            throw new WhatsCookingException(MESSAGE_INVALID_SERVINGS);
        }
    }

    /**
     * Parses each comma-separated ingredient and adds it to the recipe.
     *
     * @param recipe the recipe being built
     * @param ingredientText the comma-separated ingredients, e.g. "3 eggs, 5 tortillas"
     * @throws WhatsCookingException if any ingredient is missing or malformed
     */
    static void addIngredients(Recipe recipe, String ingredientText) throws WhatsCookingException {
        assert recipe != null : "Recipe should not be null";
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
                logger.log(Level.INFO, "Rejected bad ingredient \"{0}\"", trimmed);
                throw new WhatsCookingException("Invalid ingredient \"" + trimmed + "\": " + e.getMessage()
                        + System.lineSeparator() + MESSAGE_WRONG_INGREDIENTS);
            }
        }
    }

    /**
     * Adds each step in order, e.g. {@code Scramble egg st/Put egg in tortilla}.
     *
     * @param recipe the recipe being built
     * @param stepText everything after the first " st/"
     * @throws WhatsCookingException if any step is empty or ingredients appear after the steps
     */
    static void addSteps(Recipe recipe, String stepText) throws WhatsCookingException {
        assert recipe != null : "Recipe should not be null";
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
     * Returns the trimmed note.
     *
     * @param text the note as typed
     * @return the trimmed note
     * @throws WhatsCookingException if the note is blank
     */
    static String parseNote(String text) throws WhatsCookingException {
        assert text != null : "Callers should pass \"\" rather than null";
        String note = text.trim();
        if (note.isEmpty()) {
            throw new WhatsCookingException(MESSAGE_EMPTY_NOTE);
        }
        return note;
    }
}
