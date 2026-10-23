package seedu.whatscooking.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.whatscooking.WhatsCookingException;
import seedu.whatscooking.recipe.Recipe;
import seedu.whatscooking.recipe.RecipeList;
import seedu.whatscooking.ui.Ui;

/** Checks the agreed serving-size contract at the boundary between add and modify. */
class ServingSizeCompatibilityTest {
    @Test
    void execute_addThenModify_scalesFromTheSpecifiedInitialSize() throws WhatsCookingException {
        RecipeList recipes = new RecipeList();
        Ui ui = new Ui();
        new AddCommand("Rice s/2 i/100 g rice").execute(recipes, ui);
        Recipe recipe = recipes.get(0);
        assertEquals(2, recipe.getServingSize());
        assertEquals("100 g rice", recipe.getIngredients().get(0).getDescription());

        new ModifyCommand("Rice s/4").execute(recipes, ui);
        assertEquals(4, recipe.getServingSize());
        assertEquals("200 g rice", recipe.getIngredients().get(0).getDescription());
    }

    @Test
    void execute_invalidServingSizes_bothCommandsRejectWithoutChangingRecipes() throws WhatsCookingException {
        String[] invalidSizes = {"0", "-1", "1.5", "+2", "two", "1e2", "2147483648", "9".repeat(100)};
        for (String value : invalidSizes) {
            RecipeList recipes = new RecipeList();
            Ui ui = new Ui();
            new AddCommand("Rice s/2 i/100 g rice").execute(recipes, ui);

            assertThrows(WhatsCookingException.class,
                    () -> new AddCommand("Other s/" + value).execute(recipes, ui), value);
            assertThrows(WhatsCookingException.class,
                    () -> new ModifyCommand("Rice s/" + value).execute(recipes, ui), value);
            assertEquals(1, recipes.size(), value);
            assertEquals(2, recipes.get(0).getServingSize(), value);
            assertEquals("100 g rice", recipes.get(0).getIngredients().get(0).getDescription(), value);
        }
    }

    @Test
    void execute_positiveIntBoundary_bothCommandsAccept() throws WhatsCookingException {
        RecipeList recipes = new RecipeList();
        Ui ui = new Ui();
        new AddCommand("Empty s/2147483647").execute(recipes, ui);
        assertEquals(Integer.MAX_VALUE, recipes.get(0).getServingSize());

        new ModifyCommand("Empty s/1").execute(recipes, ui);
        new ModifyCommand("Empty s/2147483647").execute(recipes, ui);
        assertEquals(Integer.MAX_VALUE, recipes.get(0).getServingSize());
    }
}
