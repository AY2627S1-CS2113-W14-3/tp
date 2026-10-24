package seedu.whatscooking.recipe;

import java.util.ArrayList;
import seedu.whatscooking.WhatsCookingException;
import java.util.Collections;
import java.util.List;

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
            if (isSameIngredient(item, newItem)) {
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
            if (isSameIngredient(item, toRemove)) {
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

    /**
     * Returns all ingredients currently in the stock.
     * The returned list is unmodifiable to prevent external code from bypassing the add/remove logic.
     *
     * @return an unmodifiable list of ingredients.
     */
    public List<Ingredient> getAll() {
        return Collections.unmodifiableList(ingredients);
    }

    /**
     * Checks if two ingredients are considered the same based on name (ignoring case and plurals) and unit.
     */
    private boolean isSameIngredient(Ingredient item1, Ingredient item2) {
        boolean sameName = normalizeName(item1.getName()).equals(normalizeName(item2.getName()));
        boolean sameUnit = (item1.getUnit() == null && item2.getUnit() == null)
                || (item1.getUnit() != null && item2.getUnit() != null
                && item1.getUnit().equalsIgnoreCase(item2.getUnit()));
        return sameName && sameUnit;
    }

    /**
     * Normalizes an ingredient name by converting to lowercase and removing trailing 's'.
     */
    private String normalizeName(String name) {
        String lower = name.trim().toLowerCase();
        if (lower.endsWith("s") && lower.length() > 1) {
            return lower.substring(0, lower.length() - 1);
        }
        return lower;
    }
}

