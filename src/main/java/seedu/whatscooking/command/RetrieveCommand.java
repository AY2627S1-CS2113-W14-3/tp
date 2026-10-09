package seedu.whatscooking.command;

import seedu.whatscooking.WhatsCookingException;
import seedu.whatscooking.recipe.Recipe;
import seedu.whatscooking.recipe.RecipeList;
import seedu.whatscooking.ui.Ui;

/**
 * Retrieves and displays one recipe by its 1-based position in the list.
 * <p>
 * TODO (Person C): parse {@code arguments} into an index; call
 * {@code ui.showRecipe(recipes, index)} (handles bounds-checking and
 * 1-based-to-0-based conversion already).
 */
public class RetrieveCommand extends Command {
    private final String arguments;

    public RetrieveCommand(String arguments) {
        this.arguments = arguments;
    }

    @Override
    public void execute(RecipeList recipes, Ui ui) throws WhatsCookingException {
        String input = arguments.trim();

        if (input.isEmpty()) {
            throw new WhatsCookingException("Please enter a recipe number or recipe name.");
        }

        if (isInteger(input)) {

            int index = Integer.parseInt(input);
            if (index <= 0) {
                throw new WhatsCookingException("Recipe number must be positive.");
            }
            ui.showRecipe(recipes, index);
            return;
        }
        Recipe recipe = recipes.getByName(input);
        ui.showRecipe(recipe);
    }

    /**
     * Returns true if the given string consists only of digits.
     *
     * @param s the string to check
     * @return true if s is non-empty and all characters are digits
     */
    private boolean isInteger(String s) {
        if (s.isEmpty()) {
            return false;
        }
        for (int i = 0; i < s.length(); i++) {
            if (!Character.isDigit(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}