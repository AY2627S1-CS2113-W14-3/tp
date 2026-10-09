package seedu.whatscooking.recipe;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Tests ingredient parsing, quantity changes and the display contract used by recipe commands.
 */
class IngredientTest {
    @Test
    void constructor_separatedUnit_extractsFields() {
        Ingredient ingredient = new Ingredient("200 g cheese");

        assertEquals(200.0, ingredient.getQuantity());
        assertEquals("g", ingredient.getUnit());
        assertEquals("cheese", ingredient.getName());
        assertEquals("200 g cheese", ingredient.getDescription());
    }

    @Test
    void constructor_attachedUnit_extractsFields() {
        Ingredient ingredient = new Ingredient("200g cheese");

        assertEquals(200.0, ingredient.getQuantity());
        assertEquals("g", ingredient.getUnit());
        assertEquals("cheese", ingredient.getName());
        assertEquals("200 g cheese", ingredient.toString());
    }

    @Test
    void constructor_countedIngredient_hasNoUnit() {
        Ingredient ingredient = new Ingredient("3 eggs");

        assertEquals(3.0, ingredient.getQuantity());
        assertNull(ingredient.getUnit());
        assertEquals("eggs", ingredient.getName());
        assertEquals("3 eggs", ingredient.getDescription());
    }

    @Test
    void constructor_multiWordName_keepsFullName() {
        Ingredient ingredient = new Ingredient("2 red onions");

        assertNull(ingredient.getUnit());
        assertEquals("red onions", ingredient.getName());
        assertEquals("2 red onions", ingredient.toString());
    }

    @Test
    void constructor_decimalQuantity_preservesFraction() {
        Ingredient ingredient = new Ingredient("1.5 cups plain flour");

        assertEquals(1.5, ingredient.getQuantity());
        assertEquals("cups", ingredient.getUnit());
        assertEquals("plain flour", ingredient.getName());
    }

    @Test
    void constructor_fractionWithoutLeadingZero_parsesQuantity() {
        Ingredient ingredient = new Ingredient(".5kg rice");

        assertEquals(0.5, ingredient.getQuantity());
        assertEquals("kg", ingredient.getUnit());
        assertEquals("0.5 kg rice", ingredient.toString());
    }

    @Test
    void constructor_extraWhitespace_ignoresSeparators() {
        Ingredient ingredient = new Ingredient(" \t200  \t g  cheese \t ");

        assertEquals(200.0, ingredient.getQuantity());
        assertEquals("g", ingredient.getUnit());
        assertEquals("cheese", ingredient.getName());
    }

    @Test
    void constructor_mixedCaseUnit_preservesDisplay() {
        Ingredient ingredient = new Ingredient("250 mL milk");

        assertEquals("mL", ingredient.getUnit());
        assertEquals("milk", ingredient.getName());
        assertEquals("250 mL milk", ingredient.toString());
    }

    @Test
    void constructor_missingDescription_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new Ingredient((String) null));
        assertThrows(IllegalArgumentException.class, () -> new Ingredient(""));
        assertThrows(IllegalArgumentException.class, () -> new Ingredient(" \t "));
    }

    @Test
    void constructor_missingQuantityOrName_throwsException() {
        String[] descriptions = {"cheese", "3", "200g", "200 g", "3   ", "200 g   "};
        for (String description : descriptions) {
            assertThrows(IllegalArgumentException.class, () -> new Ingredient(description), description);
        }
    }

    @Test
    void constructor_malformedQuantity_throwsException() {
        String[] descriptions = {
            "-2 eggs", "0 eggs", "1.2.3 kg rice", "NaN g salt", "Infinity g salt", "1/2 cup rice"
        };
        for (String description : descriptions) {
            assertThrows(IllegalArgumentException.class, () -> new Ingredient(description), description);
        }
    }

    @Test
    void constructor_overflowingQuantity_throwsException() {
        String description = "9".repeat(400) + " g rice";

        assertThrows(IllegalArgumentException.class, () -> new Ingredient(description));
    }

    @Test
    void constructor_unsupportedAttachedUnit_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new Ingredient("2xyz rice"));
    }

    @Test
    void constructor_separateValues_supportsCustomUnit() {
        Ingredient ingredient = new Ingredient(2.0, " slices ", " bread ");

        assertEquals(2.0, ingredient.getQuantity());
        assertEquals("slices", ingredient.getUnit());
        assertEquals("bread", ingredient.getName());
        assertEquals("2 slices bread", ingredient.toString());
    }

    @Test
    void constructor_nullOrBlankUnit_representsCount() {
        assertEquals("1.5 eggs", new Ingredient(1.5, null, "eggs").getDescription());
        assertNull(new Ingredient(3.0, " \t ", "eggs").getUnit());
    }

    @Test
    void constructor_invalidNumericQuantity_throwsException() {
        double[] quantities = {0.0, -1.0, Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY};
        for (double quantity : quantities) {
            assertThrows(IllegalArgumentException.class, () -> new Ingredient(quantity, "g", "salt"));
        }
    }

    @Test
    void constructor_missingName_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new Ingredient(1.0, "g", null));
        assertThrows(IllegalArgumentException.class, () -> new Ingredient(1.0, "g", " \t "));
    }

    @Test
    void setQuantity_fractionalAmount_updatesDescription() {
        Ingredient ingredient = new Ingredient("3 eggs");

        ingredient.setQuantity(1.5);

        assertEquals(1.5, ingredient.getQuantity());
        assertNull(ingredient.getUnit());
        assertEquals("eggs", ingredient.getName());
        assertEquals("1.5 eggs", ingredient.getDescription());
        assertEquals("1.5 eggs", ingredient.toString());
    }

    @Test
    void setQuantity_measuredIngredient_preservesUnitAndName() {
        Ingredient ingredient = new Ingredient("200 g cheese");

        ingredient.setQuantity(300.0);

        assertEquals("g", ingredient.getUnit());
        assertEquals("cheese", ingredient.getName());
        assertEquals("300 g cheese", ingredient.getDescription());
    }

    @Test
    void setQuantity_invalidAmount_keepsPreviousValue() {
        Ingredient ingredient = new Ingredient("200 g cheese");
        double[] quantities = {0.0, -1.0, Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY};

        for (double quantity : quantities) {
            assertThrows(IllegalArgumentException.class, () -> ingredient.setQuantity(quantity));
            assertEquals(200.0, ingredient.getQuantity());
            assertEquals("200 g cheese", ingredient.getDescription());
        }
    }

    @Test
    void getDescription_smallQuantity_usesPlainDecimal() {
        Ingredient ingredient = new Ingredient(0.0000001, "g", "salt");

        assertEquals("0.0000001 g salt", ingredient.getDescription());
    }
}
