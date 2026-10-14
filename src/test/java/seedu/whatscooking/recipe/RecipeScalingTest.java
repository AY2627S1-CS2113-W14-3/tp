package seedu.whatscooking.recipe;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Verifies proportional resizing, preserved recipe details and all-or-nothing validation.
 */
class RecipeScalingTest {
    @Test
    void scaleToServingSize_largerThenSmaller_usesCurrentServingSize() {
        Recipe recipe = createRecipe();

        recipe.scaleToServingSize(4);
        assertEquals(4, recipe.getServingSize());
        assertEquals(4.0, recipe.getIngredients().get(0).getQuantity());
        assertEquals(200.0, recipe.getIngredients().get(1).getQuantity());

        recipe.scaleToServingSize(3);
        assertEquals(3, recipe.getServingSize());
        assertEquals(3.0, recipe.getIngredients().get(0).getQuantity());
        assertEquals(150.0, recipe.getIngredients().get(1).getQuantity());
    }

    @Test
    void scaleToServingSize_validSize_preservesOtherDetailsAndIngredientObjects() {
        Recipe recipe = createRecipe();
        Ingredient eggs = recipe.getIngredients().get(0);
        recipe.addStep("Cook the eggs");
        recipe.setNote("Serve hot");

        recipe.scaleToServingSize(1);

        assertEquals("Egg Taco", recipe.getTitle());
        assertEquals("snack", recipe.getType());
        assertEquals(java.util.List.of("Cook the eggs"), recipe.getSteps());
        assertEquals("Serve hot", recipe.getNote());
        assertSame(eggs, recipe.getIngredients().get(0));
        assertEquals("eggs", eggs.getName());
        assertEquals("g", recipe.getIngredients().get(1).getUnit());
        assertEquals("cheese", recipe.getIngredients().get(1).getName());
    }

    @Test
    void scaleToServingSize_decimalQuantity_avoidsVisibleBinaryArtefact() {
        Recipe recipe = new Recipe("Oil mix", "sauce");
        recipe.addIngredient(new Ingredient("0.1 tbsp oil"));

        recipe.scaleToServingSize(3);

        assertEquals(0.3, recipe.getIngredients().get(0).getQuantity());
        assertEquals("0.3 tbsp oil", recipe.getIngredients().get(0).getDisplayDescription());
    }

    @Test
    void scaleToServingSize_repeatingFraction_roundsOnlyForDisplay() {
        Recipe recipe = new Recipe("Rice", "main");
        recipe.setServingSize(3);
        recipe.addIngredient(new Ingredient("1 kg rice"));

        recipe.scaleToServingSize(1);
        assertEquals(1.0 / 3, recipe.getIngredients().get(0).getQuantity(), 1e-15);
        assertEquals("0.33 kg rice", recipe.getIngredients().get(0).getDisplayDescription());

        recipe.scaleToServingSize(3);
        assertEquals(1.0, recipe.getIngredients().get(0).getQuantity());
        assertEquals("1 kg rice", recipe.getIngredients().get(0).getDisplayDescription());
    }

    @Test
    void scaleToServingSize_finiteResultWithThreeDecimals_displaysExactResult() {
        Recipe recipe = new Recipe("Spice mix", "seasoning");
        recipe.addIngredient(new Ingredient("0.125 g pepper"));

        recipe.scaleToServingSize(3);

        assertEquals(0.375, recipe.getIngredients().get(0).getQuantity());
        assertEquals("0.375 g pepper", recipe.getIngredients().get(0).getDisplayDescription());
    }

    @Test
    void scaleToServingSize_repeatingThenFinite_restoresAllFiniteDigits() {
        Recipe recipe = new Recipe("Spice mix", "seasoning");
        recipe.setServingSize(7);
        recipe.addIngredient(new Ingredient("0.375 g pepper"));

        recipe.scaleToServingSize(1);
        assertEquals("0.05 g pepper", recipe.getIngredients().get(0).getDisplayDescription());

        recipe.scaleToServingSize(7);
        assertEquals(0.375, recipe.getIngredients().get(0).getQuantity());
        assertEquals("0.375 g pepper", recipe.getIngredients().get(0).getDisplayDescription());
    }

    @Test
    void scaleToServingSize_longFiniteInput_preservesDigitsBeyondDoublePrecision() {
        Recipe recipe = new Recipe("Spice mix", "seasoning");
        recipe.addIngredient(new Ingredient("0.1234567890123456789 g pepper"));

        recipe.scaleToServingSize(3);

        assertEquals("0.3703703670370370367 g pepper", recipe.getIngredients().get(0).getDisplayDescription());
    }

