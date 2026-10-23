package seedu.whatscooking.allergen;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import seedu.whatscooking.AppLogger;
import seedu.whatscooking.recipe.Ingredient;

/**
 * Derives possible allergens from the current ingredient names without changing the recipe.
 * Results are recomputed on demand, so loading a recipe or changing its quantities needs no cached flags.
 */
public final class AllergenChecker {
    private static final Logger logger = AppLogger.getLogger(AllergenChecker.class);

    private AllergenChecker() {
        // Utility class: there is no per-recipe or global mutable detection state.
    }

    /**
     * Finds each matching category once, in the declaration order of {@link Allergen}.
     * An empty result means no built-in keyword matched; it does not establish that food is safe.
     *
     * @param ingredients validated ingredients belonging to a recipe
     * @return an unmodifiable set of possible allergens
     */
    public static Set<Allergen> findAllergens(List<Ingredient> ingredients) {
        assert ingredients != null : "A recipe must supply its ingredient list";
        EnumSet<Allergen> matches = EnumSet.noneOf(Allergen.class);
        for (Ingredient ingredient : ingredients) {
            assert ingredient != null : "Recipe ingredients must not contain null entries";
            for (Allergen allergen : Allergen.values()) {
                if (allergen.matches(ingredient.getName())) {
                    matches.add(allergen);
                }
            }
        }
        logger.log(Level.FINE, "Matched {0} allergen categories across {1} ingredient names",
                new Object[]{matches.size(), ingredients.size()});
        return Collections.unmodifiableSet(matches);
    }
}
