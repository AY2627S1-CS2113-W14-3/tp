package seedu.whatscooking.command;

import static seedu.whatscooking.command.RecipeInputParser.INGREDIENT_PREFIX;
import static seedu.whatscooking.command.RecipeInputParser.NOTE_PREFIX;
import static seedu.whatscooking.command.RecipeInputParser.STEP_PREFIX;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import seedu.whatscooking.AppLogger;
import seedu.whatscooking.WhatsCookingException;
import seedu.whatscooking.recipe.Ingredient;
import seedu.whatscooking.recipe.Recipe;
import seedu.whatscooking.recipe.RecipeList;
import seedu.whatscooking.ui.Ui;

/**
 * Changes the title, type, note, one ingredient and/or one step of an existing recipe, chosen by its number
 * or its full name: {@code edit INDEX|RECIPE_NAME [title/NEW_TITLE] [type/NEW_TYPE] [n/NEW_NOTE]
 * [i/NUMBER INGREDIENT] [st/NUMBER STEP]}.
 * <p>
 * The parts can be given in any order, but each at most once. Every new value
 * is checked before anything changes, so a rejected edit leaves the recipe as it was.
 */
public class EditCommand extends Command {
    static final String TITLE_PREFIX = " title/";
    static final String TYPE_PREFIX = " type/";

    private static final Logger logger = AppLogger.getLogger(EditCommand.class);
    private static final String[] PREFIXES = {TITLE_PREFIX, TYPE_PREFIX, NOTE_PREFIX, INGREDIENT_PREFIX, STEP_PREFIX};

    private static final String MESSAGE_WRONG_FORMAT =
            "WRONG FORMAT! Edit requires \"edit INDEX_OR_NAME [title/NEW_TITLE] [type/NEW_TYPE] [n/NEW_NOTE] "
                    + "[i/NUMBER INGREDIENT] [st/NUMBER STEP]\" with at least one part, "
                    + "e.g. edit 1 title/egg wrap OR edit egg taco st/2 Fold the wrap";
    private static final String MESSAGE_INVALID_INDEX =
            "The recipe number must be a positive whole number. Use view to see recipe numbers.";
    private static final String MESSAGE_REPEATED_PREFIX =
            "Each of title/, type/, n/, i/ and st/ can only be given once.";
    private static final String MESSAGE_WRONG_ITEM_FORMAT =
            "%s requires a number and the new text, e.g. %s";
    private static final String MESSAGE_ITEM_NOT_FOUND = "%s %d does not exist; this recipe has %d %s(s).";
    private static final String MESSAGE_INVALID_ITEM_NUMBER = "%s number must be a positive whole number, e.g. %s";
    private static final String MESSAGE_ONE_INGREDIENT =
            "Only one ingredient can be replaced at a time, so i/ cannot contain a comma.";
    private static final String INGREDIENT_NAME = "Ingredient";
    private static final String STEP_NAME = "Step";
    private static final String INGREDIENT_EXAMPLE = "i/2 3 eggs";
    private static final String STEP_EXAMPLE = "st/1 Whisk the eggs";
    private static final String INTEGER_PATTERN = "[+-]?[0-9]+";
    private static final String MESSAGE_DUPLICATE_TITLE =
            "A recipe called \"%s\" already exists. Choose a different title.";

    private final String arguments;

    /**
     * Creates an edit command for the given argument string.
     *
     * @param arguments everything the user typed after "edit"; never null
     */
    public EditCommand(String arguments) {
        assert arguments != null : "Parser should pass \"\" rather than null when edit has no arguments";
        this.arguments = arguments;
    }

