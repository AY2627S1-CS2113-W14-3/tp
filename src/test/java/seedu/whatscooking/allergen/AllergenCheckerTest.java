package seedu.whatscooking.allergen;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.whatscooking.recipe.Ingredient;
import seedu.whatscooking.recipe.Recipe;

/** Verifies the built-in categories, word boundaries, stable output and limits of keyword detection. */
class AllergenCheckerTest {
    @Test
    void findAllergens_commonIngredientNames_coversAllNineCategories() {
        Map<Allergen, String> examples = Map.of(
                Allergen.MILK, "cheese", Allergen.EGGS, "eggs", Allergen.FISH, "salmon",
                Allergen.CRUSTACEAN_SHELLFISH, "prawns", Allergen.TREE_NUTS, "almonds",
                Allergen.PEANUTS, "groundnuts", Allergen.WHEAT, "wheat flour",
                Allergen.SOYBEANS, "tofu", Allergen.SESAME, "tahini");

        for (Map.Entry<Allergen, String> example : examples.entrySet()) {
            assertEquals(Set.of(example.getKey()), find(example.getValue()), example.getValue());
        }
    }

    @Test
    void findAllergens_caseWhitespaceAndPunctuation_matchesWholePhrases() {
        assertEquals(Set.of(Allergen.TREE_NUTS, Allergen.SOYBEANS, Allergen.SESAME),
                find("roasted BRAZIL-NUTS", "Soy   Beans", "(SESAME) seeds"));
    }

    @Test
    void findAllergens_repeatedAndMixedCategories_returnsEachOnceInStableOrder() {
        Set<Allergen> actual = find("sesame oil", "eggs", "cheese", "milk", "egg", "sesame seeds");

        assertEquals(List.of(Allergen.MILK, Allergen.EGGS, Allergen.SESAME), new ArrayList<>(actual));
    }

    @Test
    void findAllergens_keywordsInsideOtherWords_doesNotMatch() {
        assertTrue(find("eggplant", "nutmeg", "butternut squash", "milkyway", "wheatgrass").isEmpty());
    }

    @Test
    void findAllergens_emptyOrUnrecognisedIngredients_returnsNoMatches() {
        assertTrue(AllergenChecker.findAllergens(List.of()).isEmpty());
        assertTrue(find("rice", "olive oil", "mystery sauce").isEmpty());
    }

    @Test
    void findAllergens_negationAndSubstitutes_remainLiteralKeywordMatches() {
        // The checker deliberately makes no inference about labels such as "free" or plant-based substitutes.
        assertEquals(Set.of(Allergen.MILK, Allergen.EGGS, Allergen.PEANUTS),
                find("coconut milk", "egg-free mix", "peanut butter"));
    }

    @Test
    void findAllergens_changedIngredients_recomputesWithoutChangingEarlierResult() {
        Recipe recipe = new Recipe("Test", "");
        recipe.addIngredient(new Ingredient("2 eggs"));
        Set<Allergen> original = AllergenChecker.findAllergens(recipe.getIngredients());
        recipe.addIngredient(new Ingredient("10 g cheese"));
        recipe.scaleToServingSize(3);

        assertEquals(Set.of(Allergen.EGGS), original);
        assertEquals(Set.of(Allergen.MILK, Allergen.EGGS),
                AllergenChecker.findAllergens(recipe.getIngredients()));
        assertEquals("6 eggs", recipe.getIngredients().get(0).getDescription());
        assertEquals("30 g cheese", recipe.getIngredients().get(1).getDescription());
        assertThrows(UnsupportedOperationException.class, () -> original.add(Allergen.MILK));
    }

    @Test
    void findAllergens_turkishDefaultLocale_stillMatchesEnglishNames() {
        Locale original = Locale.getDefault();
        try {
            Locale.setDefault(Locale.forLanguageTag("tr-TR"));
            assertEquals(Set.of(Allergen.FISH, Allergen.MILK), find("FISH", "MILK"));
        } finally {
            Locale.setDefault(original);
        }
    }

    /** Builds valid structured ingredients so the tests exercise names independently of quantity parsing. */
    private Set<Allergen> find(String... names) {
        List<Ingredient> ingredients = new ArrayList<>();
        for (String name : names) {
            ingredients.add(new Ingredient(1, null, name));
        }
        return AllergenChecker.findAllergens(ingredients);
    }
}
