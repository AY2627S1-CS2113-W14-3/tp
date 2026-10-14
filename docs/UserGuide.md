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

## FAQ

**Q**: How do I transfer my data to another computer? 

**A**: {your answer here}

## Command Summary

{Give a 'cheat sheet' of commands here}

* Add todo `todo n/TODO_NAME d/DEADLINE`
* Search recipes `search f/{name/ingredient}/{KEYWORD}`
