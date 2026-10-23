package seedu.whatscooking.recipe;

import java.util.ArrayList;
import seedu.whatscooking.WhatsCookingException;

/**
 * Holds the ingredients currently available at home.
 */
public class StockList {
    private final ArrayList<Ingredient> ingredients;

    public StockList() {
        this.ingredients = new ArrayList<>();
    }

    /**
     * Adds an ingredient to the stock.
     * If the ingredient (same name and unit) already exists, increase its quantity.
     *
     * @param newItem the ingredient to add
     */
    public void add(Ingredient newItem) {
        assert newItem != null : "Command should validate before adding";

        for (Ingredient item : ingredients) {
            // Check if name and unit match
            boolean sameName = item.getName().equalsIgnoreCase(newItem.getName());
            boolean sameUnit = (item.getUnit() == null && newItem.getUnit() == null)
                    || (item.getUnit() != null && item.getUnit().equalsIgnoreCase(newItem.getUnit()));
            if (sameName && sameUnit) {
                item.setQuantity(item.getQuantity() + newItem.getQuantity());
                return;
            }
        }
        // If not found, add as new
        ingredients.add(newItem);
    }

    /**
     * Removes an ingredient from the stock.
     * Throws exception if not found or not enough quantity.
     *
     * @param toRemove the ingredient to remove
     * @throws WhatsCookingException if the ingredient is not found or there is not enough quantity
     */
    public void remove(Ingredient toRemove) throws WhatsCookingException {
        assert toRemove != null : "Command should validate before removing";

        for (Ingredient item : ingredients) {
            boolean sameName = item.getName().equalsIgnoreCase(toRemove.getName());
            boolean sameUnit = (item.getUnit() == null && toRemove.getUnit() == null)
                    || (item.getUnit() != null && item.getUnit().equalsIgnoreCase(toRemove.getUnit()));

            if (sameName && sameUnit) {
                if (item.getQuantity() < toRemove.getQuantity()) {
                    throw new WhatsCookingException("Not enough " + item.getName() + " in stock. You only have "
                            + item.getQuantity() + " " + (item.getUnit() == null ? "" : item.getUnit()));
                }

                double newQuantity = item.getQuantity() - toRemove.getQuantity();

                if (newQuantity <= 0.0001) {
                    ingredients.remove(item);
                } else {
                    item.setQuantity(newQuantity);
                }
                return;
            }
        }
        throw new WhatsCookingException("Ingredient not found in stock: " + toRemove.getName());
    }

    public ArrayList<Ingredient> getAll() {
        return ingredients;
    }
}
