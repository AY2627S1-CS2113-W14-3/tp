package seedu.whatscooking.command;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import seedu.whatscooking.WhatsCookingException;
import seedu.whatscooking.recipe.Recipe;
import seedu.whatscooking.recipe.RecipeList;
import seedu.whatscooking.ui.Ui;

public class SearchTest {
    @Test 
    public void testSearchCommandTrue() throws WhatsCookingException {
        Recipe r1 = new Recipe("Water", "drink");
        RecipeList list = new RecipeList();
        list.add(r1);

        SearchCommand search = new SearchCommand("Water");
        assertTrue(search.internalExecute(list).size() == 1);
    }

    @Test 
    public void testSearchCommandFail() throws WhatsCookingException {
        Recipe r1 = new Recipe("Water", "drink");
        RecipeList list = new RecipeList();
        list.add(r1);

        SearchCommand search = new SearchCommand("Juice");
        assertTrue(search.internalExecute(list).size() == 0);
    }
}
