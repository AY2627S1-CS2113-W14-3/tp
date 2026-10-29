package seedu.whatscooking.allergen;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.Test;

import seedu.whatscooking.recipe.Ingredient;
import seedu.whatscooking.recipe.Recipe;

/** Checks the documented catalogue boundaries, stable numbering and read-only advice contract. */
class SubstitutionAdvisorTest {
    @Test
    void suggest_supportedFoods_returnsContextForEachRule() {
        List<SubstitutionSuggestion> result = suggest("milk", "butter", "eggs", "peanut butter",
                "almond butter", "wheat flour");

        assertEquals(List.of("rice drink", "milk-free margarine", "mashed banana", "sunflower seed butter",
                "sunflower seed butter", "wheat-free flour blend"),
                result.stream().map(SubstitutionSuggestion::replacement).toList());
        assertTrue(result.stream().allMatch(suggestion -> !suggestion.usageNote().isBlank()));
        assertTrue(result.get(2).usageNote().contains("Not for omelettes or meringues"));
    }

    @Test
    void suggest_wholeNameAliases_normalizesCaseWhitespaceAndHyphens() {
        List<SubstitutionSuggestion> result = suggest("COW'S MILK", "egg", "cashew butter", "WHOLE   WHEAT-FLOUR");

        assertEquals(4, result.size());
        assertEquals("rice drink", result.get(0).replacement());
        assertEquals("WHOLE   WHEAT-FLOUR", result.get(3).ingredientName());
        assertEquals("wheat-free flour blend", result.get(3).replacement());
    }

    @Test
    void suggest_compoundAndNegatedNames_doesNotGuessFromAllergenKeywords() {
        assertTrue(suggest("coconut milk", "egg-free mix", "eggplant", "salted butter",
                "peanut butter sauce", "milk chocolate", "fish", "mystery sauce").isEmpty());
    }

    @Test
    void suggest_supportedNameAfterUnknownAndDuplicate_keepsOriginalNumbersAndOrder() {
        List<SubstitutionSuggestion> result = suggest("rice", "peanut butter", "milk", "milk");

        assertEquals(List.of(2, 3, 4), result.stream().map(SubstitutionSuggestion::ingredientNumber).toList());
        assertEquals(List.of("peanut butter", "milk", "milk"),
                result.stream().map(SubstitutionSuggestion::ingredientName).toList());
        assertEquals("sunflower seed butter", result.get(0).replacement());
    }

    @Test
    void suggest_scaledRecipe_preservesObjectsExactQuantityAndEarlierResults() {
        Recipe recipe = new Recipe("Cake", "dessert");
        Ingredient milk = new Ingredient("1 l milk");
        recipe.addIngredient(milk);
        recipe.setServingSize(3);
        recipe.scaleToServingSize(1);

        List<SubstitutionSuggestion> original = SubstitutionAdvisor.suggest(recipe.getIngredients());
        assertSame(milk, recipe.getIngredients().get(0));
        assertEquals(1, recipe.getServingSize());
        recipe.addIngredient(new Ingredient("2 eggs"));
        assertEquals(2, SubstitutionAdvisor.suggest(recipe.getIngredients()).size());
        assertEquals(1, original.size());
        assertThrows(UnsupportedOperationException.class, () -> original.clear());
        recipe.scaleToServingSize(3);
        assertEquals("1 l milk", milk.getDisplayDescription());
    }

    @Test
    void suggest_emptyList_returnsEmptyImmutableResult() {
        List<SubstitutionSuggestion> result = SubstitutionAdvisor.suggest(List.of());

        assertTrue(result.isEmpty());
        assertThrows(UnsupportedOperationException.class, () -> result.add(suggest("milk").get(0)));
    }

    @Test
    void suggest_turkishDefaultLocale_doesNotChangeEnglishMatching() {
        Locale original = Locale.getDefault();
        try {
            Locale.setDefault(Locale.forLanguageTag("tr-TR"));
            assertEquals("rice drink", suggest("MILK").get(0).replacement());
        } finally {
            Locale.setDefault(original);
        }
    }

    /** Creates structured ingredients without making quantity or unit text part of matching. */
    private List<SubstitutionSuggestion> suggest(String... names) {
        return SubstitutionAdvisor.suggest(List.of(names).stream()
                .map(name -> new Ingredient(1, null, name)).toList());
    }
}
