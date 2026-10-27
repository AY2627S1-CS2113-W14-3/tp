package seedu.whatscooking.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import seedu.whatscooking.WhatsCookingException;
import seedu.whatscooking.recipe.RecipeList;
import seedu.whatscooking.recipe.StockList;
import seedu.whatscooking.ui.Ui;

public class StockCommandTest {
    @Test
    public void execute_addNewIngredient_success() throws Exception {
        StockList stock = new StockList();
        Ui ui = new Ui();
        new StockCommand("add 3 eggs", stock).execute(new RecipeList(), ui);

        assertEquals(1, stock.getAll().size());
        assertEquals(3.0, stock.getAll().get(0).getQuantity());
        assertNull(stock.getAll().get(0).getExpiryDate());
    }

    @Test
    public void execute_addWithExpiryDate_success() throws Exception {
        StockList stock = new StockList();
        Ui ui = new Ui();
        new StockCommand("add 2 milk by/2026-12-31", stock).execute(new RecipeList(), ui);

        assertEquals(1, stock.getAll().size());
        assertEquals(2.0, stock.getAll().get(0).getQuantity());
        assertEquals(LocalDate.parse("2026-12-31"), stock.getAll().get(0).getExpiryDate());
    }

    @Test
    public void execute_addWithInvalidExpiryDate_throwsException() {
        StockList stock = new StockList();
        Ui ui = new Ui();

        assertThrows(WhatsCookingException.class, () ->
                new StockCommand("add 2 milk by/31-12-2026", stock).execute(new RecipeList(), ui));
    }

    @Test
    public void execute_addExistingIngredient_updatesQuantity() throws Exception {
        StockList stock = new StockList();
        Ui ui = new Ui();
        new StockCommand("add 3 eggs", stock).execute(new RecipeList(), ui);
        new StockCommand("add 2 eggs", stock).execute(new RecipeList(), ui);

        assertEquals(1, stock.getAll().size());
        assertEquals(5.0, stock.getAll().get(0).getQuantity());
    }

    @Test
    public void execute_removeValidIngredient_updatesQuantity() throws Exception {
        StockList stock = new StockList();
        Ui ui = new Ui();
        new StockCommand("add 5 eggs", stock).execute(new RecipeList(), ui);
        new StockCommand("remove 2 eggs", stock).execute(new RecipeList(), ui);

        assertEquals(3.0, stock.getAll().get(0).getQuantity());
    }

    @Test
    public void execute_removeAllIngredient_removesFromList() throws Exception {
        StockList stock = new StockList();
        Ui ui = new Ui();
        new StockCommand("add 5 eggs", stock).execute(new RecipeList(), ui);
        new StockCommand("remove 5 eggs", stock).execute(new RecipeList(), ui);

        assertTrue(stock.getAll().isEmpty());
    }

    @Test
    public void execute_removeTooMuch_throwsException() {
        StockList stock = new StockList();
        Ui ui = new Ui();
        assertThrows(WhatsCookingException.class, () -> {
            new StockCommand("add 3 eggs", stock).execute(new RecipeList(), ui);
            new StockCommand("remove 5 eggs", stock).execute(new RecipeList(), ui);
        });
    }

    @Test
    public void execute_removeNotInStock_throwsException() {
        StockList stock = new StockList();
        Ui ui = new Ui();
        assertThrows(WhatsCookingException.class, () -> {
            new StockCommand("remove 3 eggs", stock).execute(new RecipeList(), ui);
        });
    }

    @Test
    public void execute_listWithExtraArguments_throwsException() {
        StockList stock = new StockList();
        Ui ui = new Ui();

        assertThrows(WhatsCookingException.class, () ->
                new StockCommand("list something", stock).execute(new RecipeList(), ui));
    }
}
