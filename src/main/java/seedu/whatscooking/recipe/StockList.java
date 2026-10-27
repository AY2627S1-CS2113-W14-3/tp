package seedu.whatscooking.recipe;

import java.util.ArrayList;
import seedu.whatscooking.WhatsCookingException;
import java.util.Collections;
import java.util.List;

/**
 * Holds the ingredients currently available at home.
 */
public class StockList {
    private final ArrayList<StockItem> ingredients;

    public StockList() {
        this.ingredients = new ArrayList<>();
    }

    /**
     * Adds an ingredient to the stock.
     * If the ingredient (same name and unit) already exists, increase its quantity.
     *
     * @param newItem the ingredient to add
     */
    public void add(StockItem newItem) {
        assert newItem != null : "Command should validate before adding";

        for (StockItem item : ingredients) {
            // Check if name and unit match
            if (isSameIngredient(item, newItem) && item.isSameExpiry(newItem)) {
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
    public void remove(StockItem toRemove) throws WhatsCookingException {
        assert toRemove != null : "Command should validate before removing";

        List<StockItem> matches = new ArrayList<>();
        for (StockItem item : ingredients) {
            if (isSameIngredient(item, toRemove)) {
                matches.add(item);
            }
        }

        if (matches.isEmpty()) {
            throw new WhatsCookingException("Ingredient not found in stock: " + toRemove.getName());
        }

        matches.sort((a, b) -> {
            if (a.getExpiryDate() == null && b.getExpiryDate() == null) {
                return 0;
            }
            if (a.getExpiryDate() == null) {
                return 1;
            }
            if (b.getExpiryDate() == null) {
                return -1;
            }
            return a.getExpiryDate().compareTo(b.getExpiryDate());
        });

        double remainingToRemove = toRemove.getQuantity();

        for (StockItem item : matches) {
            if (remainingToRemove <= 0) {
                break;
            }

            if (item.getQuantity() <= remainingToRemove) {
                remainingToRemove -= item.getQuantity();
                ingredients.remove(item);
            } else {
                item.setQuantity(item.getQuantity() - remainingToRemove);
                remainingToRemove = 0;
            }
        }

        if (remainingToRemove > 0.0001) {
            throw new WhatsCookingException("Not enough " + toRemove.getName() + " in stock.");
        }
    }

    /**
     * Returns all ingredients currently in the stock.
     * The returned list is unmodifiable to prevent external code from bypassing the add/remove logic.
     *
     * @return an unmodifiable list of ingredients.
     */
    public List<StockItem> getAll() {
        return Collections.unmodifiableList(ingredients);
    }

    /**
     * Checks if two ingredients are considered the same based on name (ignoring case and plurals) and unit.
     */
    private boolean isSameIngredient(StockItem item1, StockItem item2) {
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