    @Override
    public void execute(RecipeList recipes, Ui ui) throws WhatsCookingException {
        assert recipes != null : "Recipe list should never be null";
        assert ui != null : "Ui should never be null";
        logger.log(Level.FINE, "Executing edit with arguments \"{0}\"", arguments);

        //Leading space lets a prefix match even when it is the first thing typed
        String input = " " + arguments;
        int firstPrefix = findFirstPrefix(input);
        if (firstPrefix == -1 || input.substring(0, firstPrefix).isBlank()) {
            throw new WhatsCookingException(MESSAGE_WRONG_FORMAT);
        }
        //[recipe number or name, the parts to change]
        int index = findRecipeNumber(input.substring(0, firstPrefix).trim(), recipes);
        Recipe recipe = recipes.get(index - 1);
        String fields = input.substring(firstPrefix);
        String titleText = getValue(fields, TITLE_PREFIX);
        String typeText = getValue(fields, TYPE_PREFIX);
        String noteText = getValue(fields, NOTE_PREFIX);
        String ingredientText = getValue(fields, INGREDIENT_PREFIX);
        String stepText = getValue(fields, STEP_PREFIX);

        //Check every new value first and only queue the change, so a rejected edit changes nothing
        List<Runnable> changes = new ArrayList<>();
        List<String> changedItems = new ArrayList<>();
        if (titleText != null) {
            String title = parseNewTitle(titleText, recipe, recipes);
            changes.add(() -> recipe.setTitle(title));
        }
        if (typeText != null) {
            String type = RecipeInputParser.parseType(typeText);
            changes.add(() -> recipe.setType(type));
        }
        if (noteText != null) {
            String note = RecipeInputParser.parseNote(noteText);
            changes.add(() -> recipe.setNote(note));
        }
        if (ingredientText != null) {
            String[] item = splitItem(ingredientText, INGREDIENT_NAME, INGREDIENT_EXAMPLE);
            int number = parseItemNumber(item[0], recipe.getIngredients().size(), INGREDIENT_NAME,
                    INGREDIENT_EXAMPLE);
            if (item[1].contains(",")) {
                throw new WhatsCookingException(MESSAGE_ONE_INGREDIENT);
            }
            Ingredient ingredient = RecipeInputParser.parseIngredient(item[1]);
            changes.add(() -> recipe.setIngredient(number - 1, ingredient));
            changedItems.add(INGREDIENT_NAME + " " + number + ": " + ingredient.getDisplayDescription());
        }
        if (stepText != null) {
            String[] item = splitItem(stepText, STEP_NAME, STEP_EXAMPLE);
            int number = parseItemNumber(item[0], recipe.getSteps().size(), STEP_NAME, STEP_EXAMPLE);
            String step = RecipeInputParser.parseStep(item[1]);
            changes.add(() -> recipe.setStep(number - 1, step));
            changedItems.add(STEP_NAME + " " + number + ": " + step);
        }

        changes.forEach(Runnable::run);
        logger.log(Level.INFO, "Edited recipe {0}", index);
        ui.showMessage(buildResultMessage(index, recipe, changedItems));
    }

    /**
     * Returns where the first prefix starts in {@code input}, or -1 if there is none.
     */
    private int findFirstPrefix(String input) {
        int first = -1;
        for (String prefix : PREFIXES) {
            int start = input.indexOf(prefix);
            if (start != -1 && (first == -1 || start < first)) {
                first = start;
            }
        }
        return first;
    }

    /**
     * Finds the recipe the user chose, by its number from {@code view} (e.g. "3") or by its full name
     * (e.g. "egg taco"), and returns its 1-based number. Like {@code retrieve}, text that looks like a
     * whole number is always treated as a recipe number.
     *
     * @param selector the recipe number or name as typed
     * @param recipes all recipes
     * @return the chosen recipe's 1-based number
     * @throws WhatsCookingException if the number is invalid or no recipe has that number or name
     */
    private int findRecipeNumber(String selector, RecipeList recipes) throws WhatsCookingException {
        if (selector.matches(INTEGER_PATTERN)) {
            int number = parseIndex(selector);
            recipes.get(number - 1); //Throws a user-facing error if there is no such recipe
            return number;
        }
        Recipe recipe = recipes.getByName(selector);
        for (int i = 0; i < recipes.size(); i++) {
            if (recipes.get(i) == recipe) {
                return i + 1;
            }
        }
        throw new AssertionError("getByName returned a recipe that is not in the list");
    }

    /**
     * Converts the 1-based recipe number typed by the user.
     *
     * @param text the recipe number as typed
     * @return the 1-based recipe number
     * @throws WhatsCookingException if the text is not a positive whole number
     */
    private int parseIndex(String text) throws WhatsCookingException {
        return parsePositiveNumber(text, MESSAGE_INVALID_INDEX);
    }

    /**
     * Converts text such as "2" into a positive whole number.
     *
     * @param text the number as typed
     * @param errorMessage the message to show if the text is not a positive whole number
     * @return the number
     * @throws WhatsCookingException if the text is not a positive whole number
     */
    private int parsePositiveNumber(String text, String errorMessage) throws WhatsCookingException {
        if (!text.matches("[0-9]+")) {
            throw new WhatsCookingException(errorMessage);
        }
        try {
            int number = Integer.parseInt(text);
            if (number <= 0) {
                throw new WhatsCookingException(errorMessage);
            }
            return number;
        } catch (NumberFormatException e) {
            //Only reached when the number is too large to fit in an int
            throw new WhatsCookingException(errorMessage);
        }
    }

