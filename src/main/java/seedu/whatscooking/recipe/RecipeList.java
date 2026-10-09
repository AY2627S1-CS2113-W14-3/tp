package seedu.whatscooking.recipe;

import seedu.whatscooking.WhatsCookingException;

import java.util.ArrayList;

/**
 * Holds the recipes the user has added and provides safe access to them.
 * <p>
 * Indices passed to and returned by this class are 0-based, matching
 * {@link ArrayList} convention. Commands that display recipe numbers to the
 * user (e.g. "recipe 1") are responsible for converting between that
 * 1-based display numbering and the 0-based indices used here.
 */
public class RecipeList {
    private final ArrayList<Recipe> recipes;

    public RecipeList() {
        this.recipes = new ArrayList<>();
    }

    public void add(Recipe recipe) {
        recipes.add(recipe);
    }

    /**
     * Returns the recipe at the given 0-based index.
     *
     * @param index 0-based position of the recipe
     * @return the recipe at that position
     * @throws WhatsCookingException if index is out of bounds, with a
     *     message that reports the 1-based recipe number the user typed
     */
    public Recipe get(int index) throws WhatsCookingException {
        if (index < 0 || index >= recipes.size()) {
            throw new WhatsCookingException(
                    "Recipe " + (index + 1) + " does not exist; you have " + recipes.size() + " recipe(s).");
        }
        return recipes.get(index);
    }

    /**
     * Returns the first recipe whose title matches the given name,
     * case-insensitively.
     *
     * @param name the recipe title to search for
     * @return the matching recipe
     * @throws WhatsCookingException if no recipe with that title exists
     */
    public Recipe getByName(String name) throws WhatsCookingException {
        for (Recipe recipe : recipes) {
            if (recipe.getTitle().equalsIgnoreCase(name)) {
                return recipe;
            }
        }
        throw new WhatsCookingException("Recipe not found: " + name);
    }


    public int size() {
        return recipes.size();
    }

    public boolean isEmpty() {
        return recipes.isEmpty();
    }
}
