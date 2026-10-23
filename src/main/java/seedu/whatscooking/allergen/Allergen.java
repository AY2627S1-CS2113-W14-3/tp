package seedu.whatscooking.allergen;

import java.util.List;
import java.util.Locale;

/**
 * Defines the nine built-in allergen categories and their English ingredient-name keywords.
 * A keyword match is a reminder to check the ingredient label, not a food-safety assessment.
 * See the User Guide for the complete list and the limits of name-based matching.
 */
public enum Allergen {
    MILK("milk", "milk", "butter", "cheese", "cream", "yogurt", "yoghurt", "whey", "casein"),
    EGGS("eggs", "egg", "eggs"),
    FISH("fish", "fish", "salmon", "tuna", "cod", "anchovy", "anchovies", "sardine", "sardines"),
    CRUSTACEAN_SHELLFISH("crustacean shellfish", "shrimp", "shrimps", "prawn", "prawns", "crab", "crabs",
            "lobster", "lobsters", "crayfish", "crawfish", "crustacean shellfish"),
    TREE_NUTS("tree nuts", "almond", "almonds", "walnut", "walnuts", "cashew", "cashews", "pecan", "pecans",
            "pistachio", "pistachios", "hazelnut", "hazelnuts", "brazil nut", "brazil nuts",
            "macadamia", "macadamias", "tree nut", "tree nuts"),
    PEANUTS("peanuts", "peanut", "peanuts", "groundnut", "groundnuts"),
    WHEAT("wheat", "wheat", "semolina", "couscous", "bulgur"),
    SOYBEANS("soybeans", "soy", "soya", "soybean", "soybeans", "soy bean", "soy beans", "tofu", "edamame"),
    SESAME("sesame", "sesame", "tahini");

    private final String displayName;
    private final List<String> keywords;

    /** Stores a stable display label and an immutable list of lowercase keywords. */
    Allergen(String displayName, String... keywords) {
        this.displayName = displayName;
        this.keywords = List.of(keywords);
    }

    /** Returns the category label used in recipe warnings. */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Matches whole words or phrases, ignoring case and treating punctuation as a separator.
     * Padding both sides with spaces prevents "egg" from matching "eggplant".
     *
     * @param ingredientName the ingredient name without its quantity or unit
     * @return whether at least one keyword appears in the name
     */
    boolean matches(String ingredientName) {
        assert ingredientName != null : "Ingredient names must be present before allergen checking";
        String normalized = " " + ingredientName.toLowerCase(Locale.ROOT)
                .replaceAll("[^\\p{L}\\p{N}]+", " ").strip() + " ";
        for (String keyword : keywords) {
            if (normalized.contains(" " + keyword + " ")) {
                return true;
            }
        }
        return false;
    }
}
