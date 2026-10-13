package seedu.whatscooking.recipe;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * Stores an ingredient amount as an exact fraction so repeated resizing does not lose precision.
 * Only the display of a non-terminating decimal is rounded.
 */
final class IngredientQuantity {
    private final BigInteger numerator;
    private final BigInteger denominator;

    /**
     * Converts a positive decimal to a fraction without rounding its digits.
     * A negative scale represents trailing integer zeroes, such as 1E+3.
     *
     * @param quantity the positive decimal amount
     */
    IngredientQuantity(BigDecimal quantity) {
        this(quantity.unscaledValue().multiply(BigInteger.TEN.pow(Math.max(-quantity.scale(), 0))),
                BigInteger.TEN.pow(Math.max(quantity.scale(), 0)));
    }

    /**
     * Reduces a positive fraction so its numerator and denominator stay small across repeated changes.
     */
    private IngredientQuantity(BigInteger numerator, BigInteger denominator) {
        assert numerator.signum() > 0 : "Ingredient quantities must be positive";
        assert denominator.signum() > 0 : "An ingredient quantity must have a positive denominator";
        BigInteger commonFactor = numerator.gcd(denominator);
        this.numerator = numerator.divide(commonFactor);
        this.denominator = denominator.divide(commonFactor);
    }

    /**
     * Returns a scaled copy while leaving this quantity unchanged.
     *
     * @param currentServingSize the positive current number of servings
     * @param newServingSize the positive target number of servings
     * @return the exact scaled amount
     */
    IngredientQuantity scale(int currentServingSize, int newServingSize) {
        assert currentServingSize > 0 : "Current serving size must be positive";
        assert newServingSize > 0 : "New serving size must be positive";
        return new IngredientQuantity(numerator.multiply(BigInteger.valueOf(newServingSize)),
                denominator.multiply(BigInteger.valueOf(currentServingSize)));
    }

    /**
     * Returns an approximation for the existing numeric API and finite-range validation.
     * Scaling and display use the fraction itself, not this approximation.
     *
     * @return the quantity as a double
     */
    double doubleValue() {
        return new BigDecimal(numerator).divide(new BigDecimal(denominator), MathContext.DECIMAL128).doubleValue();
    }

    /**
     * Displays finite decimals in full and rounds repeating decimals to at most two places.
     *
     * @return the decimal amount without unnecessary trailing zeroes
     */
    String toDisplayString() {
        BigDecimal decimalNumerator = new BigDecimal(numerator);
        BigDecimal decimalDenominator = new BigDecimal(denominator);
        BigDecimal amount;
        try {
            // Exact division succeeds only when the decimal expansion terminates.
            amount = decimalNumerator.divide(decimalDenominator);
        } catch (ArithmeticException e) {
            amount = decimalNumerator.divide(decimalDenominator, 2, RoundingMode.HALF_UP);
        }
        return amount.stripTrailingZeros().toPlainString();
    }
}
