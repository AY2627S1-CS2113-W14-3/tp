# User Guide

## Introduction

{Give a product intro}

## Quick Start

{Give steps to get started quickly}

1. Ensure that you have Java 25 or above installed.
1. Down the latest version of `Duke` from [here](http://link.to/duke).

## Features 

{Give detailed description of each feature}

### Adding a todo: `todo`
Adds a new item to the list of todo items.

Format: `todo n/TODO_NAME d/DEADLINE`

* The `DEADLINE` can be in a natural language format.
* The `TODO_NAME` cannot contain punctuation.  

Example of usage: 

`todo n/Write the rest of the User Guide d/next week`

`todo n/Refactor the User Guide to remove passive voice d/13/04/2020`

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

## FAQ

**Q**: How do I transfer my data to another computer? 

**A**: {your answer here}

## Command Summary

{Give a 'cheat sheet' of commands here}

* Add todo `todo n/TODO_NAME d/DEADLINE`
* Modify serving size `modify RECIPE_NAME s/NEW_SERVING_SIZE`
