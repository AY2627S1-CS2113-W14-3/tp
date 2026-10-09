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
    private static final Pattern DESCRIPTION_PATTERN =
            Pattern.compile("([0-9]+(?:\\.[0-9]+)?|\\.[0-9]+)([A-Za-z]*)\\s+(.+)");

    // Recognising units explicitly keeps "2 red onions" from treating "red" as a unit.
    private static final Set<String> UNITS = Set.of(
            "g", "kg", "mg", "ml", "l", "tsp", "tbsp", "cup", "cups", "oz", "lb", "lbs");
    private static final String INVALID_DESCRIPTION =
            "Use a positive quantity and an ingredient name, e.g. '200 g cheese' or '3 eggs'.";

    private double quantity;
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

        double parsedQuantity = Double.parseDouble(matcher.group(1));
        validateQuantity(parsedQuantity);
        String parsedUnit = matcher.group(2);
        String parsedName = matcher.group(3).strip();

        if (parsedUnit.isEmpty()) {
            String[] nameParts = parsedName.split("\\s+", 2);
            if (isUnit(nameParts[0])) {
                if (nameParts.length < 2) {
                    throw new IllegalArgumentException(INVALID_DESCRIPTION);
                }
                parsedUnit = nameParts[0];
                parsedName = nameParts[1].strip();
            }
        } else if (!isUnit(parsedUnit)) {
            throw new IllegalArgumentException("Unsupported ingredient unit: " + parsedUnit);
        }

        validateName(parsedName);
        this.quantity = parsedQuantity;
        this.unit = parsedUnit.isEmpty() ? null : parsedUnit;
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
        this.quantity = quantity;
        this.unit = unit == null || unit.isBlank() ? null : unit.strip();
        this.name = name.strip();
    }

    /**
     * Returns the ingredient quantity.
     *
     * @return the quantity
     */
    public double getQuantity() {
        return quantity;
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
        this.quantity = quantity;
    }

    /**
     * Returns the current ingredient description without unnecessary decimal zeroes.
     * Rebuilds the description so it reflects later quantity changes.
     *
     * @return the quantity, optional unit and name
     */
    public String getDescription() {
        String amount = BigDecimal.valueOf(quantity).stripTrailingZeros().toPlainString();
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
