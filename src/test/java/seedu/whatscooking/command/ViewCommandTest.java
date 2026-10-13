package seedu.whatscooking.command;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import seedu.whatscooking.WhatsCookingException;
import seedu.whatscooking.recipe.Recipe;
import seedu.whatscooking.recipe.RecipeList;
import seedu.whatscooking.ui.Ui;

/**
 * Tests for {@link ViewCommand}: it must list every recipe without adding,
 * removing or reordering any of them.
 */
class ViewCommandTest {
    @Test
    public void execute_populatedList_leavesRecipesUnchanged() throws WhatsCookingException {
        RecipeList recipes = new RecipeList();
        recipes.add(new Recipe("Pancakes", "breakfast"));
        recipes.add(new Recipe("Egg Fried Rice", "dinner"));

        new ViewCommand().execute(recipes, new Ui());

        // input:    view run against a list holding 2 recipes
        // expected: list is untouched, same recipes in the same order
        assertEquals(2, recipes.size());
        assertEquals("Pancakes", recipes.get(0).getTitle());
        assertEquals("Egg Fried Rice", recipes.get(1).getTitle());
    }

    @Test
    public void execute_emptyList_doesNotThrow() throws WhatsCookingException {
        // input:    view run against an empty list
        // expected: no exception, e.g. Ui.listRecipes's "No recipes found." path
        new ViewCommand().execute(new RecipeList(), new Ui());
    }
}
