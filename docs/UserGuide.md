# User Guide

## Introduction

{Give a product intro}

## Quick Start

1. Ensure that you have Java 25 or above installed. You can check by running `java -version` in a terminal.
1. Download the latest `whatscooking.jar` from the [releases page](https://github.com/AY2627S1-CS2113-W14-3/tp/releases).
1. Copy the file into an empty folder. Your recipes will be saved in this folder (see [Saving the data](#saving-the-data)).
1. Open a terminal in that folder and run `java -jar whatscooking.jar`. A welcome message appears:

   ```text
   ____________________________________________________________
   Welcome to WhatsCooking!
   What recipe shall we manage today?
   ____________________________________________________________
   ```

1. Type a command and press Enter. For example, `add egg taco, snack i/3 eggs` adds a recipe, and `view`
   lists your recipes. See [Features](#features) for every command.
1. Type `bye` to exit.

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

Expected output for `retrieve egg taco`:

```text
____________________________________________________________
egg taco (snack, serves 1)
Ingredients:
  1. 3 eggs
  2. 5 tortillas
Steps:
  1. Scramble egg
  2. Put egg in tortilla
____________________________________________________________
Possible allergens: eggs. Check ingredient labels.
____________________________________________________________
```

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

Tracks the ingredients you currently have at home so you know what you can cook. You can add items you bought, remove items you used, and view your current inventory. The stock feature automatically ignores plural forms (e.g., `egg` and `eggs` are treated as the same item) and allows you to track expiry dates.

Format: `stock add INGREDIENT [by/YYYY-MM-DD]`, `stock remove INGREDIENT`, or `stock list`

* `stock add`: Adds the specified ingredient to your stock. If the ingredient (with the exact same expiry date) already exists, the quantities are summed.
* `stock remove`: Subtracts the specified quantity from your stock. The system **prioritizes removing items that are expiring soonest**. If the quantity reaches zero, the item is removed.
* `stock list`: Displays all ingredients currently in your stock, highlighting expired or soon-to-expire items.
* **Ingredient Format**: Must follow the same format as recipe ingredients: `QUANTITY [UNIT] NAME` (e.g., `3 eggs`, `200g chicken`).
* **Expiry Date (`by/`)**: An optional parameter for `add`. Must be in `YYYY-MM-DD` format.

Examples:
* `stock add 3 eggs` adds 3 eggs to your stock without an expiry date.
* `stock add 200g chicken by/2026-10-31` adds 200 grams of chicken that expires on Oct 31, 2026.
* `stock list` shows your current inventory.

Expected output for `stock list` (showing expiry tags):

```text
____________________________________________________________
Current Home Stock:
- 3 eggs
- 200 g chicken (Expires: 2026-10-31)
- 2 milk (Expires: 2026-10-20) [EXPIRING SOON]
- 1 cheese (Expires: 2026-10-01) [EXPIRED]
____________________________________________________________
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

Warnings are recalculated from the current ingredient names, including after saving and restarting
the application. Use `substitute` below to view cooking alternatives for supported ingredients.

### Viewing ingredient alternatives: `substitute`

Shows possible alternatives for supported ingredients in an existing recipe.

Format: `substitute RECIPE_NAME`

* Use the **full recipe title**, ignoring case and surrounding whitespace. This command does not
  accept a recipe index; a numeric value is interpreted as a literal recipe title.
* Suggestions refer to the ingredient numbers shown by `retrieve`, in the same order. Unsupported
  ingredients are omitted; repeated ingredients retain their own numbers.
* Matching uses the **complete ingredient name**, ignoring case, repeated whitespace and hyphens.
  For example, `WHOLE WHEAT-FLOUR` matches `whole wheat flour`. It does not guess from individual
  words: `coconut milk`, `egg-free mix`, `salted butter` and `peanut butter sauce` have no entry.
* The command does not swap ingredients or calculate replacement amounts. Your original recipe,
  serving size and stock stay unchanged. Check a suitable recipe before using an alternative.

The complete built-in catalogue is:

| Recognised ingredient names | Alternative to consider | Context |
| --- | --- | --- |
| `milk`, `cow milk`, `cow's milk` | Rice drink | Cooking or baking; flavour and nutrition differ. |
| `butter` | Milk-free margarine | Spreading or baking; check the product's instructions. |
| `egg`, `eggs` | Mashed banana | Some baked goods only; adds flavour and cannot replace eggs in omelettes or meringues. |
| `peanut butter` | Sunflower seed butter | Spreads or suitable baking recipes; check seed allergies. |
| `almond butter`, `cashew butter` | Sunflower seed butter | Spreads or suitable baking recipes; check seed allergies. |
| `wheat flour`, `whole wheat flour` | Wheat-free flour blend | Follow a tested recipe and the blend's instructions. |

These cooking ideas draw on [FARE's substitution guide](https://www.foodallergy.org/resources/substitutions-common-allergens-pdf)
and the [Kids with Food Allergies substitution guides](https://kidswithfoodallergies.org/recipes-diet/recipe-substitutions/).
The application's catalogue is deliberately narrower than its nine-category warning dictionary.
There are no built-in suggestions for fish, shellfish, soy or sesame, or for other unlisted names.

**An alternative can contain other allergens.** Check all relevant allergies, product labels and
cross-contact. A suggestion, or the absence of one, does not establish that any food is safe.
The command cannot evaluate a recipe's cooking method, an allergy profile or nutritional needs.

Example:

```text
add Cake, dessert s/2 i/1 cup milk, 2 eggs st/Bake
substitute Cake
```

Expected output:

```text
____________________________________________________________
Substitution ideas for Cake:
  1. milk -> rice drink
     For cooking or baking; flavour and nutrition differ from cow's milk.
  2. eggs -> mashed banana
     For some baked goods only; adds banana flavour. Not for omelettes or meringues.
Check all allergies, product labels and cross-contact; alternatives may contain allergens.
Use a suitable recipe for amounts. Your recipe and quantities have not been changed.
____________________________________________________________
```

For a recipe without supported names, the output says
`No built-in suggestions for the recorded ingredient names.` and still shows the caution.
`substitute` without a title reports usage; `substitute Missing` reports `Recipe not found: Missing`.

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

Lists every recipe in the order it was added, showing each one's number, title, type and serving size.

Format: `view`

* Any text typed after `view` is ignored.
* The number before each recipe is the `INDEX` used by `retrieve`, `edit` and `delete`.

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
  so rounding for display does not affect subsequent calculations **within the current session**.
  Saving and restarting stores/reloads decimal approximations, so exact fraction round trips are not
  guaranteed across restarts.
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

### Exiting the program: `bye`

Saves your recipes and closes the app.

Format: `bye` or `exit`

Expected output:

```text
____________________________________________________________
Bye! Happy cooking.
____________________________________________________________
```

## Saving the data

Your recipes are saved automatically after every command that succeeds. There is no need to save manually.
They are stored in `data/whatscooking.txt`, inside the folder you run the app from. The `data` folder is
created the first time the app saves.

The next time you start the app from the same folder, your recipes are loaded back automatically.

* Only recipes are saved. Your home stock (`stock`) is **not** saved and is empty each time you start the app.
* If `data/whatscooking.txt` does not exist yet, the app starts with no recipes. This is normal on first use.
* If the file's contents are damaged, the app shows the error below and starts with no recipes:

  ```text
  ____________________________________________________________
  Error: Could not load saved recipes, starting with an empty list. Data file is corrupted: ...
  ____________________________________________________________
  ```

> **Caution:** The data file is plain text, so you can open and read it, but editing it is not recommended.
> If an edit makes the file unreadable, the app starts with no recipes, and the next successful command
> replaces the file, removing the recipes it held. Make a backup copy of `data/whatscooking.txt` before
> editing it.

## FAQ

**Q**: How do I transfer my data to another computer?

**A**: Copy the `data` folder from the folder you run the app from on your old computer into the folder you
run the app from on your new computer.

**Q**: What is the `whatscooking.log` file that appears next to the app?

**A**: It records diagnostic information to help developers investigate problems. You do not need to open it,
and deleting it does not affect your recipes.

## Command Summary

{Give a 'cheat sheet' of commands here}

* Add a recipe `add TITLE[, TYPE] [s/SERVINGS] [i/INGREDIENT, INGREDIENT...] [st/STEP]... [n/NOTE]`
* Edit a recipe's title, type or note `edit INDEX [title/NEW_TITLE] [type/NEW_TYPE] [n/NEW_NOTE]`
* Search recipes `search f/{name/ingredient}/{KEYWORD}`
* View all recipes `view`
* Retrieve a recipe and check possible allergens `retrieve INDEX` or `retrieve RECIPE_NAME`
* Modify serving size `modify RECIPE_NAME s/NEW_SERVING_SIZE`
* View ingredient alternatives `substitute RECIPE_NAME`
* Delete a recipe `delete INDEX` or `delete RECIPE_NAME`
* Manage home stock `stock add INGREDIENT [by/YYYY-MM-DD]`, `stock remove INGREDIENT`, or `stock list`
* Exit the program `bye` or `exit`
