package seedu.whatscooking.recipe;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import seedu.whatscooking.WhatsCookingException;

public class StockListTest {

    @Test
    public void add_newIngredient_addsToList() {
        StockList stock = new StockList();
        stock.add(new StockItem(new Ingredient("3 eggs"), null));

        assertEquals(1, stock.getAll().size());
        assertEquals(3.0, stock.getAll().get(0).getQuantity());
        assertEquals("eggs", stock.getAll().get(0).getName());
    }

    @Test
    public void add_existingIngredientSameExpiry_increasesQuantity() {
        StockList stock = new StockList();
        LocalDate date = LocalDate.now().plusDays(5);
        stock.add(new StockItem(new Ingredient("3 eggs"), date));
        stock.add(new StockItem(new Ingredient("2 eggs"), date));

        assertEquals(1, stock.getAll().size());
        assertEquals(5.0, stock.getAll().get(0).getQuantity());
    }

    @Test
    public void add_existingIngredientDifferentExpiry_addsAsSeparateItem() {
        StockList stock = new StockList();
        stock.add(new StockItem(new Ingredient("3 eggs"), LocalDate.now().plusDays(5)));
        stock.add(new StockItem(new Ingredient("2 eggs"), LocalDate.now().plusDays(10)));

        assertEquals(2, stock.getAll().size());
    }

    @Test
    public void add_sameNameDifferentUnit_addsAsSeparateItem() {
        StockList stock = new StockList();
        stock.add(new StockItem(new Ingredient("3 eggs"), null));
        stock.add(new StockItem(new Ingredient("200 g eggs"), null));

        assertEquals(2, stock.getAll().size());
    }

    @Test
    public void remove_validIngredient_decreasesQuantity() throws WhatsCookingException {
        StockList stock = new StockList();
        stock.add(new StockItem(new Ingredient("5 eggs"), null));
        stock.remove(new StockItem(new Ingredient("2 eggs"), null));

        assertEquals(1, stock.getAll().size());
        assertEquals(3.0, stock.getAll().get(0).getQuantity());
    }

    @Test
    public void remove_allQuantity_removesFromList() throws WhatsCookingException {
        StockList stock = new StockList();
        stock.add(new StockItem(new Ingredient("5 eggs"), null));
        stock.remove(new StockItem(new Ingredient("5 eggs"), null));

        assertTrue(stock.getAll().isEmpty());
    }

    @Test
    public void remove_prioritizesEarlierExpiry() throws WhatsCookingException {
        StockList stock = new StockList();
        LocalDate soon = LocalDate.now().plusDays(1);
        LocalDate later = LocalDate.now().plusDays(10);

        stock.add(new StockItem(new Ingredient("3 eggs"), soon));
        stock.add(new StockItem(new Ingredient("2 eggs"), later));

        stock.remove(new StockItem(new Ingredient("4 eggs"), null));

        assertEquals(1, stock.getAll().size());
        assertEquals(1.0, stock.getAll().get(0).getQuantity());
        assertEquals(later, stock.getAll().get(0).getExpiryDate());
    }

    @Test
    public void remove_insufficientQuantity_throwsException() {
        StockList stock = new StockList();
        stock.add(new StockItem(new Ingredient("3 eggs"), null));

        assertThrows(WhatsCookingException.class,
                () -> stock.remove(new StockItem(new Ingredient("5 eggs"), null)));
    }

    @Test
    public void remove_ingredientNotInStock_throwsException() {
        StockList stock = new StockList();

        assertThrows(WhatsCookingException.class,
                () -> stock.remove(new StockItem(new Ingredient("3 eggs"), null)));
    }
}
