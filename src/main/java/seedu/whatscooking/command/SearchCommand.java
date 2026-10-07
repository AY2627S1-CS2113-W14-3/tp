package seedu.whatscooking.command;

import seedu.whatscooking.WhatsCookingException;
import seedu.whatscooking.recipe.RecipeList;
import seedu.whatscooking.ui.Ui;

/**
 * Searches recipes by name and displays the matches.
 * <p>
 * TODO (Person D): treat {@code arguments} as a keyword; loop over
 * {@code recipes} collecting titles that match into an
 * {@code ArrayList<Recipe>}; display with {@code ui.showRecipes(matches)}.
 */
public class SearchCommand extends Command {
    private final String arguments;

    public SearchCommand(String arguments) {
        this.arguments = arguments;
    }

    @Override
    public void execute(RecipeList recipes, Ui ui) throws WhatsCookingException {
        throw new WhatsCookingException("search command not yet implemented");
    }
}
