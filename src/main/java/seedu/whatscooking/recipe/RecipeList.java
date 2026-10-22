package seedu.whatscooking.recipe;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import seedu.whatscooking.AppLogger;
import seedu.whatscooking.WhatsCookingException;

/**
 * Holds the recipes the user has added and provides safe access to them.
 * <p>
 * Indices passed to and returned by this class are 0-based, matching
 * {@link ArrayList} convention. Commands that display recipe numbers to the
 * user (e.g. "recipe 1") are responsible for converting between that
 * 1-based display numbering and the 0-based indices used here.
 */
public class RecipeList {
    private static final Logger logger = AppLogger.getLogger(RecipeList.class);

    private final ArrayList<Recipe> recipes;

    public RecipeList() {
        this.recipes = new ArrayList<>();
    }

    /**
     * Adds a recipe to the end of the list.
     *
     * @param recipe the recipe to store; commands are expected to have fully
     *     validated it before calling this
     */
    public void add(Recipe recipe) {
        assert recipe != null : "Commands should reject bad input before adding, so recipe is never null";

        recipes.add(recipe);
        logger.log(Level.FINE, "Added recipe \"{0}\"; list now holds {1}",
                new Object[]{recipe.getTitle(), recipes.size()});
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
            logger.log(Level.FINE, "Rejected out-of-range index {0}; list holds {1}",
                    new Object[]{index, recipes.size()});
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
        assert name != null : "Name should not be null";
        String target = name.trim();
        for (Recipe recipe : recipes) {
            if (recipe.getTitle().trim().equalsIgnoreCase(target)) {
                return recipe;
            }
        }
        logger.log(Level.FINE, "Rejected unknown title \"{0}\"; list holds {1}",
                new Object[]{target, recipes.size()});
        throw new WhatsCookingException("Recipe not found: " + target);
    }

    /**
     * Removes the recipe at the given 0-based index and returns it.
     *
     * @param index 0-based position of the recipe
     * @return the recipe that was removed
     * @throws WhatsCookingException if index is out of bounds
     */
    public Recipe delete(int index) throws WhatsCookingException {
        assert index >= 0 : "Caller should pass a 0-based index";
        if (index >= recipes.size()) {
            logger.log(Level.FINE, "Rejected delete at out-of-range index {0}; list holds {1}",
                    new Object[]{index, recipes.size()});
            throw new WhatsCookingException(
                    "Recipe " + (index + 1) + " does not exist; you have " + recipes.size() + " recipe(s).");
        }
        Recipe removed = recipes.remove(index);
        logger.log(Level.FINE, "Deleted recipe \"{0}\"; list now holds {1}",
                new Object[]{removed.getTitle(), recipes.size()});
        return removed;
    }

    /**
     * Removes the first recipe whose title matches the given name,
     * case-insensitively, and returns it.
     *
     * @param name the recipe title to delete
     * @return the recipe that was removed
     * @throws WhatsCookingException if no recipe with that title exists
     */
    public Recipe deleteByName(String name) throws WhatsCookingException {
        assert name != null : "Name should not be null";
        Recipe recipe = getByName(name);
        recipes.remove(recipe);
        logger.log(Level.FINE, "Deleted recipe \"{0}\" by name; list now holds {1}",
                new Object[]{recipe.getTitle(), recipes.size()});
        return recipe;
    }

    /**
     * Returns whether any recipe already uses the given title, ignoring case
     * and surrounding spaces.
     *
     * @param title the title to look for
     * @return true if a recipe with that title exists
     */
    public boolean containsTitle(String title) {
        assert title != null : "Title should not be null";
        String target = title.trim();
        for (Recipe recipe : recipes) {
            if (recipe.getTitle().trim().equalsIgnoreCase(target)) {
                return true;
            }
        }
        return false;
    }

    public int size() {
        return recipes.size();
    }

    public boolean isEmpty() {
        return recipes.isEmpty();
    }
}


