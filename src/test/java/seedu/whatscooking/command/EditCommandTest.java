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
        assertEditRejected("1 title/2026");
        assertEditRejected("1 n/");
        assertEditRejected("1 type/lunch n/");
    }

    @Test
    public void execute_repeatedPrefix_throwsAndChangesNothing() {
        assertEditRejected("1 title/a title/b");
        assertEditRejected("1 n/a type/lunch n/b");
    }

    @Test
    public void execute_ingredientAndStep_replacesOnlyThoseItems() throws WhatsCookingException {
        RecipeList recipes = createRecipeWithItems();
        new EditCommand("1 i/2 5 corn tortillas st/1 Scramble the eggs").execute(recipes, new Ui());

        Recipe recipe = recipes.get(0);
        assertEquals("3 eggs", recipe.getIngredients().get(0).getDescription());
        assertEquals("5 corn tortillas", recipe.getIngredients().get(1).getDescription());
        assertEquals("Scramble the eggs", recipe.getSteps().get(0));
        assertEquals("Wrap", recipe.getSteps().get(1));
        assertEquals("taco", recipe.getTitle());
    }

    @Test
    public void execute_stepWithTitleInAnyOrder_changesBoth() throws WhatsCookingException {
        RecipeList recipes = createRecipeWithItems();
        new EditCommand("1 st/2 Fold and serve title/egg wrap").execute(recipes, new Ui());

        Recipe recipe = recipes.get(0);
        assertEquals("egg wrap", recipe.getTitle());
        assertEquals("Fold and serve", recipe.getSteps().get(1));
    }

    @Test
    public void execute_invalidIngredientOrStep_throwsAndChangesNothing() {
        assertItemEditRejected("1 i/0 2 eggs");
        assertItemEditRejected("1 i/3 2 eggs");
        assertItemEditRejected("1 i/abc 2 eggs");
        assertItemEditRejected("1 i/2");
        assertItemEditRejected("1 i/1 2 eggs, 3 milk");
        assertItemEditRejected("1 i/1 two eggs");
        assertItemEditRejected("1 st/3 Serve");
        assertItemEditRejected("1 st/1   ");
        assertItemEditRejected("1 i/1 2 eggs i/2 3 tortillas");
        assertItemEditRejected("1 st/1 Whisk type/light snack");
    }

    @Test
    public void execute_byName_editsThatRecipe() throws WhatsCookingException {
        Recipe recipe = runEdit("PANCAKES type/dessert n/Add syrup").get(1);
        assertEquals("dessert", recipe.getType());
        assertEquals("Add syrup", recipe.getNote());
    }

    @Test
    public void execute_byNameWithExtraSpaces_editsThatRecipe() throws WhatsCookingException {
        assertEquals("egg wrap", runEdit("  egg   TACO  title/egg wrap").get(0).getTitle());
    }

    @Test
    public void execute_unknownNameOrBadNumber_throwsAndChangesNothing() {
        assertEditRejected("waffles type/main");
        assertEditRejected("egg type/main");
        assertEditRejected("-1 type/main");
    }

    @Test
    public void execute_titleDifferingOnlyInSpaces_caughtAsDuplicate() {
        assertEditRejected("2 title/egg   TACO");
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

    /** Returns a list holding one recipe "taco" with ingredients 3 eggs, 4 tortillas and steps Scramble, Wrap. */
    private static RecipeList createRecipeWithItems() throws WhatsCookingException {
        RecipeList recipes = new RecipeList();
        new AddCommand("taco, snack i/3 eggs, 4 tortillas st/Scramble st/Wrap").execute(recipes, new Ui());
        return recipes;
    }

    /**
     * Asserts that {@code edit <arguments>} throws and leaves the recipe from
     * {@link #createRecipeWithItems()} unchanged.
     *
     * @param arguments the argument string to reject, also used as the failure label
     */
    private static void assertItemEditRejected(String arguments) {
        try {
            RecipeList recipes = createRecipeWithItems();
            EditCommand command = new EditCommand(arguments);
            assertThrows(WhatsCookingException.class, () -> command.execute(recipes, new Ui()), arguments);

            Recipe recipe = recipes.get(0);
            assertEquals("taco", recipe.getTitle(), arguments);
            assertEquals("snack", recipe.getType(), arguments);
            assertEquals("3 eggs", recipe.getIngredients().get(0).getDescription(), arguments);
            assertEquals("4 tortillas", recipe.getIngredients().get(1).getDescription(), arguments);
            assertEquals("Scramble", recipe.getSteps().get(0), arguments);
            assertEquals("Wrap", recipe.getSteps().get(1), arguments);
        } catch (WhatsCookingException e) {
            throw new AssertionError("Starting recipe could not be created", e);
        }
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
