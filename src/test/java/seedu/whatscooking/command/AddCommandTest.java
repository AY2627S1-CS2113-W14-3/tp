package seedu.whatscooking.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        assertEquals("Scramble egg", recipe.getSteps().getFirst());
    }

    @Test
    public void execute_stepsWithoutIngredients_parsesSteps() throws WhatsCookingException {
        Recipe recipe = runAdd("toast st/Toast bread").get(0);
        assertEquals("toast", recipe.getTitle());
        assertEquals(0, recipe.getIngredients().size());
        assertEquals(1, recipe.getSteps().size());
        assertEquals("Toast bread", recipe.getSteps().getFirst());
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
    public void execute_noteOnly_setsNote() throws WhatsCookingException {
        Recipe recipe = runAdd("toast n/Best eaten hot").get(0);
        assertEquals("toast", recipe.getTitle());
        assertEquals("Best eaten hot", recipe.getNote());
    }

    @Test
    public void execute_fullLineWithNote_parsesAllParts() throws WhatsCookingException {
        Recipe recipe = runAdd("egg taco, snack i/3 eggs st/Scramble egg st/Wrap n/ Serve hot ").get(0);
        assertEquals("snack", recipe.getType());
        assertEquals(1, recipe.getIngredients().size());
        assertEquals(2, recipe.getSteps().size());
        assertEquals("Wrap", recipe.getSteps().get(1));
        assertEquals("Serve hot", recipe.getNote());
    }

    @Test
    public void execute_noNote_noteIsEmpty() throws WhatsCookingException {
        assertEquals("", runAdd("toast st/Toast bread").get(0).getNote());
    }

    @Test
    public void execute_invalidNote_throwsAndAddsNothing() {
        assertAddRejected("toast n/");
        assertAddRejected("toast n/   ");
        assertAddRejected("toast n/hot n/crispy");
        assertAddRejected("toast n/hot st/Toast bread");
        assertAddRejected("toast n/hot i/2 bread");
    }

    @Test
    public void execute_emptyType_throwsAndAddsNothing() {
        assertAddRejected("egg taco,");
        assertAddRejected("egg taco ,   i/3 eggs");
    }

    @Test
    public void execute_prefixesOutOfOrderOrRepeated_throwsAndAddsNothing() {
        assertAddRejected("egg taco i/3 eggs i/5 tortillas");
        assertAddRejected("egg taco st/Scramble egg i/3 eggs");
    }

    @Test
    public void execute_invalidIngredient_messageNamesIngredient() {
        RecipeList recipes = new RecipeList();
        AddCommand command = new AddCommand("cake i/3 eggs, two cups flour");
        WhatsCookingException e = assertThrows(WhatsCookingException.class,
                () -> command.execute(recipes, new Ui()));
        assertTrue(e.getMessage().contains("two cups flour"));
        assertEquals(0, recipes.size());
    }

    @Test
    public void execute_duplicateTitle_throwsAndKeepsOneRecipe() throws WhatsCookingException {
        RecipeList recipes = runAdd("egg taco");
        AddCommand command = new AddCommand("  EGG TACO , snack");
        assertThrows(WhatsCookingException.class, () -> command.execute(recipes, new Ui()));
        assertEquals(1, recipes.size());
    }

    @Test
    public void execute_servingSize_setsServings() throws WhatsCookingException {
        Recipe recipe = runAdd("egg taco, snack s/4 i/3 eggs st/Scramble egg").get(0);
        assertEquals(4, recipe.getServingSize());
        assertEquals("snack", recipe.getType());
        assertEquals(1, recipe.getIngredients().size());
    }

    @Test
    public void execute_noServingSize_defaultsToOne() throws WhatsCookingException {
        assertEquals(1, runAdd("egg taco i/3 eggs").get(0).getServingSize());
    }

    @Test
    public void execute_invalidServingSize_throwsAndAddsNothing() {
        assertAddRejected("egg taco s/");
        assertAddRejected("egg taco s/0");
        assertAddRejected("egg taco s/-2");
        assertAddRejected("egg taco s/two");
        assertAddRejected("egg taco s/1.5");
        assertAddRejected("egg taco s/99999999999");
        assertAddRejected("egg taco s/2 s/3");
    }

    @Test
    public void execute_servingSizeInWrongPlace_throwsAndAddsNothing() {
        assertAddRejected("egg taco s/2, snack");
        assertAddRejected("egg taco i/3 eggs s/2");
        assertAddRejected("egg taco st/Scramble egg s/2");
        assertAddRejected("egg taco n/Serve hot s/2");
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
