package seedu.whatscooking.recipe;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.whatscooking.WhatsCookingException;

class RecipeListTest {
    @Test
    public void add_singleRecipe_sizeIncreases() {
        RecipeList recipes = new RecipeList();
        recipes.add(new Recipe("Pancakes", "breakfast"));
        // input:    1 recipe added
        // actual:   recipes.size()
        // expected: 1
        assertEquals(1, recipes.size());
    }

    @Test
    public void get_validIndex_returnsRecipe() throws WhatsCookingException {
        RecipeList recipes = new RecipeList();
        Recipe pancakes = new Recipe("Pancakes", "breakfast");
        recipes.add(pancakes);
        // input:    index 0 (pancakes was added there)
        // actual:   recipes.get(0)
        // expected: pancakes
        assertEquals(pancakes, recipes.get(0));
    }

    @Test
    public void get_indexOutOfBounds_throwsException() {
        RecipeList recipes = new RecipeList();
        // input:    index 0, on an empty list
        // expected: WhatsCookingException
        assertThrows(WhatsCookingException.class, () -> recipes.get(0));
    }
}
