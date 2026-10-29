package seedu.whatscooking.allergen;

/**
 * An immutable suggestion for one ingredient, without changing its quantity or the recipe.
 *
 * @param ingredientNumber the ingredient's 1-based position in the recipe
 * @param ingredientName the original name as entered by the user
 * @param replacement the alternative to consider, not a guarantee of suitability
 * @param usageNote the cooking context and limitations of the alternative
 */
public record SubstitutionSuggestion(int ingredientNumber, String ingredientName,
                                    String replacement, String usageNote) {
}
