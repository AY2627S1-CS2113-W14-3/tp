package seedu.whatscooking.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.whatscooking.WhatsCookingException;
import seedu.whatscooking.recipe.Recipe;
import seedu.whatscooking.recipe.RecipeList;
import seedu.whatscooking.ui.Ui;

/**
 * Tests for {@link AddCommand}. Each test runs one add and then checks the recipe list:
 * valid input adds exactly one recipe, invalid input throws
 * {@link WhatsCookingException} and leaves the list empty.
 */
class AddCommandTest {
    @Test
    public void execute_titleOnly_noTypeIngredientsOrSteps() throws WhatsCookingException {
        RecipeList recipes = runAdd("egg taco");
        assertEquals(1, recipes.size());

        Recipe recipe = recipes.get(0);
        assertEquals("egg taco", recipe.getTitle());
        assertEquals("", recipe.getType());
        assertEquals(0, recipe.getIngredients().size());
        assertEquals(0, recipe.getSteps().size());
    }

    @Test
    public void execute_titleAndType_setsType() throws WhatsCookingException {
        Recipe recipe = runAdd("egg taco, snack").get(0);
        assertEquals("egg taco", recipe.getTitle());
        assertEquals("snack", recipe.getType());
    }

    @Test
    public void execute_fullLine_parsesAllParts() throws WhatsCookingException {
        Recipe recipe = runAdd("egg taco, snack i/3 eggs, 5 tortillas st/Scramble egg st/Put egg in tortilla").get(0);
        assertEquals("egg taco", recipe.getTitle());
        assertEquals("snack", recipe.getType());

        assertEquals(2, recipe.getIngredients().size());
        assertEquals("3 eggs", recipe.getIngredients().get(0).getDescription());
        assertEquals("5 tortillas", recipe.getIngredients().get(1).getDescription());

        assertEquals(2, recipe.getSteps().size());
        assertEquals("Scramble egg", recipe.getSteps().get(0));
        assertEquals("Put egg in tortilla", recipe.getSteps().get(1));
    }

    @Test
    public void execute_extraSpaces_trimsEachPart() throws WhatsCookingException {
        Recipe recipe = runAdd("  egg taco ,  snack  i/ 3 eggs ,5 tortillas  st/  Scramble egg ").get(0);
        assertEquals("egg taco", recipe.getTitle());
        assertEquals("snack", recipe.getType());
        assertEquals("3 eggs", recipe.getIngredients().get(0).getDescription());
        assertEquals("5 tortillas", recipe.getIngredients().get(1).getDescription());
        assertEquals("Scramble egg", recipe.getSteps().get(0));
    }

    @Test
    public void execute_stepsWithoutIngredients_parsesSteps() throws WhatsCookingException {
        Recipe recipe = runAdd("toast st/Toast bread").get(0);
        assertEquals("toast", recipe.getTitle());
        assertEquals(0, recipe.getIngredients().size());
        assertEquals(1, recipe.getSteps().size());
        assertEquals("Toast bread", recipe.getSteps().get(0));
    }

    @Test
    public void execute_missingTitle_throwsAndAddsNothing() {
        assertAddRejected("");
        assertAddRejected(", snack");
        assertAddRejected("i/3 eggs");
    }

    @Test
    public void execute_emptyIngredient_throwsAndAddsNothing() {
        assertAddRejected("egg taco i/");
        assertAddRejected("egg taco i/3 eggs, , 5 tortillas");
        assertAddRejected("egg taco i/3 eggs,");
    }

    @Test
    public void execute_emptyStep_throwsAndAddsNothing() {
        assertAddRejected("egg taco st/");
        assertAddRejected("egg taco st/Scramble egg st/ ");
    }

    @Test
    public void parse_addCommand_addsRecipe() throws WhatsCookingException {
        RecipeList recipes = new RecipeList();
        Parser.parse("add egg taco").execute(recipes, new Ui());
        assertEquals(1, recipes.size());
    }

    /** Runs {@code add <arguments>} on a fresh, empty list and returns that list. */
    private static RecipeList runAdd(String arguments) throws WhatsCookingException {
        RecipeList recipes = new RecipeList();
        new AddCommand(arguments).execute(recipes, new Ui());
        return recipes;
    }

    /**
     * Asserts that {@code add <arguments>} is rejected: it throws
     * {@link WhatsCookingException} and leaves the list empty, so a malformed
     * command never stores a half-built recipe.
     *
     * @param arguments the argument string to reject, also used as the failure label
     */
    private static void assertAddRejected(String arguments) {
        RecipeList recipes = new RecipeList();
        AddCommand command = new AddCommand(arguments);
        assertThrows(WhatsCookingException.class, () -> command.execute(recipes, new Ui()), arguments);
        assertEquals(0, recipes.size(), arguments);
    }
}
