# Developer Guide

## Acknowledgements

* Project structure: [CS2113 project template](https://github.com/NUS-CS2113-AY2627-S1/tp).
* Allergen category grouping: [USDA, Food Allergies: The Big 9](https://www.fsis.usda.gov/food-safety/safe-food-handling-and-preparation/food-safety-basics/food-allergies-big-9).
  The application's English keyword dictionary is limited and maintained in this repository; it is not an official detection algorithm.
* UML rendering: [PlantUML](https://plantuml.com/). Editable sources are included alongside the diagrams.

## Design & implementation

### Built-in allergen checking (v2.0)

This enhancement helps users notice possible allergens while managing recipes. A successful `add`
prints a warning when a recorded ingredient name matches the built-in dictionary. `retrieve` prints
the same warning with the recipe's details, whether the recipe is selected by its index or full name.
The recipe is still added or displayed normally: a match is advisory and does not reject the command.

The feature checks nine categories: milk, eggs, fish, crustacean shellfish, tree nuts, peanuts, wheat,
soybeans and sesame. These categories define the scope of the feature, not an exhaustive claim about
food allergies. The [User Guide](UserGuide.md#built-in-allergen-warnings) contains the complete keyword
list, command examples and user-visible limitations.

#### Responsibilities and data model

The feature separates matching from console formatting so the same rules can be reused and tested
without running the application loop.

| Component | Responsibility |
| --- | --- |
| `Allergen` | Defines each category's display name and immutable list of English keywords; matches a name against those keywords. |
| `AllergenChecker` | Examines the supplied `Ingredient` objects and returns the distinct matching categories. It has no recipe-specific state. |
| `Ingredient` | Supplies the name separately from its quantity and unit, so quantities and measurement units are not scanned. |
| `Ui` | Formats the warning consistently for a newly added recipe and a retrieved recipe. |
| `AddCommand` | Calls `Ui.showAllergenWarnings(recipe)` only after input validation and successful insertion. |
| `RetrieveCommand` | Uses the existing `Ui.showRecipe` overloads; both eventually display one `Recipe` and its warning. |

![Class diagram of the allergen-checking components](diagrams/allergen-classes.svg)

*Figure 1. Dependencies relevant to allergen checking. Unrelated members and classes are omitted.*
[PlantUML source](diagrams/allergen-classes.puml)

No allergen flag is stored in `Recipe` or in the save file. `AllergenChecker.findAllergens` derives a
new result from the current ingredient names each time it is called. Consequently, recipes loaded by
`Storage` receive the same check as newly created recipes, and scaling quantities does not invalidate
the result. Existing files remain compatible because the feature adds no persistent fields. The
returned set is unmodifiable; callers cannot mutate a result or use it to change a recipe.

#### Matching rules

`Allergen.matches` converts an ingredient name to lowercase using `Locale.ROOT`, then replaces runs
of non-letter/non-digit characters with a space. Using a fixed locale makes matching independent of
the computer's language settings. Punctuation and repeated whitespace therefore separate words;
for example, `BRAZIL-NUTS` becomes `brazil nuts`.

The normalized name and each keyword are compared with spaces around their boundaries. This allows
`roasted peanuts` to match `peanuts` without allowing `egg` to match `eggplant`. Singular and plural
forms are listed explicitly; the implementation does not guess word stems or interpret ingredient
semantics. An ingredient may match several categories, and several ingredients may match the same
category. An `EnumSet` removes duplicates and preserves category declaration order, producing stable
output such as `Possible allergens: milk, eggs. Check ingredient labels.`

For I ingredients and a fixed dictionary containing K keywords in total, the checker performs at
most I x K substring checks after normalization. Both the dictionary and typical recipe lists are
small, so a direct loop is sufficient. A search index or external service would add complexity with
little benefit for this version.

#### Sequence: retrieving a recipe

![Sequence diagram of retrieving a recipe and checking possible allergens](diagrams/allergen-retrieve.svg)

*Figure 2. Successful retrieval by full name. Parsing, private formatting helpers and ingredient
getters are omitted to keep attention on the allergen check.*
[PlantUML source](diagrams/allergen-retrieve.puml)

1. The application dispatches `RetrieveCommand.execute(recipes, ui)`.
2. The command obtains the recipe using `RecipeList.getByName` and asks `Ui` to display it.
3. `Ui` prints the recipe details. Its private formatting helper passes `recipe.getIngredients()`
   to `AllergenChecker.findAllergens`.
4. For each ingredient and category, the checker compares the ingredient name with that category's
   keywords and collects matching categories.
5. The checker returns an unmodifiable set. `Ui` prints one warning line if the set is non-empty.
   If there are no matches, the ordinary recipe output is unchanged.

Retrieval by index first uses `Ui.showRecipe(recipes, index)`, which resolves the recipe and delegates
to `Ui.showRecipe(recipe)`. It therefore uses exactly the same warning path. During `add`, validation
and insertion finish before `Ui.showAllergenWarnings(recipe)` invokes the shared formatting helper.
A malformed ingredient or duplicate title throws before that point, so a rejected recipe produces
neither a success message nor an allergen warning.

#### Defensive programming and integration

User input continues through the existing validation and `WhatsCookingException` path. Assertions
guard internal assumptions: the checker requires a non-null ingredient list with non-null entries,
and the UI requires a recipe. These assertions document programming errors; they do not replace
validation of user input. Java assertions are enabled for Gradle `run` and tests. When launching the
packaged application directly, use `java -ea -jar whatscooking.jar` to enable them as well.

The checker logs the number of matched categories and examined ingredients at `FINE` level through
`AppLogger`. The application directs logs to a file rather than the CLI output. Logging does not
change a recipe or store detection results.

The agreed `s/` contract is also verified: `add ... s/2` records the initial serving size without
rescaling the supplied quantities; `modify ... s/4` then scales those quantities by 4 / 2. Both
commands accept positive whole numbers up to `Integer.MAX_VALUE` and reject invalid or overflowing
values. Allergen detection uses names only, so this interaction preserves the warnings.

#### Alternatives and limitations

Storing detected categories in each recipe would make reads cheaper, but would require updating the
flags after every ingredient edit and migrating the save format. Deriving results avoids those
consistency problems. Keeping separate matching code inside `add` and `retrieve` was also avoided
because the two commands could otherwise develop different dictionaries or matching rules.

A fixed keyword list is easy to inspect, test and run offline. Its tradeoff is limited understanding:
it misses unlisted names and hidden ingredients, and it does not understand negation or substitutes.
For example, `coconut milk` matches milk and `peanut butter` matches both milk and peanuts. These are
literal name matches, not findings about the food's composition. No output should describe an
unmatched recipe as safe or allergen-free. Only ingredient names are checked; notes and preparation
steps may mention food that the checker does not see.

The planned v2.0b substitution feature is outside this implementation. A future change must define
and test its own replacement rules; the absence of a keyword match cannot establish that a proposed
replacement is safe.

#### Automated verification and diagram maintenance

* `AllergenCheckerTest` covers every category, aliases, case and punctuation, word boundaries,
  duplicates, stable ordering, empty and unrecognised input, literal substitute/negation handling,
  locale independence and fresh immutable results after recipe changes.
* `AllergenWorkflowTest` launches the real CLI in temporary directories. It checks successful add,
  retrieval by index and name, rejected additions, ignored titles/notes/steps, recipes without
  ingredients, and warnings after saving, restarting and scaling.
* `ServingSizeCompatibilityTest` checks the `add`/`modify` contract and invalid numeric boundaries.
* The existing `ModifyWorkflowTest` retains exact console-output checks; its expected output includes
  the two new warning lines in the fixture session.

Run `./gradlew check` on macOS/Linux or `.\gradlew.bat check` on Windows with Java 25. This runs the
JUnit suite and Checkstyle. Diagrams are committed as SVG so GitHub and the published DG can display
them without a PlantUML plugin. After changing a `.puml` source, regenerate its SVG with
`java -jar /path/to/plantuml.jar -tsvg docs/diagrams/*.puml` and inspect the rendered result.


## Product scope
### Target user profile

Home cooks who prefer a command-line interface for storing, finding and adjusting their recipes.

### Value proposition

Keep recipe details in a local collection, find them quickly, resize quantities for a different
number of servings, and surface possible allergens named in the recorded ingredients.

## User Stories

|Version| As a ... | I want to ... | So that I can ...|
|--------|----------|---------------|------------------|
|v2.0|home cook|see possible allergens when adding or retrieving a recipe|know which recorded ingredients need further checking|

## Non-Functional Requirements

* Allergen checking runs offline, without third-party runtime dependencies or a remote database.
* Detection must leave the recipe and its quantities unchanged and use a stable display order.
* The feature must work with the existing plain-text save format and Java 25 application.

## Glossary

* *Possible allergen* - A category whose configured keyword matches an ingredient name; this does not
  establish the actual composition of the ingredient.
* *Derived result* - A value recalculated from current recipe data instead of stored independently.

## Instructions for manual testing

### Allergen warnings

Run a built JAR in a separate test folder so these examples do not affect your personal recipe file.
Build it with `./gradlew shadowJar` (Windows: `.\gradlew.bat shadowJar`), copy
`build/libs/whatscooking.jar` into that folder, and launch `java -ea -jar whatscooking.jar`.

| Action | Expected observation |
| --- | --- |
| `add Omelette, breakfast s/2 i/2 eggs, 100 g cheese st/Cook` | Recipe added; warning lists `milk, eggs` once each. |
| `retrieve 1` and `retrieve omelette` | Both show the recipe and the same warning. |
| `modify Omelette s/4`, then `retrieve 1` | Quantities are 4 eggs and 200 g cheese; warning still lists `milk, eggs`. |
| `bye`, restart in the same folder, then `retrieve 1` | The saved recipe and its warning reappear. |
| `add Invalid i/1 eggs, 0 g cheese` | Input error; no recipe or warning is added for the failed command. |
| `add Roast i/1 eggplant`, then `retrieve Roast` | No allergen warning; `egg` must not match inside `eggplant`. |
| `add Empty`, then `retrieve Empty` | Empty ingredient list displays normally with no warning. |
| `add Drink i/1 cup coconut milk` | Literal keyword matching warns about milk; it cannot interpret substitutes. |
