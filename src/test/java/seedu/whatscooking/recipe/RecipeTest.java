package seedu.whatscooking.recipe;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests for {@link Recipe}: the state a newly constructed recipe starts in,
 * and that ingredients and steps are kept in the order they were added.
 */
class RecipeTest {
    @Test
    public void constructor_titleAndType_startsEmptyWithDefaultServingSize() {
        Recipe recipe = new Recipe("Pancakes", "breakfast");
        // input:    a new recipe, nothing added to it yet
        // expected: title and type as given, serves 1, no ingredients/steps/note
        assertEquals("Pancakes", recipe.getTitle());
        assertEquals("breakfast", recipe.getType());
        assertEquals(1, recipe.getServingSize());
        assertEquals(0, recipe.getIngredients().size());
        assertEquals(0, recipe.getSteps().size());
        assertTrue(recipe.getNote().isEmpty());
    }

    @Test
    public void addIngredient_twoIngredients_keepsInsertionOrder() {
        Recipe recipe = new Recipe("Pancakes", "breakfast");
        recipe.addIngredient(new Ingredient("3 eggs"));
        recipe.addIngredient(new Ingredient("5 tortillas"));
        // input:    "3 eggs" added before "5 tortillas"
        // actual:   recipe.getIngredients()
        // expected: both present, in the order they were added
        assertEquals(2, recipe.getIngredients().size());
        assertEquals("3 eggs", recipe.getIngredients().get(0).getDescription());
        assertEquals("5 tortillas", recipe.getIngredients().get(1).getDescription());
    }

    @Test
    public void addStep_twoSteps_keepsInsertionOrder() {
        Recipe recipe = new Recipe("Pancakes", "breakfast");
        recipe.addStep("Mix batter");
        recipe.addStep("Fry");
        // input:    "Mix batter" added before "Fry"
        // actual:   recipe.getSteps()
        // expected: both present, in the order they were added
        assertEquals(2, recipe.getSteps().size());
        assertEquals("Mix batter", recipe.getSteps().get(0));
        assertEquals("Fry", recipe.getSteps().get(1));
    }

    @Test
    public void toString_recipeWithServingSize_showsTitleTypeAndServings() {
        Recipe recipe = new Recipe("Pancakes", "breakfast");
        recipe.setServingSize(4);
        // input:    serving size changed to 4
        // actual:   recipe.toString()
        // expected: "Pancakes (breakfast, serves 4)"
        assertEquals("Pancakes (breakfast, serves 4)", recipe.toString());
    }
}
