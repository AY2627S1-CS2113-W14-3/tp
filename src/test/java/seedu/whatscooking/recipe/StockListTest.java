package seedu.whatscooking.recipe;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import seedu.whatscooking.WhatsCookingException;

public class StockListTest {

    @Test
    public void add_newIngredient_addsToList() {
        StockList stock = new StockList();
        stock.add(new Ingredient("3 eggs"));

        assertEquals(1, stock.getAll().size());
        assertEquals(3.0, stock.getAll().get(0).getQuantity());
        assertEquals("eggs", stock.getAll().get(0).getName());
    }

    @Test
    public void add_existingIngredient_increasesQuantity() {
        StockList stock = new StockList();
        stock.add(new Ingredient("3 eggs"));
        stock.add(new Ingredient("2 eggs"));

        assertEquals(1, stock.getAll().size());
        assertEquals(5.0, stock.getAll().get(0).getQuantity());
    }

    @Test
    public void add_sameNameDifferentUnit_addsAsSeparateItem() {
        StockList stock = new StockList();
        stock.add(new Ingredient("3 eggs"));
        stock.add(new Ingredient("200 g eggs"));

        assertEquals(2, stock.getAll().size());
    }

    @Test
    public void remove_validIngredient_decreasesQuantity() throws WhatsCookingException {
        StockList stock = new StockList();
        stock.add(new Ingredient("5 eggs"));
        stock.remove(new Ingredient("2 eggs"));

        assertEquals(1, stock.getAll().size());
        assertEquals(3.0, stock.getAll().get(0).getQuantity());
    }

    @Test
    public void remove_allQuantity_removesFromList() throws WhatsCookingException {
        StockList stock = new StockList();
        stock.add(new Ingredient("5 eggs"));
        stock.remove(new Ingredient("5 eggs"));

        assertTrue(stock.getAll().isEmpty());
    }

    @Test
    public void remove_insufficientQuantity_throwsException() {
        StockList stock = new StockList();
        stock.add(new Ingredient("3 eggs"));

        assertThrows(WhatsCookingException.class, () -> stock.remove(new Ingredient("5 eggs")));
    }

    @Test
    public void remove_ingredientNotInStock_throwsException() {
        StockList stock = new StockList();

        assertThrows(WhatsCookingException.class, () -> stock.remove(new Ingredient("3 eggs")));
    }
}
