package seedu.whatscooking.command;

import java.util.ArrayList;

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

    public ArrayList<Integer> internalExecute(RecipeList recipes) throws WhatsCookingException {
        ArrayList<Integer> matches = new ArrayList<>();

        for(int i = 0; i < recipes.size(); i++) {
            if (recipes.get(i).getTitle().contains(arguments)) {
                matches.add(i);
            }
        }
        return matches;
    }

    @Override
    public void execute(RecipeList recipes, Ui ui) throws WhatsCookingException {
        ArrayList<Integer> out = internalExecute(recipes);
        ui.showMessage("Found " + out.size() + " Recipes: ");
        for (int i = 0; i < out.size(); i++) {

            ui.showMessage(recipes.get(out.get(i)).toString());
        }
    }
}
