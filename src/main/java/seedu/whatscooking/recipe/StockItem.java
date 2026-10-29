package seedu.whatscooking.recipe;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Represents an ingredient in the stock, optionally with an expiry date.
 */
public class StockItem {
    private final Ingredient ingredient;
    private final LocalDate expiryDate; // Can be null if no expiry date is provided

    public StockItem(Ingredient ingredient, LocalDate expiryDate) {
        this.ingredient = ingredient;
        this.expiryDate = expiryDate;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public String getName() {
        return ingredient.getName();
    }

    public String getUnit() {
        return ingredient.getUnit();
    }

    public double getQuantity() {
        return ingredient.getQuantity();
    }

    public void setQuantity(double quantity) {
        ingredient.setQuantity(quantity);
    }

    /**
     * Returns true if the item is already expired.
     */
    public boolean isExpired() {
        return expiryDate != null && expiryDate.isBefore(LocalDate.now());
    }

    /**
     * Returns true if the item expires within the next 3 days.
     */
    public boolean isExpiringSoon() {
        if (expiryDate == null) {
            return false;
        }
        long daysUntilExpiry = ChronoUnit.DAYS.between(LocalDate.now(), expiryDate);
        return daysUntilExpiry >= 0 && daysUntilExpiry <= 3;
    }

    /**
     * Checks if this stock item has the same expiry date as another.
     * Two items with null expiry dates are considered to have the same expiry.
     *
     * @param other the other stock item to compare with
     * @return true if both have the same expiry date, false otherwise
     */
    public boolean isSameExpiry(StockItem other) {
        if (this.expiryDate == null && other.expiryDate == null) {
            return true;
        }
        if (this.expiryDate == null || other.expiryDate == null) {
            return false;
        }
        return this.expiryDate.equals(other.expiryDate);
    }
}
