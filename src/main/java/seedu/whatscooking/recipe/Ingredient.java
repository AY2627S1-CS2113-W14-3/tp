package seedu.whatscooking.recipe;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Stores an ingredient's quantity, optional unit and name separately.
 * Quantity can change when a recipe is scaled, while the unit and name stay the same.
 */
public class Ingredient {
    // Capture-group numbers follow the quantity, attached unit and name in DESCRIPTION_PATTERN.
    private static final int QUANTITY_GROUP = 1;
    private static final int UNIT_GROUP = 2;
    private static final int NAME_GROUP = 3;
    private static final int UNIT_AND_NAME_PART_COUNT = 2;
    private static final int UNIT_PART_INDEX = 0;
    private static final int NAME_PART_INDEX = 1;

    private static final Pattern DESCRIPTION_PATTERN =
            Pattern.compile("([0-9]+(?:\\.[0-9]+)?|\\.[0-9]+)([A-Za-z]*)\\s+(.+)");

    // Recognising units explicitly keeps "2 red onions" from treating "red" as a unit.
    private static final Set<String> UNITS = Set.of(
            "g", "kg", "mg", "ml", "l", "tsp", "tbsp", "cup", "cups", "oz", "lb", "lbs");
    private static final String INVALID_DESCRIPTION =
            "Use a positive quantity and an ingredient name, e.g. '200 g cheese' or '3 eggs'.";

    /** Exact amount used for scaling; converting to double is reserved for the numeric getter. */
    private IngredientQuantity quantity;
    private final String unit;
    private final String name;

    /**
     * Parses an ingredient such as {@code 200 g cheese}, {@code 200g cheese} or {@code 3 eggs}.
     * Supports decimal quantities and multi-word names. Recognised units are g, kg, mg, ml, l,
     * tsp, tbsp, cup, cups, oz, lb and lbs, matched without regard to case.
     *
     * @param description the quantity, optional unit and ingredient name
     * @throws IllegalArgumentException if the description is missing or malformed, or its quantity is not positive
     */
    public Ingredient(String description) {
        if (description == null) {
            throw new IllegalArgumentException(INVALID_DESCRIPTION);
        }
        Matcher matcher = DESCRIPTION_PATTERN.matcher(description.strip());
        if (!matcher.matches()) {
            throw new IllegalArgumentException(INVALID_DESCRIPTION);
        }

        double parsedQuantity = Double.parseDouble(matcher.group(QUANTITY_GROUP));
        validateQuantity(parsedQuantity);
        String parsedUnit = matcher.group(UNIT_GROUP);
        String parsedName = matcher.group(NAME_GROUP).strip();

        if (parsedUnit.isEmpty()) {
            String[] nameParts = parsedName.split("\\s+", UNIT_AND_NAME_PART_COUNT);
            if (isUnit(nameParts[UNIT_PART_INDEX])) {
                if (nameParts.length < UNIT_AND_NAME_PART_COUNT) {
                    throw new IllegalArgumentException(INVALID_DESCRIPTION);
                }
                parsedUnit = nameParts[UNIT_PART_INDEX];
                parsedName = nameParts[NAME_PART_INDEX].strip();
            }
        } else if (!isUnit(parsedUnit)) {
            throw new IllegalArgumentException("Unsupported ingredient unit: " + parsedUnit);
        }

        validateName(parsedName);
        this.quantity = new IngredientQuantity(new BigDecimal(matcher.group(QUANTITY_GROUP)));
        if (parsedUnit.isEmpty()) {
            this.unit = null;
        } else {
            this.unit = parsedUnit;
        }
        this.name = parsedName;
    }

    /**
     * Creates an ingredient from separate values, including units beyond the text parser's supported list.
     *
     * @param quantity the positive, finite quantity
     * @param unit the unit, or null or blank when the ingredient is counted without a unit
     * @param name the non-blank ingredient name
     * @throws IllegalArgumentException if the quantity or name is invalid
     */
    public Ingredient(double quantity, String unit, String name) {
        validateQuantity(quantity);
        validateName(name);
        this.quantity = new IngredientQuantity(BigDecimal.valueOf(quantity));
        if (unit == null || unit.isBlank()) {
            this.unit = null;
        } else {
            this.unit = unit.strip();
        }
        this.name = name.strip();
    }

    /**
     * Returns the ingredient quantity as a double for callers that need a numeric value.
     * Repeating decimals are approximated here; resizing retains the exact quantity internally.
     *
     * @return the quantity
     */
    public double getQuantity() {
        return quantity.doubleValue();
    }

    /**
     * Returns the unit, or null for ingredients counted without a unit.
     *
     * @return the optional unit
     */
    public String getUnit() {
        return unit;
    }

    /**
     * Returns the ingredient name without its quantity or unit.
     *
     * @return the ingredient name
     */
    public String getName() {
        return name;
    }

    /**
     * Updates the quantity without changing the unit or name.
     * Rejects an invalid quantity before changing the ingredient.
     *
     * @param quantity the new positive, finite quantity
     * @throws IllegalArgumentException if the quantity is zero, negative or non-finite
     */
    public void setQuantity(double quantity) {
        validateQuantity(quantity);
        this.quantity = new IngredientQuantity(BigDecimal.valueOf(quantity));
    }

    /**
     * Applies an exact quantity after the recipe has validated every scaled ingredient.
     *
     * @param quantity the positive, finite amount to store
     */
    void setQuantity(IngredientQuantity quantity) {
        validateQuantity(quantity.doubleValue());
        this.quantity = quantity;
    }

    /**
     * Returns the immutable exact amount for recipe resizing.
     *
     * @return the exact ingredient quantity
     */
    IngredientQuantity getExactQuantity() {
        return quantity;
    }

    /**
     * Returns the current ingredient description using the same precision rules as console output.
     *
     * @return the quantity, optional unit and name
     */
    public String getDescription() {
        return getDisplayDescription();
    }

    /**
     * Displays finite decimals in full, rounding only repeating decimals to at most two places.
     * Keeps the exact stored quantity unchanged for later resizing.
     *
     * @return the formatted quantity, optional unit and ingredient name
     */
    public String getDisplayDescription() {
        String amount = quantity.toDisplayString();
        return amount + " " + (unit == null ? "" : unit + " ") + name;
    }

    @Override
    public String toString() {
        return getDescription();
    }

    /**
     * Checks whether a word is a supported measurement unit.
     */
    private static boolean isUnit(String word) {
        return UNITS.contains(word.toLowerCase(Locale.ROOT));
    }

    /**
     * Rejects quantities that cannot represent a usable ingredient amount.
     */
    private static void validateQuantity(double quantity) {
        if (!Double.isFinite(quantity) || quantity <= 0) {
            throw new IllegalArgumentException("Ingredient quantity must be a positive, finite number.");
        }
    }

    /**
     * Rejects missing ingredient names.
     */
    private static void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Ingredient name cannot be blank.");
        }
    }
}
