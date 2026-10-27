package seedu.whatscooking.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.whatscooking.WhatsCookingException;
import seedu.whatscooking.recipe.Recipe;
import seedu.whatscooking.recipe.RecipeList;
import seedu.whatscooking.ui.Ui;

/**
 * Tests for {@link EditCommand}. Each test starts from two recipes:
 * 1. "egg taco" (snack, note "Serve hot") and 2. "pancakes" (breakfast, no note).
 */
class EditCommandTest {
    @Test
    public void execute_titleOnly_changesTitleOnly() throws WhatsCookingException {
        RecipeList recipes = runEdit("1 title/egg wrap");
        Recipe recipe = recipes.get(0);
        assertEquals("egg wrap", recipe.getTitle());
        assertEquals("snack", recipe.getType());
        assertEquals("Serve hot", recipe.getNote());
    }

    @Test
    public void execute_allPartsInAnyOrder_changesAll() throws WhatsCookingException {
        Recipe recipe = runEdit("2 n/ Add syrup  type/dessert title/ fluffy pancakes ").get(1);
        assertEquals("fluffy pancakes", recipe.getTitle());
        assertEquals("dessert", recipe.getType());
        assertEquals("Add syrup", recipe.getNote());
    }

    @Test
    public void execute_sameTitleDifferentCase_allowed() throws WhatsCookingException {
        assertEquals("Egg Taco", runEdit("1 title/Egg Taco").get(0).getTitle());
    }

    @Test
    public void execute_editKeepsIngredientsAndServings() throws WhatsCookingException {
        Recipe recipe = runEdit("1 type/lunch").get(0);
        assertEquals(1, recipe.getIngredients().size());
        assertEquals(2, recipe.getServingSize());
    }

    @Test
    public void execute_titleUsedByOtherRecipe_throwsAndChangesNothing() {
        assertEditRejected("1 title/PANCAKES");
    }

    @Test
    public void execute_invalidIndex_throwsAndChangesNothing() {
        assertEditRejected("0 title/egg wrap");
        assertEditRejected("3 title/egg wrap");
        assertEditRejected("abc title/egg wrap");
        assertEditRejected("99999999999 title/egg wrap");
    }

    @Test
    public void execute_wrongFormat_throwsAndChangesNothing() {
        assertEditRejected("");
        assertEditRejected("1");
        assertEditRejected("1 egg wrap");
        assertEditRejected("1 hello title/egg wrap");
        assertEditRejected("title/egg wrap");
    }

    @Test
    public void execute_emptyValue_throwsAndChangesNothing() {
        assertEditRejected("1 title/");
        assertEditRejected("1 type/   ");
        assertEditRejected("1 n/");
        assertEditRejected("1 type/lunch n/");
    }

    @Test
    public void execute_repeatedPrefix_throwsAndChangesNothing() {
        assertEditRejected("1 title/a title/b");
        assertEditRejected("1 n/a type/lunch n/b");
    }

    @Test
    public void parse_editCommand_returnsEditCommand() throws WhatsCookingException {
        assertInstanceOf(EditCommand.class, Parser.parse("edit 1 title/egg wrap"));
    }

    /** Returns a list holding the two starting recipes described in the class comment. */
    private static RecipeList createRecipes() throws WhatsCookingException {
        RecipeList recipes = new RecipeList();
        new AddCommand("egg taco, snack s/2 i/3 eggs n/Serve hot").execute(recipes, new Ui());
        new AddCommand("pancakes, breakfast").execute(recipes, new Ui());
        return recipes;
    }

    /** Runs {@code edit <arguments>} on the starting recipes and returns the list. */
    private static RecipeList runEdit(String arguments) throws WhatsCookingException {
        RecipeList recipes = createRecipes();
        new EditCommand(arguments).execute(recipes, new Ui());
        return recipes;
    }

    /**
     * Asserts that {@code edit <arguments>} throws and leaves both starting recipes unchanged.
     *
     * @param arguments the argument string to reject, also used as the failure label
     */
    private static void assertEditRejected(String arguments) {
        try {
            RecipeList recipes = createRecipes();
            EditCommand command = new EditCommand(arguments);
            assertThrows(WhatsCookingException.class, () -> command.execute(recipes, new Ui()), arguments);

            Recipe first = recipes.get(0);
            assertEquals("egg taco", first.getTitle(), arguments);
            assertEquals("snack", first.getType(), arguments);
            assertEquals("Serve hot", first.getNote(), arguments);
            assertEquals("pancakes", recipes.get(1).getTitle(), arguments);
        } catch (WhatsCookingException e) {
            throw new AssertionError("Starting recipes could not be created", e);
        }
    }
}
