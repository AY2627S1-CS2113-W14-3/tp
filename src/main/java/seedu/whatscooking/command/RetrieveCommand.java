package seedu.whatscooking.command;

import seedu.whatscooking.WhatsCookingException;
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
        throw new WhatsCookingException("retrieve command not yet implemented");
    }
}