    @Test
    void scaleToServingSize_manuallyUpdatedQuantity_usesReplacementValue() {
        Recipe recipe = new Recipe("Spice mix", "seasoning");
        recipe.setServingSize(3);
        Ingredient pepper = new Ingredient("1 g pepper");
        recipe.addIngredient(pepper);
        recipe.scaleToServingSize(1);

        pepper.setQuantity(0.125);
        recipe.scaleToServingSize(3);

        assertEquals("0.375 g pepper", pepper.getDisplayDescription());
    }

    @Test
    void scaleToServingSize_failedResize_preservesExactRepeatingQuantity() {
        Recipe recipe = new Recipe("Rice", "main");
        recipe.setServingSize(3);
        Ingredient rice = new Ingredient("1 kg rice");
        recipe.addIngredient(rice);
        recipe.scaleToServingSize(1);
        Ingredient salt = new Ingredient(Double.MAX_VALUE, "g", "salt");
        recipe.addIngredient(salt);

        assertThrows(IllegalArgumentException.class, () -> recipe.scaleToServingSize(3));
        assertEquals(1, recipe.getServingSize());
        assertEquals("0.33 kg rice", rice.getDisplayDescription());

        salt.setQuantity(1);
        recipe.scaleToServingSize(3);
        assertEquals(1.0, rice.getQuantity());
        assertEquals("1 kg rice", rice.getDisplayDescription());
    }

    @Test
    void scaleToServingSize_sameSize_preservesExactQuantity() {
        Recipe recipe = new Recipe("Large recipe", "main");
        recipe.addIngredient(new Ingredient(Double.MAX_VALUE, "g", "rice"));

        recipe.scaleToServingSize(1);

        assertEquals(1, recipe.getServingSize());
        assertEquals(Double.MAX_VALUE, recipe.getIngredients().get(0).getQuantity());
    }

    @Test
    void scaleToServingSize_noIngredients_updatesServingSize() {
        Recipe recipe = new Recipe("Toast", "breakfast");

        recipe.scaleToServingSize(4);

        assertEquals(4, recipe.getServingSize());
        assertEquals(0, recipe.getIngredients().size());
    }

    @Test
    void scaleToServingSize_invalidSize_preservesRecipe() {
        Recipe recipe = createRecipe();
        for (int size : new int[]{0, -1, Integer.MIN_VALUE}) {
            assertThrows(IllegalArgumentException.class, () -> recipe.scaleToServingSize(size));
            assertEquals(2, recipe.getServingSize());
            assertEquals(2.0, recipe.getIngredients().get(0).getQuantity());
            assertEquals(100.0, recipe.getIngredients().get(1).getQuantity());
        }
    }

    @Test
    void setServingSize_invalidSize_preservesServingSize() {
        Recipe recipe = createRecipe();
        for (int size : new int[]{0, -1, Integer.MIN_VALUE}) {
            assertThrows(IllegalArgumentException.class, () -> recipe.setServingSize(size));
            assertEquals(2, recipe.getServingSize());
        }
    }

    @Test
    void scaleToServingSize_laterQuantityOverflows_preservesAllQuantities() {
        Recipe recipe = createRecipe();
        recipe.addIngredient(new Ingredient(Double.MAX_VALUE, "g", "rice"));

        assertThrows(IllegalArgumentException.class, () -> recipe.scaleToServingSize(4));

        assertEquals(2, recipe.getServingSize());
        assertEquals(2.0, recipe.getIngredients().get(0).getQuantity());
        assertEquals(100.0, recipe.getIngredients().get(1).getQuantity());
        assertEquals(Double.MAX_VALUE, recipe.getIngredients().get(2).getQuantity());
    }

    @Test
    void scaleToServingSize_laterQuantityUnderflows_preservesAllQuantities() {
        Recipe recipe = createRecipe();
        recipe.addIngredient(new Ingredient(Double.MIN_VALUE, "g", "salt"));

        assertThrows(IllegalArgumentException.class, () -> recipe.scaleToServingSize(1));

        assertEquals(2, recipe.getServingSize());
        assertEquals(2.0, recipe.getIngredients().get(0).getQuantity());
        assertEquals(100.0, recipe.getIngredients().get(1).getQuantity());
        assertEquals(Double.MIN_VALUE, recipe.getIngredients().get(2).getQuantity());
    }

    /**
     * Creates a recipe with known quantities for two servings.
     */
    private Recipe createRecipe() {
        Recipe recipe = new Recipe("Egg Taco", "snack");
        recipe.setServingSize(2);
        recipe.addIngredient(new Ingredient("2 eggs"));
        recipe.addIngredient(new Ingredient("100 g cheese"));
        return recipe;
    }
}
