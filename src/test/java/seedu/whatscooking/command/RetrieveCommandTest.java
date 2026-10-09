package seedu.whatscooking.command;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.whatscooking.WhatsCookingException;
import seedu.whatscooking.recipe.Recipe;
import seedu.whatscooking.recipe.RecipeList;
import seedu.whatscooking.ui.Ui;

public class RetrieveCommandTest {
    private RecipeList createListWithOneRecipe() {
        RecipeList recipes = new RecipeList();
        recipes.add(new Recipe("Chicken Pasta", "main"));
        return recipes;
    }

    @Test
    public void execute_validIndex_showsRecipe() throws Exception {
        RecipeList recipes = createListWithOneRecipe();
        Ui ui = new Ui();
        new RetrieveCommand("1").execute(recipes, ui);
    }

    @Test
    public void execute_validName_showsRecipe() throws Exception {
        RecipeList recipes = createListWithOneRecipe();
        Ui ui = new Ui();
        new RetrieveCommand("Chicken Pasta").execute(recipes, ui);
    }

    @Test
    public void execute_nameCaseInsensitive_showsRecipe() throws Exception {
        RecipeList recipes = createListWithOneRecipe();
        Ui ui = new Ui();
        new RetrieveCommand("chicken pasta").execute(recipes, ui);
    }

    @Test
    public void execute_emptyArgument_throwsException() {
        RecipeList recipes = createListWithOneRecipe();
        Ui ui = new Ui();
        assertThrows(WhatsCookingException.class,
                () -> new RetrieveCommand("").execute(recipes, ui));
    }

    @Test
    public void execute_zeroIndex_throwsException() {
        RecipeList recipes = createListWithOneRecipe();
        Ui ui = new Ui();
        assertThrows(WhatsCookingException.class,
                () -> new RetrieveCommand("0").execute(recipes, ui));
    }

    @Test
    public void execute_negativeIndex_throwsException() {
        RecipeList recipes = createListWithOneRecipe();
        Ui ui = new Ui();
        assertThrows(WhatsCookingException.class,
                () -> new RetrieveCommand("-1").execute(recipes, ui));
    }

    @Test
    public void execute_outOfRangeIndex_throwsException() {
        RecipeList recipes = createListWithOneRecipe();
        Ui ui = new Ui();
        assertThrows(WhatsCookingException.class,
                () -> new RetrieveCommand("99").execute(recipes, ui));
    }

    @Test
    public void execute_nameNotFound_throwsException() {
        RecipeList recipes = createListWithOneRecipe();
        Ui ui = new Ui();
        assertThrows(WhatsCookingException.class,
                () -> new RetrieveCommand("Nonexistent").execute(recipes, ui));
    }
}
