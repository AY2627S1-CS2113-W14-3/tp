package seedu.whatscooking.command;

import seedu.whatscooking.recipe.Ingredient;
import seedu.whatscooking.recipe.Recipe;
import seedu.whatscooking.recipe.RecipeList;
import seedu.whatscooking.ui.Ui;
/**
 * Adds a new recipe to the list.
 * <p>
 * TODO (Person B): parse {@code arguments} into a title, type, ingredients
 * and steps; build a {@code Recipe}; add it via {@code recipes.add(recipe)};
 * confirm with {@code ui.showMessage(...)}.
 */
public class AddCommand extends Command {
    //input prefixes to identify start of input for the different variables
    private static final String INGREDIENT_PREFIX = "i/";
    private static final String STEP_PREFIX =" st/";

    //standard prefix format for error messages for add command
    private static final String MESSAGE_WRONG_FORMAT =
            "WRONG FORMAT! Add requires "
            + "\"add RECIPE_TITLE[, TYPE] [i/INGREDIENTS] [st/STEP]...\" "
            + "e.g. add egg taco, snack i/3 eggs, 5 tortillas st/Scramble egg st/Put egg in tortilla";

    private static final String MESSAGE_WRONG_INGREDIENTS =
            "Wrong format! Ingredients require "
            + "\"QUANTITY INGREDIENT, QUANTITY INGREDIENT\" e.g. 3 eggs, 5 tortillas";

    private static final String MESSAGE_EMPTY_STEP = "A step cannot be empty. e.g. Scramble egg in pan until yellow";

    private final String arguments;

    public AddCommand(String arguments) {
        this.arguments = arguments;
    }

    @Override
    public void execute(RecipeList recipes, Ui ui) {
        //Let variable prefix match even when they are the first
        String input = " " + arguments;

        //Cut off everything from the first " st/" onwards: [before steps, steps]
        String[] stepSplit = input.split(STEP_PREFIX, 2);
        //Cut off the ingredients: [title and type, ingredient]
        String[] ingredientSplit = stepSplit[0].split(INGREDIENT_PREFIX, 2);
        //Split the title from the typer at the first comma: [title, type]
        String[] titleSplit = ingredientSplit[0].split(",", 2);

        String title = titleSplit[0].trim();
        if (title.isEmpty()) {
            ui.showError(MESSAGE_WRONG_FORMAT);
            return;
        }

        String type = "";
        if (titleSplit.length > 1) {
            type = titleSplit[1].trim();
        }
        Recipe recipe = new Recipe(title, type);

        if (ingredientSplit.length > 1) {
            boolean isValid = addIngredients(recipe, ingredientSplit[1]);
            if (!isValid) {
                ui.showError(MESSAGE_WRONG_INGREDIENTS);
                return;
            }
        }

        if (stepSplit.length > 1) {
            boolean isValid = addSteps(recipe, stepSplit[1]);
            if (!isValid) {
                ui.showError(MESSAGE_EMPTY_STEP);
                return;
            }
        }

        //Only reached if every part was valid, so a invalid half-finished recipe is never saved

        recipes.add(recipe);
        ui.showMessage(getRecipeLabel(recipe) + " added!");
    }

    /**
     * Parses each comma-separated ingredient and adds it to the recipe being built.
     * Returns false for invalid input so the caller reports an error without saving the recipe.
     *
     * @param recipe the recipe being built, which has not been saved to the list yet
     * @param ingredientText the comma-separated ingredient descriptions
     * @return true if every ingredient is valid
     */
    private boolean addIngredients(Recipe recipe, String ingredientText) {
        //split() drops an empty item at the end so "3 eggs, " must be caught separately
        if (ingredientText.trim().isEmpty() || ingredientText.trim().endsWith(",")) {
            return false;
        }

        String[] ingredients = ingredientText.split(",");
        for (String ingredient: ingredients) {
            String trimmed = ingredient.trim();
            if (trimmed.isEmpty()) {
                return false;
            }
            try {
                recipe.addIngredient(new Ingredient(trimmed));
            } catch (IllegalArgumentException e) {
                return false;
            }
        }
        return true;
    }

    //Add steps one at a time,e.g. Scramble egg st/Put egg in tortilla
    //Each loop akes the text up to the next " st/" as one step
    //Return true if every step was valid, false is any was empty

    private boolean addSteps(Recipe recipe, String stepText) {
        String remaining = stepText;
        boolean hasMoreSteps = true;

        while (hasMoreSteps) {
            String[] parts = remaining.split(STEP_PREFIX, 2);
            String step = parts[0].trim();
            if (step.isEmpty()) {
                return false;
            }
            recipe.addStep(step);

            if (parts.length > 1) {
                remaining = parts[1];
            } else {
                hasMoreSteps = false;
            }
        }
        return true;
    }

    //Return "title, type", or just "title" when no type was given

    private String getRecipeLabel(Recipe recipe) {
        if (recipe.getType().isEmpty()) {
            return recipe.getTitle();
        }
        return recipe.getTitle() + ", " + recipe.getType();
    }
}
