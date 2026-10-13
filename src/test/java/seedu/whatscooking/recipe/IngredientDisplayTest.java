package seedu.whatscooking.recipe;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Checks exact finite decimals and display-only rounding of repeating decimals.
 */
class IngredientDisplayTest {
    @Test
    void getDisplayDescription_finiteDecimals_preservesAllDigitsWithoutTrailingZeroes() {
        double[] quantities = {1.0, 1.5, 1.234, 1.235, 0.005};
        String[] descriptions = {"1 g salt", "1.5 g salt", "1.234 g salt", "1.235 g salt", "0.005 g salt"};
        for (int i = 0; i < quantities.length; i++) {
            Ingredient ingredient = new Ingredient(quantities[i], "g", "salt");
            assertEquals(descriptions[i], ingredient.getDisplayDescription());
            assertEquals(quantities[i], ingredient.getQuantity());
        }
    }

    @Test
    void getDisplayDescription_noUnit_preservesNameWithoutExtraSpaces() {
        Recipe recipe = new Recipe("Eggs", "breakfast");
        recipe.setServingSize(3);
        recipe.addIngredient(new Ingredient("4 eggs"));

        recipe.scaleToServingSize(1);

        assertEquals("1.33 eggs", recipe.getIngredients().get(0).getDisplayDescription());
    }

    @Test
    void getDisplayDescription_tinyFiniteAmount_displaysExactAmount() {
        Ingredient ingredient = new Ingredient("0.004 kg salt");

        assertEquals("0.004 kg salt", ingredient.getDisplayDescription());
        assertEquals(0.004, ingredient.getQuantity());
        assertEquals("0.004 kg salt", ingredient.getDescription());
    }

    @Test
    void getDisplayDescription_longFiniteInput_preservesInputPrecision() {
        Ingredient ingredient = new Ingredient("0.12345678901234567890 g salt");

        assertEquals("0.1234567890123456789 g salt", ingredient.getDisplayDescription());
        assertEquals("0.1234567890123456789 g salt", ingredient.toString());
    }

    @Test
    void getDisplayDescription_repeatedDigitsInFiniteInput_doesNotMistakeForRepeatingFraction() {
        Ingredient ingredient = new Ingredient("0.333333333333 g salt");

        assertEquals("0.333333333333 g salt", ingredient.getDisplayDescription());
    }

    @Test
    void getDisplayDescription_repeatingDecimal_roundsUpWhenNeeded() {
        Recipe recipe = new Recipe("Rice", "main");
        recipe.setServingSize(3);
        recipe.addIngredient(new Ingredient("2 kg rice"));

        recipe.scaleToServingSize(1);

        assertEquals("0.67 kg rice", recipe.getIngredients().get(0).getDisplayDescription());
    }
}
