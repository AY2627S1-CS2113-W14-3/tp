package seedu.whatscooking.recipe;

/**
 * Represents a single ingredient line within a {@link Recipe}, e.g. "3 eggs"
 * or "200g cheese".
 * <p>
 * This is a minimal stub: it stores the ingredient as one unparsed
 * description string. Person E is extending this class to split the
 * description into a quantity (amount + unit) and a name, so that recipes
 * can later be scaled or ingredients searched by name. The constructor
 * signature here is deliberately simple so other classes can start
 * depending on {@code Ingredient} immediately without waiting on that work.
 */
public class Ingredient {
    private final String description;

    public Ingredient(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return description;
    }
}