    /**
     * Checks a new title and returns it cleaned up. A new title must not belong to another recipe,
     * but changing only the letter case or spacing of the recipe's own title is allowed.
     *
     * @param text the new title as typed
     * @param recipe the recipe being edited
     * @param recipes all recipes, used to look for duplicates
     * @return the cleaned-up title
     * @throws WhatsCookingException if the title is blank or used by another recipe
     */
    private String parseNewTitle(String text, Recipe recipe, RecipeList recipes) throws WhatsCookingException {
        String title = RecipeInputParser.parseTitle(text);
        String currentTitle = recipe.getTitle().trim().replaceAll("\\s+", " ");
        boolean isSameRecipe = title.equalsIgnoreCase(currentTitle);
        if (!isSameRecipe && recipes.containsTitle(title)) {
            logger.log(Level.INFO, "Edit rejected: duplicate title \"{0}\"", title);
            throw new WhatsCookingException(String.format(MESSAGE_DUPLICATE_TITLE, title));
        }
        return title;
    }

    /**
     * Splits the text after i/ or st/, e.g. "2 3 eggs", into its number and new text: ["2", "3 eggs"].
     *
     * @throws WhatsCookingException if the number or the new text is missing
     */
    private String[] splitItem(String text, String itemName, String example) throws WhatsCookingException {
        String[] item = text.trim().split("\\s+", 2);
        if (item.length < 2 || item[0].isEmpty()) {
            throw new WhatsCookingException(String.format(MESSAGE_WRONG_ITEM_FORMAT, itemName, example));
        }
        return item;
    }

    /**
     * Converts the 1-based ingredient or step number and checks that it exists in the recipe.
     *
     * @param text the number as typed
     * @param count how many ingredients or steps the recipe has
     * @param itemName "Ingredient" or "Step", used in error messages
     * @param example an example of the correct format, used in error messages
     * @return the 1-based number
     * @throws WhatsCookingException if the number is not a positive whole number or is too large
     */
    private int parseItemNumber(String text, int count, String itemName, String example)
            throws WhatsCookingException {
        int number = parsePositiveNumber(text, String.format(MESSAGE_INVALID_ITEM_NUMBER, itemName, example));
        if (number > count) {
            throw new WhatsCookingException(String.format(MESSAGE_ITEM_NOT_FOUND, itemName, number, count,
                    itemName.toLowerCase()));
        }
        return number;
    }

    /**
     * Returns the text typed after {@code prefix}, up to the next prefix or the end of the line.
     *
     * @param fields everything after the recipe number or name, starting with a space
     * @param prefix the prefix to look for, e.g. " title/"
     * @return the text after the prefix, or null if the prefix was not given
     * @throws WhatsCookingException if a prefix is repeated, no prefix is given,
     *     or there is extra text before the first prefix
     */
    private String getValue(String fields, String prefix) throws WhatsCookingException {
        List<Integer> starts = findPrefixStarts(fields);
        if (starts.isEmpty() || !fields.substring(0, starts.get(0)).isBlank()) {
            throw new WhatsCookingException(MESSAGE_WRONG_FORMAT);
        }

        int start = fields.indexOf(prefix);
        if (start == -1) {
            return null;
        }
        int valueStart = start + prefix.length();
        int valueEnd = fields.length();
        for (int otherStart : starts) {
            if (otherStart > start) {
                valueEnd = otherStart;
                break;
            }
        }
        return fields.substring(valueStart, valueEnd);
    }

    /**
     * Returns where each prefix starts in {@code fields}, in order.
     *
     * @throws WhatsCookingException if any prefix appears more than once
     */
    private List<Integer> findPrefixStarts(String fields) throws WhatsCookingException {
        List<Integer> starts = new ArrayList<>();
        for (String prefix : PREFIXES) {
            int start = fields.indexOf(prefix);
            if (start == -1) {
                continue;
            }
            if (fields.indexOf(prefix, start + 1) != -1) {
                throw new WhatsCookingException(MESSAGE_REPEATED_PREFIX);
            }
            starts.add(start);
        }
        starts.sort(Comparator.naturalOrder());
        return starts;
    }

    /** Returns the confirmation shown after a successful edit, listing any replaced ingredient or step. */
    private String buildResultMessage(int index, Recipe recipe, List<String> changedItems) {
        String message = "Updated recipe " + index + ":" + System.lineSeparator() + recipe;
        if (!recipe.getNote().isEmpty()) {
            message += System.lineSeparator() + "Note: " + recipe.getNote();
        }
        for (String changedItem : changedItems) {
            message += System.lineSeparator() + changedItem;
        }
        return message;
    }
}
