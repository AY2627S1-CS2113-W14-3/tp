package seedu.whatscooking.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.whatscooking.WhatsCookingException;
import seedu.whatscooking.recipe.Recipe;
import seedu.whatscooking.recipe.RecipeList;
import seedu.whatscooking.ui.Ui;

public class DeleteCommandTest {
    private RecipeList createList() {
        RecipeList recipes = new RecipeList();
        recipes.add(new Recipe("Chicken Pasta", "main"));
        recipes.add(new Recipe("Apple Pie", "dessert"));
        return recipes;
    }

    @Test
    public void execute_validIndex_deletesRecipe() throws Exception {
        RecipeList recipes = createList();
        Ui ui = new Ui();
        new DeleteCommand("1").execute(recipes, ui);
        assertEquals(1, recipes.size());
        assertEquals("Apple Pie", recipes.get(0).getTitle());
    }

    @Test
    public void execute_validName_deletesRecipe() throws Exception {
        RecipeList recipes = createList();
        Ui ui = new Ui();
        new DeleteCommand("Apple Pie").execute(recipes, ui);
        assertEquals(1, recipes.size());
        assertEquals("Chicken Pasta", recipes.get(0).getTitle());
    }

    @Test
    public void execute_nameCaseInsensitive_deletesRecipe() throws Exception {
        RecipeList recipes = createList();
        Ui ui = new Ui();
        new DeleteCommand("apple pie").execute(recipes, ui);
        assertEquals(1, recipes.size());
    }

    @Test
    public void execute_emptyArgument_throwsException() {
        RecipeList recipes = createList();
        Ui ui = new Ui();
        assertThrows(WhatsCookingException.class,
                () -> new DeleteCommand("").execute(recipes, ui));
    }

    @Test
    public void execute_zeroIndex_throwsException() {
        RecipeList recipes = createList();
        Ui ui = new Ui();
        assertThrows(WhatsCookingException.class,
                () -> new DeleteCommand("0").execute(recipes, ui));
    }

    @Test
    public void execute_negativeIndex_throwsException() {
        RecipeList recipes = createList();
        Ui ui = new Ui();
        assertThrows(WhatsCookingException.class,
                () -> new DeleteCommand("-1").execute(recipes, ui));
    }

    @Test
    public void execute_outOfRangeIndex_throwsException() {
        RecipeList recipes = createList();
        Ui ui = new Ui();
        assertThrows(WhatsCookingException.class,
                () -> new DeleteCommand("99").execute(recipes, ui));
    }

    @Test
    public void execute_nameNotFound_throwsException() {
        RecipeList recipes = createList();
        Ui ui = new Ui();
        assertThrows(WhatsCookingException.class,
                () -> new DeleteCommand("Nonexistent").execute(recipes, ui));
    }
}
