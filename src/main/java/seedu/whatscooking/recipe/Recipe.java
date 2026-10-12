package seedu.whatscooking.recipe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a single recipe: a title, a type (e.g. "dessert", "main"), a
 * serving size, a list of ingredients, a list of preparation steps, and an
 * optional free-text note.
 */
public class Recipe {
    private static final int DEFAULT_SERVING_SIZE = 1;

    private final String title;
    private final String type;
    private int servingSize;
    private final ArrayList<Ingredient> ingredients;
    private final ArrayList<String> steps;
    private String note;

    /**
     * Constructs a recipe with the default serving size and no note.
     * Ingredients and steps start empty and are expected to be added
     * afterwards via {@link #addIngredient(Ingredient)} and
     * {@link #addStep(String)}.
     *
     * @param title the name of the recipe, e.g. "Chocolate Cake"
     * @param type  the category of the recipe, e.g. "dessert"
     */
    public Recipe(String title, String type) {
        assert title != null : "Recipe title should never be null";
        assert type != null : "Recipe type should never be null; use \"\" when the user gave none";

        this.title = title;
        this.type = type;
        this.servingSize = DEFAULT_SERVING_SIZE;
        this.ingredients = new ArrayList<>();
        this.steps = new ArrayList<>();
        this.note = "";
    }

    public String getTitle() {
        return title;
    }

    public String getType() {
        return type;
    }

    public int getServingSize() {
        return servingSize;
    }

    public void setServingSize(int servingSize) {
        this.servingSize = servingSize;
    }

    /**
     * Returns the ingredients in the order they were added. The list is
     * read-only, so callers must go through {@link #addIngredient(Ingredient)}
     * to change it rather than modifying the returned list directly.
     *
     * @return an unmodifiable view of this recipe's ingredients
     */
    public List<Ingredient> getIngredients() {
        return Collections.unmodifiableList(ingredients);
    }

    public void addIngredient(Ingredient ingredient) {
        ingredients.add(ingredient);
    }

    /**
     * Returns the preparation steps in the order they were added. The list is
     * read-only, so callers must go through {@link #addStep(String)} to change
     * it rather than modifying the returned list directly.
     *
     * @return an unmodifiable view of this recipe's steps
     */
    public List<String> getSteps() {
        return Collections.unmodifiableList(steps);
    }

    public void addStep(String step) {
        steps.add(step);
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public String toString() {
        return title + " (" + type + ", serves " + servingSize + ")";
    }
}
