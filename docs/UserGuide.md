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

Format: `add TITLE[, TYPE] [i/INGREDIENT, INGREDIENT...] [st/STEP]... [n/NOTE]`

* `TITLE` is required. Everything else is optional.
* `TYPE` goes after the first comma, e.g. `dessert`, `main`, `snack`. If you type a comma, the type cannot be left empty.
* Parts must be in this order: title and type, then `i/`, then `st/`, then `n/`.
* **Ingredients (`i/`)**: give all ingredients after a single `i/`, separated by commas.
    * Each ingredient is `QUANTITY [UNIT] NAME`, e.g. `3 eggs`, `200 g cheese`, `200g cheese`, `1.5 tbsp olive oil`.
    * `QUANTITY` must be a positive number. Decimals are allowed.
    * Supported units: `g`, `kg`, `mg`, `ml`, `l`, `tsp`, `tbsp`, `cup`, `cups`, `oz`, `lb`, `lbs` (not case-sensitive).
* **Steps (`st/`)**: start each step with its own `st/`. Steps are saved in the order you type them and cannot be empty.
* **Note (`n/`)**: an optional free-text note. It must come last, only one is allowed, and it cannot be empty.
* If any part is invalid, the recipe is **not** added, and an error message explains what to fix.

Examples:

* `add egg taco` adds a recipe with only a title.
* `add egg taco, snack i/3 eggs, 5 tortillas st/Scramble egg st/Put egg in tortilla` adds a snack with 2 ingredients and 2 steps.
* `add omelette, breakfast i/3 eggs, 200g cheese, 1.5 tbsp olive oil st/Whisk eggs st/Cook in pan n/Best eaten hot` adds a recipe with a note.

Expected output (for the second example):

```
____________________________________________________________
egg taco, snack added!
____________________________________________________________
```

> **Caution:** Unlike some other commands, the parts of `add` must be typed in order. For example, `add toast n/Serve hot st/Toast bread` is rejected because the note is not last.

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

* Add todo `todo n/TODO_NAME d/DEADLINE`
* Search recipes `search f/{name/ingredient}/{KEYWORD}`
* View all recipes `view`
* Modify serving size `modify RECIPE_NAME s/NEW_SERVING_SIZE`
