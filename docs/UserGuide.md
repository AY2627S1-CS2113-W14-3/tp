# User Guide

## Introduction

{Give a product intro}

## Quick Start

{Give steps to get started quickly}

1. Ensure that you have Java 25 or above installed.
1. Down the latest version of `Duke` from [here](http://link.to/duke).

## Features

{Give detailed description of each feature}

### Adding a recipe: `add`
Adds a new recipe to your recipe list.

Format: `add TITLE[, TYPE] [s/SERVINGS] [i/INGREDIENT, INGREDIENT...] [st/STEP]... [n/NOTE]`

* `TITLE` is required. Everything else is optional.
* Each recipe must have its own title. A title that is already used is rejected, ignoring letter case,
  so `Egg Taco` and `egg taco` count as the same title.
* `TYPE` goes after the first comma, e.g. `dessert`, `main`, `snack`. If you type a comma, the type cannot be left empty.
* Parts must be in this order: title and type, then `s/`, then `i/`, then `st/`, then `n/`.
* **Serving size (`s/`)**: how many people the recipe serves. It must be a positive whole number, e.g. `s/4`.
  If you leave it out, the recipe serves 1. The `modify` command uses this number when scaling ingredients.
* **Ingredients (`i/`)**: give all ingredients after a single `i/`, separated by commas.
  * Each ingredient is `QUANTITY [UNIT] NAME`, e.g. `3 eggs`, `200 g cheese`, `200g cheese`, `1.5 tbsp olive oil`.
  * `QUANTITY` must be a positive number. Decimals are allowed.
  * Supported units: `g`, `kg`, `mg`, `ml`, `l`, `tsp`, `tbsp`, `cup`, `cups`, `oz`, `lb`, `lbs` (not case-sensitive).
* **Steps (`st/`)**: start each step with its own `st/`. Steps are saved in the order you type them and cannot be empty.
* **Note (`n/`)**: an optional free-text note. It must come last, only one is allowed, and it cannot be empty.
* If any part is invalid, the recipe is **not** added, and an error message explains what to fix.
* After a successful add, matching ingredient names produce a [possible-allergen warning](#built-in-allergen-warnings).
  The warning does not prevent the recipe from being added.

Examples:

* `add egg taco` adds a recipe with only a title.
* `add egg taco, snack i/3 eggs, 5 tortillas st/Scramble egg st/Put egg in tortilla` adds a snack with 2 ingredients and 2 steps.
* `add garlic bread, side s/4 i/2 tbsp butter st/Spread butter st/Toast` adds a recipe that serves 4.
* `add omelette, breakfast i/3 eggs, 200g cheese, 1.5 tbsp olive oil st/Whisk eggs st/Cook in pan n/Best eaten hot` adds a recipe with a note.

Expected output (for the second example):

```
____________________________________________________________
egg taco, snack added!
____________________________________________________________
____________________________________________________________
Possible allergens: eggs. Check ingredient labels.
____________________________________________________________
```

Expected output when the title is already used:

```
____________________________________________________________
Error: A recipe called "egg taco" already exists. Use a different title, or edit the existing recipe.
____________________________________________________________
```

> **Caution:** Unlike some other commands, the parts of `add` must be typed in order. For example, `add toast n/Serve hot st/Toast bread` is rejected because the note is not last.

### Editing a recipe: `edit`

Changes the title, type and/or note of an existing recipe. Ingredients, steps and serving size stay the same.

Format: `edit INDEX [title/NEW_TITLE] [type/NEW_TYPE] [n/NEW_NOTE]`

* `INDEX` is the positive, 1-based number shown by `view`.
* Give at least one of `title/`, `type/` and `n/`. They can be in any order, but each only once.
* The new values replace the old ones and cannot be empty.
* The new title cannot be one that another recipe already uses (ignoring letter case).
  Changing only the letter case of a recipe's own title, e.g. `egg taco` to `Egg Taco`, is allowed.
* If any part is invalid, the recipe is **not** changed.
* To change the serving size, use [`modify`](#modifying-a-recipe-modify) instead.

Examples:

* `edit 1 title/egg wrap` renames recipe 1 to `egg wrap`.
* `edit 3 type/side n/Add cheese before toasting` changes recipe 3's type and note.

Expected output (for `edit 3 title/Cheesy garlic bread n/Add cheese before toasting`):

```
____________________________________________________________
Updated recipe 3:
Cheesy garlic bread (side, serves 4)
Note: Add cheese before toasting
____________________________________________________________
```

Possible errors:

* `edit 9 type/main` when there are only 3 recipes: `Error: Recipe 9 does not exist; you have 3 recipe(s).`
* `edit 1 title/pancakes` when another recipe is called `pancakes`:
  `Error: A recipe called "pancakes" already exists. Choose a different title.`
* `edit 1` or `edit 1 egg wrap`: an error showing the correct format.

### Retrieving a recipe: `retrieve`

Displays a recipe's title, type, serving size, ingredients, steps and optional note.
Matching ingredient names also produce the possible-allergen warning described below.

Format: `retrieve INDEX` or `retrieve RECIPE_NAME`

* `INDEX` is the positive, 1-based number shown by `view`.
* Alternatively, use the complete recipe name; matching ignores letter case.
* The same allergen check applies to both forms, including recipes loaded from a previous session.

Examples: `retrieve 1` or `retrieve egg taco`.

### Deleting a recipe: `delete`

Removes a recipe from your recipe list permanently.

Format: `delete INDEX` or `delete RECIPE_NAME`

* `INDEX` is the positive, 1-based number shown by the `view` command.
* Alternatively, use the complete recipe name. Matching is case-insensitive.
* You cannot delete a recipe that does not exist.

Examples:
* `delete 1` deletes the first recipe in the list.
* `delete egg taco` deletes the recipe named "Egg Taco".

Expected output for `delete egg taco`:

```text
Deleted: egg taco (snack, serves 1)
```

### Managing home stock: `stock`

Tracks the ingredients you currently have at home so you know what you can cook. You can add items you bought, remove items you used, and view your current inventory. The stock feature automatically ignores plural forms (e.g., `egg` and `eggs` are treated as the same item).

Format: `stock add INGREDIENT`, `stock remove INGREDIENT`, or `stock list`

* `stock add`: Adds the specified ingredient to your stock. If the ingredient already exists, the quantities are summed.
* `stock remove`: Subtracts the specified quantity from your stock. If the quantity reaches zero, the item is removed.
* `stock list`: Displays all ingredients currently in your stock.
* **Ingredient Format**: Must follow the same format as recipe ingredients: `QUANTITY [UNIT] NAME` (e.g., `3 eggs`, `200g chicken`).

Examples:
* `stock add 3 eggs` adds 3 eggs to your stock.
* `stock add 200g chicken` adds 200 grams of chicken.
* `stock list` shows your current inventory.

Expected output for `stock list`:

```text
Current Home Stock:

3 eggs

200 g chicken
```

Expected output for `stock remove 5 eggs` (insufficient quantity):

```text
Error: Not enough eggs in stock. You only have 3.0
```

> **Caution:** The stock feature does not do unit conversions. If you add `1 kg chicken` and try to remove `500 g chicken`, the command will fail because the units (`kg` and `g`) do not match.

### Built-in allergen warnings

After a successful `add`, and when you `retrieve` a recipe, WhatsCooking checks the recorded ingredient
names against the fixed English keyword list below. No extra command or allergy profile is needed.

For example:

```text
add Omelette, breakfast s/2 i/2 eggs, 100 g cheese st/Cook
retrieve Omelette
```

Both commands include this line in their output:

```text
Possible allergens: milk, eggs. Check ingredient labels.
```

The nine categories follow the [USDA's explanation of the major food allergens](https://www.fsis.usda.gov/food-safety/safe-food-handling-and-preparation/food-safety-basics/food-allergies-big-9).
The keywords are this application's limited matching rules, not a complete ingredient database.

| Category | Recognised keywords and phrases |
| --- | --- |
| milk | milk, butter, cheese, cream, yogurt, yoghurt, whey, casein |
| eggs | egg, eggs |
| fish | fish, salmon, tuna, cod, anchovy, anchovies, sardine, sardines |
| crustacean shellfish | shrimp, shrimps, prawn, prawns, crab, crabs, lobster, lobsters, crayfish, crawfish, crustacean shellfish |
| tree nuts | almond, almonds, walnut, walnuts, cashew, cashews, pecan, pecans, pistachio, pistachios, hazelnut, hazelnuts, brazil nut, brazil nuts, macadamia, macadamias, tree nut, tree nuts |
| peanuts | peanut, peanuts, groundnut, groundnuts |
| wheat | wheat, semolina, couscous, bulgur |
| soybeans | soy, soya, soybean, soybeans, soy bean, soy beans, tofu, edamame |
| sesame | sesame, tahini |

Matching ignores case and treats punctuation and whitespace as separators. Thus `EGGS` matches eggs,
and `brazil-nuts` matches tree nuts, but `eggplant` does not match eggs. Each category appears at most
once, in the table's order. Only ingredient names are checked: recipe titles, types, steps and notes
are not scanned. Changing the serving size does not change the detected categories.

If no keyword matches, or a recipe has no ingredients, no warning is printed. **Absence of a warning
does not mean a recipe is allergen-free.** The checker cannot identify every allergen, hidden ingredients,
cross-contact or ingredients named in other languages. It also cannot understand substitutions or
negation: `coconut milk`, `peanut butter` and `egg-free mix` can produce extra warnings because their
names contain listed keywords. Check the actual ingredient labels and suitability yourself.

This version does not suggest or swap replacement ingredients. Warnings are recalculated from the
current ingredient names, including after saving and restarting the application.

### Searching for recipes: `search`
Searches recipes using one or more filters for the recipe name and/or its ingredients.

Format: `search f/{name/ingredient}/{KEYWORD} [f/{name/ingredient}/{KEYWORD} ...]`

* Use `f/name/KEYWORD` to match `KEYWORD` against a recipe name.
* Use `f/ingredient/KEYWORD` to match `KEYWORD` against an ingredient name.
* Matching is case-insensitive and compares the complete name or ingredient text.
* Multiple filters are combined with AND logic: a recipe must satisfy every filter.
* Separate filters with spaces. Extra spaces before, between, or after filters are allowed.
* The filter type and keyword are required. The filter must be either `name` or `ingredient`.

Examples:

* `search f/name/Omelette` finds the recipe whose name is `Omelette`, ignoring letter case.
* `search f/ingredient/chicken` finds recipes with an ingredient named `chicken`, ignoring letter case.
* `search f/name/Chicken f/ingredient/chicken` finds recipes whose name is `Chicken` and which contain an ingredient named `chicken`.

Invalid examples such as `search chicken`, `search f/type/chicken`, or `search f/name/` are rejected.
### Viewing all recipes: `view`

Lists every recipe added so far, showing each one's name, type and serving size. Takes no arguments.

Format: `view`

Example output:

```
____________________________________________________________
1. Egg Fried Rice (dinner, serves 1)
2. Pancakes (breakfast, serves 1)
____________________________________________________________
```

If no recipes have been added yet, this prints `No recipes found.` instead.

### Modifying a recipe: `modify`

Changes the serving size of an existing recipe and automatically scales all ingredient quantities proportionally.

Format: `modify RECIPE_NAME s/NEW_SERVING_SIZE`

* `RECIPE_NAME` must be the full name of an existing recipe. Matching is case-insensitive;
  for example, `Egg Taco` and `egg taco` refer to the same recipe.
* Recipe names may contain spaces. The `s/` prefix marks the beginning of the new serving-size parameter.
* `NEW_SERVING_SIZE` must be a positive integer, such as `1`, `2`, or `3`.
  Zero, negative numbers, decimal numbers, and non-numeric values are not accepted.
* Each ingredient quantity is multiplied by **new serving size ÷ current serving size**.
* Ingredient names and measurement units remain unchanged.
* Scaled quantities with a finite decimal expansion are displayed in full, without unnecessary trailing zeroes.
  For example, `0.375` remains `0.375`, while `1.500` is displayed as `1.5` and `2.000` as `2`.
* Scaled quantities with an infinite recurring decimal expansion are rounded to at most two decimal places for display.
  For example, `1 ÷ 3` is displayed as `0.33`.
* Rounding affects only the displayed values. The application stores exact ingredient quantities internally,
  so rounding for display does not affect subsequent calculations.
* If the new serving size is the same as the current serving size, no changes are made.

Examples:

Each example below starts with an `Egg Taco` recipe that serves **2** and contains **2 eggs**, **4 tortillas**,
and **100 g cheese**.

* `modify Egg Taco s/4`

  Changes the serving size from 2 to 4 and doubles all ingredient quantities.

  Expected output:

  ```text
  Serving size of Egg Taco has been updated from 2 to 4.
  Updated ingredients:
  1. 4 eggs
  2. 8 tortillas
  3. 200 g cheese
  ```

* `modify Egg Taco s/2`

  Keeps the recipe unchanged because it already serves 2.

  Expected output:

  ```text
  Egg Taco already has a serving size of 2. No changes were made.
  ```

Possible errors:

* Recipe not found: `modify Unknown Recipe s/4`

  Expected output:

  ```text
  Error: Recipe "Unknown Recipe" could not be found.
  ```

* Invalid serving size: `modify Egg Taco s/zero`

  Expected output:

  ```text
  Error: Serving size must be a positive integer.
  ```

## Saving the data

Your recipes are saved automatically to `data/whatscooking.txt` after every command. There is no need to save manually.

The next time you start the app, your recipes are loaded back from that file automatically.

## FAQ

**Q**: How do I transfer my data to another computer?

**A**: Copy the `data/whatscooking.txt` file from your old computer to the same location (the folder where the app's jar file is) on your new computer.

## Command Summary

{Give a 'cheat sheet' of commands here}

* Add a recipe `add TITLE[, TYPE] [s/SERVINGS] [i/INGREDIENT, INGREDIENT...] [st/STEP]... [n/NOTE]`
* Search recipes `search f/{name/ingredient}/{KEYWORD}`
* View all recipes `view`
* Retrieve a recipe and check possible allergens `retrieve INDEX` or `retrieve RECIPE_NAME`
* Modify serving size `modify RECIPE_NAME s/NEW_SERVING_SIZE`
* **Delete a recipe** `delete INDEX` or `delete RECIPE_NAME`
* **Manage home stock** `stock add INGREDIENT`, `stock remove INGREDIENT`, or `stock list`
