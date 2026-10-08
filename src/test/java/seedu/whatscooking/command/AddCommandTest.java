package seedu.whatscooking.command;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import seedu.whatscooking.WhatsCookingException;
import seedu.whatscooking.recipe.Recipe;
import seedu.whatscooking.recipe.RecipeList;
import seedu.whatscooking.ui.Ui;

/**
 * Tests for {@link AddCommand}. Each test runs one add and then checks the recipe list:
 * valid input adds exactly one recipe, invalid input leaves the list empty.
 * <p>
 * The tests declare {@code throws WhatsCookingException} only because
 * {@link RecipeList#get(int)} can throw; {@link AddCommand} itself never throws.
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
    public void execute_missingTitle_addsNothing() {
        assertEquals(0, runAdd("").size());
        assertEquals(0, runAdd(", snack").size());
        assertEquals(0, runAdd("i/3 eggs").size());
    }

    @Test
    public void execute_emptyIngredient_addsNothing() {
        assertEquals(0, runAdd("egg taco i/").size());
        assertEquals(0, runAdd("egg taco i/3 eggs, , 5 tortillas").size());
        assertEquals(0, runAdd("egg taco i/3 eggs,").size());
    }

    @Test
    public void execute_emptyStep_addsNothing() {
        assertEquals(0, runAdd("egg taco st/").size());
        assertEquals(0, runAdd("egg taco st/Scramble egg st/ ").size());
    }

    @Test
    public void parse_addCommand_addsRecipe() throws WhatsCookingException {
        RecipeList recipes = new RecipeList();
        Parser.parse("add egg taco").execute(recipes, new Ui());
        assertEquals(1, recipes.size());
    }

    /** Runs {@code add <arguments>} on a fresh, empty list and returns that list. */
    private static RecipeList runAdd(String arguments) {
        RecipeList recipes = new RecipeList();
        new AddCommand(arguments).execute(recipes, new Ui());
        return recipes;
    }
}
