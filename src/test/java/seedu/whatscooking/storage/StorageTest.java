package seedu.whatscooking.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.whatscooking.WhatsCookingException;
import seedu.whatscooking.recipe.Ingredient;
import seedu.whatscooking.recipe.Recipe;
import seedu.whatscooking.recipe.RecipeList;

/**
 * Tests for {@link Storage}: recipes round-trip through a save and a load
 * with every field intact, and a missing or corrupted file is handled
 * without crashing.
 */
class StorageTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    public void load_missingFile_returnsEmptyList() throws WhatsCookingException {
        Storage storage = new Storage(temporaryDirectory.resolve("whatscooking.txt").toString());

        // input:    a file path that has never been saved to
        // actual:   storage.load()
        // expected: an empty list, not an exception
        RecipeList recipes = storage.load();
        assertTrue(recipes.isEmpty());
    }

    @Test
    public void saveThenLoad_variedRecipes_restoresEveryField() throws WhatsCookingException {
        Storage storage = new Storage(temporaryDirectory.resolve("whatscooking.txt").toString());

        RecipeList original = new RecipeList();
        Recipe eggTaco = new Recipe("Egg Taco", "snack");
        eggTaco.addIngredient(new Ingredient("3 eggs"));
        eggTaco.addIngredient(new Ingredient("200g cheese"));
        eggTaco.addStep("Scramble egg");
        eggTaco.addStep("Put egg in tortilla");
        eggTaco.setNote("Best eaten hot");
        eggTaco.setServingSize(2);
        original.add(eggTaco);
        original.add(new Recipe("Toast", "")); // no type, no ingredients, no steps, no note

        storage.save(original);
        RecipeList loaded = storage.load();

        // input:    2 recipes saved, one fully populated, one minimal
        // actual:   storage.load()
        // expected: both recipes restored with every field intact
        assertEquals(2, loaded.size());

        Recipe loadedEggTaco = loaded.get(0);
        assertEquals("Egg Taco", loadedEggTaco.getTitle());
        assertEquals("snack", loadedEggTaco.getType());
        assertEquals(2, loadedEggTaco.getServingSize());
        assertEquals(2, loadedEggTaco.getIngredients().size());
        assertEquals("3 eggs", loadedEggTaco.getIngredients().get(0).getDescription());
        assertEquals("200 g cheese", loadedEggTaco.getIngredients().get(1).getDescription());
        assertEquals(2, loadedEggTaco.getSteps().size());
        assertEquals("Scramble egg", loadedEggTaco.getSteps().get(0));
        assertEquals("Put egg in tortilla", loadedEggTaco.getSteps().get(1));
        assertEquals("Best eaten hot", loadedEggTaco.getNote());

        Recipe loadedToast = loaded.get(1);
        assertEquals("Toast", loadedToast.getTitle());
        assertEquals("", loadedToast.getType());
        assertEquals(0, loadedToast.getIngredients().size());
        assertEquals(0, loadedToast.getSteps().size());
        assertEquals("", loadedToast.getNote());
    }

    @Test
    public void save_newDataDirectory_createsParentDirectories() throws WhatsCookingException {
        Storage storage = new Storage(temporaryDirectory.resolve("nested/dir/whatscooking.txt").toString());
        RecipeList recipes = new RecipeList();
        recipes.add(new Recipe("Pancakes", "breakfast"));

        // input:    a file path whose parent directories do not exist yet
        // expected: save() creates them rather than throwing
        storage.save(recipes);
        assertEquals(1, storage.load().size());
    }

    @Test
    public void load_corruptedFile_throwsException() throws IOException {
        Path file = temporaryDirectory.resolve("whatscooking.txt");
        Files.writeString(file, "Egg Taco\nsnack\nnot-a-number\n");
        Storage storage = new Storage(file.toString());

        // input:    a data file whose serving-size line is not a number
        // expected: WhatsCookingException, not a crash
        assertThrows(WhatsCookingException.class, storage::load);
    }
}
