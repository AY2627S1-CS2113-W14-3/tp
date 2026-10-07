package seedu.whatscooking.command;

import seedu.whatscooking.WhatsCookingException;
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
    private final String arguments;

    public AddCommand(String arguments) {
        this.arguments = arguments;
    }

    @Override
    public void execute(RecipeList recipes, Ui ui) throws WhatsCookingException {
        throw new WhatsCookingException("add command not yet implemented");
    }
}
