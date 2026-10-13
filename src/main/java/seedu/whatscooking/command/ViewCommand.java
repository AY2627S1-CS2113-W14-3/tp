package seedu.whatscooking.command;

import seedu.whatscooking.WhatsCookingException;
import seedu.whatscooking.recipe.RecipeList;
import seedu.whatscooking.ui.Ui;

/**
 * Lists every recipe added so far, showing each one's name, type and serving
 * size. Takes no arguments.
 */
public class ViewCommand extends Command {
    @Override
    public void execute(RecipeList recipes, Ui ui) throws WhatsCookingException {
        ui.listRecipes(recipes);
    }
}
