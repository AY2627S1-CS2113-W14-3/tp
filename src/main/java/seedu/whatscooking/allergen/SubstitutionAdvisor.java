package seedu.whatscooking.allergen;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import seedu.whatscooking.AppLogger;
import seedu.whatscooking.recipe.Ingredient;

/**
 * Looks up cooking alternatives in a small, explicit catalogue of ingredient names.
 * Unlike the broad allergen warning, a suggestion requires a complete name match so that
 * "coconut milk" and "egg-free mix" are not treated as milk and eggs. Sources and supported
 * names are documented in the User Guide. Suggestions never establish allergy safety.
 */
public final class SubstitutionAdvisor {
    private static final Logger logger = AppLogger.getLogger(SubstitutionAdvisor.class);

    /** Fixed cooking ideas; no rule supplies a quantity conversion or modifies a recipe. */
    private static final List<Rule> RULES = List.of(
            new Rule(List.of("milk", "cow milk", "cow's milk"), "rice drink",
                    "For cooking or baking; flavour and nutrition differ from cow's milk."),
            new Rule(List.of("butter"), "milk-free margarine",
                    "For spreading or baking; check the label and the product's baking instructions."),
            new Rule(List.of("egg", "eggs"), "mashed banana",
                    "For some baked goods only; adds banana flavour. Not for omelettes or meringues."),
            new Rule(List.of("peanut butter"), "sunflower seed butter",
                    "For spreads or suitable baking recipes; check seed allergies and product labels."),
            new Rule(List.of("almond butter", "cashew butter"), "sunflower seed butter",
                    "For spreads or suitable baking recipes; check seed allergies and product labels."),
            new Rule(List.of("wheat flour", "whole wheat flour"), "wheat-free flour blend",
                    "Use a tested recipe and follow the blend's instructions; check every ingredient."));

    private SubstitutionAdvisor() {
        // Utility class: the catalogue is immutable and results are derived on each request.
    }

    /**
     * Returns suggestions in recipe ingredient order, retaining each ingredient's display number.
     * Unsupported names are omitted. An empty list means there is no catalogue entry, not that
     * the recipe is safe. No quantities, ingredients, stock or stored recipe fields are changed.
     *
     * @param ingredients the current validated recipe ingredients
     * @return an immutable list of independent suggestion values
     */
    public static List<SubstitutionSuggestion> suggest(List<Ingredient> ingredients) {
        assert ingredients != null : "A recipe must supply its ingredient list";
        List<SubstitutionSuggestion> suggestions = new ArrayList<>();
        for (int index = 0; index < ingredients.size(); index++) {
            Ingredient ingredient = ingredients.get(index);
            assert ingredient != null : "Recipe ingredients must not contain null entries";
            String name = normalize(ingredient.getName());
            for (Rule rule : RULES) {
                if (rule.names().contains(name)) {
                    suggestions.add(new SubstitutionSuggestion(index + 1, ingredient.getName(),
                            rule.replacement(), rule.usageNote()));
                    break;
                }
            }
        }
        logger.log(Level.FINE, "Suggested alternatives for {0} of {1} ingredients",
                new Object[]{suggestions.size(), ingredients.size()});
        return List.copyOf(suggestions);
    }

    /** Normalizes case, whitespace and hyphens while preserving the complete ingredient name. */
    private static String normalize(String name) {
        return name.toLowerCase(Locale.ROOT).strip().replaceAll("[\\s-]+", " ");
    }

    /** One catalogue entry with explicit aliases and a context-specific explanation. */
    private record Rule(List<String> names, String replacement, String usageNote) {
    }
}
