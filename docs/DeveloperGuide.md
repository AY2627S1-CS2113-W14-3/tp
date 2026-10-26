# Developer Guide

## Acknowledgements

* Project structure: [CS2113 project template](https://github.com/NUS-CS2113-AY2627-S1/tp).
* Allergen category grouping: [USDA, Food Allergies: The Big 9](https://www.fsis.usda.gov/food-safety/safe-food-handling-and-preparation/food-safety-basics/food-allergies-big-9).
  The application's English keyword dictionary is limited and maintained in this repository; it is not an official detection algorithm.
* UML rendering: [PlantUML](https://plantuml.com/). Editable sources are included alongside the diagrams.
* AI assistance: Lim Sin ([wunicorn21](https://github.com/wunicorn21)) used Claude (Anthropic) to help plan and
  draft the v2.0 `add` improvements, the `edit` command, `RecipeInputParser`, their tests, and the related
  User Guide and Developer Guide sections and diagrams. All generated work was reviewed, tested and adjusted by
  Lim Sin.

## Design & implementation

### Serving-size modification: `modify`

*Section author: CAO JINGYUAN ([littlecrawler](https://github.com/littlecrawler)).*

`modify RECIPE_NAME s/NEW_SERVING_SIZE` resizes an existing recipe. It multiplies each ingredient
quantity by the target serving size divided by the current serving size. For example, changing a
two-serving recipe containing 2 eggs and 100 g cheese to four servings produces 4 eggs and 200 g
cheese. Ingredient names, units, recipe title, type, preparation steps and note remain unchanged.
The [User Guide](UserGuide.md#modifying-a-recipe-modify) describes the command syntax and output.

#### Responsibilities and quantity representation

The command handles user input and reporting; the recipe model owns the calculation and update.
This keeps the resizing rules available to callers other than the CLI.

| Component | Responsibility |
| --- | --- |
| `Parser` | Recognizes `modify` and passes the remaining arguments to `ModifyCommand`. |
| `ModifyCommand` | Validates the name/size arguments, locates the recipe, handles an unchanged size and reports success or errors. |
| `RecipeList` | Finds the first recipe whose full title matches, ignoring case and surrounding whitespace. |
| `Recipe` | Calculates and validates every new quantity before applying any changes. |
| `Ingredient` | Keeps the immutable name and unit separate from the replaceable quantity; formats quantities for output. |
| `IngredientQuantity` | Stores an exact fraction and returns new, reduced fractions when scaling. This class is package-private and immutable. |
| `Ui` | Displays the previous and new serving sizes and the updated ingredient list. |

![Class diagram of serving-size modification](diagrams/modify-classes.svg)

*Figure 1. Classes involved in resizing. Only relevant members and relationships are shown;
`~` indicates package-private access.*
[PlantUML source](diagrams/modify-classes.puml)

An ingredient's quantity is an `IngredientQuantity`, represented by positive `BigInteger` numerator
and denominator fields. Decimal input is converted through `BigDecimal` without first rounding the
stored value to a `double`. Scaling creates a new fraction:

```text
new numerator   = current numerator   * new serving size
new denominator = current denominator * current serving size
```

The constructor divides both terms by their greatest common divisor. This preserves exact values
across successive changes while reducing the size of the integers. For example, 1 kg of rice for
three servings becomes exactly 1/3 kg for one serving. Its display is `0.33 kg rice`, but changing
back to three servings in the same session restores exactly `1 kg rice`.

`IngredientQuantity.toDisplayString()` displays terminating decimals in full, removing trailing
zeroes. It rounds a repeating decimal to at most two places using `HALF_UP`. Displayed text is never
fed back into the resizing calculation. `doubleValue()` is used only for the existing numeric API
and range validation; it is not the source of subsequent scaling calculations.

#### Command flow and update order

![Sequence diagram of a successful serving-size modification](diagrams/modify-serving-size.svg)

*Figure 2. A valid command that changes the serving size. Parsing, unchanged-size handling,
exception paths and the application's subsequent save are described in the text and omitted here.*
[PlantUML source](diagrams/modify-serving-size.puml)

1. `ModifyCommand.execute` splits the arguments at a whitespace-delimited `s/`. It requires one
   name and one size, and parses the size as an integer from 1 to `Integer.MAX_VALUE`.
2. `RecipeList.getByName` resolves the full recipe name. A missing recipe produces a user-facing
   error. If the requested size already equals the current size, the command reports no change.
3. `Recipe.scaleToServingSize` allocates a temporary array. For every ingredient, it calculates an
   exact new quantity and checks that its numeric approximation remains positive and finite.
4. Only after all quantities pass validation does a second loop replace the ingredient quantities.
   The method then updates the recipe's serving size. It retains the existing ingredient objects.
5. The command logs the successful resize at `FINE` level and calls `Ui.showModifiedRecipe`.

The two loops prevent a later invalid quantity from leaving earlier ingredients partly resized.
For example, if the last ingredient overflows, the exception occurs before any quantity or serving
size is changed. An empty ingredient list is valid: only its serving size changes. The algorithm
makes two passes over I ingredients and uses I temporary quantity references, in addition to the
integer arithmetic needed for each exact fraction.

#### Validation and error handling

| Condition | Result |
| --- | --- |
| Missing name/size or repeated `s/` | A usage error before any recipe is changed. |
| Zero, negative, decimal, non-numeric or overflowing serving size | `Serving size must be a positive integer.` |
| No matching full recipe name | A recipe-not-found error; partial names do not match. |
| Target equals current size | A no-change message; quantities remain untouched. |
| A scaled quantity overflows to infinity or underflows to zero | An error with every original quantity and the original serving size preserved. |

Assertions check internal assumptions, including non-null command dependencies and positive
fraction terms. Runtime validation handles user input even when assertions are disabled.
`ModifyCommand` converts model `IllegalArgumentException`s to `WhatsCookingException`s, which the
application loop catches and displays before accepting the next command.

#### Integration, alternatives and current limits

`setServingSize` and `scaleToServingSize` serve different purposes. `add ... s/2` and loading a saved
recipe use the setter to record the serving size for the supplied quantities. `modify ... s/4`
uses the scaler to resize those quantities by 4 / 2. Substituting the setter for the scaler would
leave the ingredient amounts inconsistent with the requested number of servings.

`WhatsCooking` saves recipes after a command returns successfully, including `modify`. The current
`Storage` implementation writes `Ingredient.getQuantity()` as a decimal and reloads it as a `double`.
Consequently, exact fractions and decimal digits beyond double precision are not preserved across
a save/restart boundary. The exact round-trip guarantee above applies within a running session;
preserving it across restarts would require a coordinated change to the quantity storage format.

Using only `double` quantities would be simpler, but repeated resizing could accumulate rounding
errors. Rounding each result to two decimal places before storing it would also lose information
needed by later commands. Exact fractions keep calculation separate from display. Updating each
ingredient immediately would avoid the temporary array but could leave a partially modified recipe
when a later ingredient fails validation.

#### Automated verification

* `ModifyCommandTest` covers parser dispatch, full-name matching, unchanged sizes, invalid syntax,
  numeric boundaries, missing recipes and reporting a failed resize without a partial update.
* `RecipeScalingTest` covers successive resizing, preserved recipe details and ingredient objects,
  exact fractions, empty recipes, and overflow/underflow without changes to the original recipe.
* `IngredientDisplayTest` covers finite decimal precision, repeating decimals and display rounding.
* `ModifyWorkflowTest` checks the complete CLI session against `text-ui-test/EXPECTED.txt`.
* `ServingSizeCompatibilityTest` checks that `add` establishes the size used by later `modify` calls.

### Built-in allergen checking (v2.0)

*Section author: CAO JINGYUAN ([littlecrawler](https://github.com/littlecrawler)).*

This enhancement helps users notice possible allergens while managing recipes. A successful `add`
prints a warning when a recorded ingredient name matches the built-in dictionary. `retrieve` prints
the same warning with the recipe's details, whether the recipe is selected by its index or full name.
The recipe is still added or displayed normally: a match is advisory and does not reject the command.
This section covers the allergen checker and its integration with the existing commands; it does
not describe the full implementation of recipe creation or retrieval.

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

*Figure 3. Dependencies relevant to allergen checking. Unrelated members and classes are omitted.*
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

#### Sequence: allergen checking during `retrieve`

![Sequence diagram of retrieving a recipe and checking possible allergens](diagrams/allergen-retrieve.svg)

*Figure 4. The allergen check triggered by successful retrieval by full name. Parsing, private
formatting helpers and ingredient getters are omitted to keep attention on the allergen check.*
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

### Stock management: `stock`

*Section author: Jiang Siyu ([irisiyuj](https://github.com/irisiyuj))*

The `stock` command manages the ingredients the user currently has at home. It allows adding items (`stock add`), removing consumed items (`stock remove`), and viewing the current inventory (`stock list`). Unlike recipes, which store a fixed list of required ingredients, the stock is a mutable collection that grows and shrinks. The feature ignores plural forms to improve usability and ensures items cannot be removed if the user does not have enough. The [User Guide](UserGuide.md#managing-home-stock-stock) describes the command syntax and output.

#### Responsibilities and data model

The feature separates the inventory management from command parsing, allowing the underlying data structure to be tested independently of the console loop.

| Component | Responsibility |
| --- | --- |
| `StockList` | Holds an `ArrayList` of `Ingredient` objects. Provides the logic to add, merge, and remove items. It normalizes names to match singular and plural forms. |
| `StockCommand` | Parses the `action` (add, remove, list) and `data`. It handles command-level validation and passes structured `Ingredient` objects to `StockList`. |
| `Ingredient` | Reused from the recipe module. It supplies the quantity, unit and name, allowing the stock to perform numeric operations (addition and subtraction). |
| `Parser` | Recognizes the `stock` keyword and instantiates the `StockCommand` with the shared `StockList`. |

![Object diagram of the stock components](diagrams/stock-object.svg)

*Figure 5. The in-memory state of `StockList` after adding "3 eggs" and "200g chicken". Unrelated members are omitted.*

Unlike `Recipe`, the `StockList` does not maintain serving sizes. It relies on an `ArrayList` to preserve insertion order. The `Ingredient` objects stored in the `StockList` are mutable only in their quantity; their name and unit remain final.

#### Implementation details

![Sequence diagram of a successful stock remove command](diagrams/stock-remove.svg)
*Figure 6. A valid `stock remove` command that successfully subtracts quantities. Exception paths and the application's subsequent save are omitted.*

1. `StockCommand.execute` splits the arguments into an `action` and the remaining `data`. For `add` and `remove`, it attempts to construct a new `Ingredient` from the `data`.
2. If the format is invalid (e.g., missing quantity), `IllegalArgumentException` is caught and rethrown as a user-facing `WhatsCookingException`.
3. The `StockList` iterates through its current items. It checks if the ingredient is the same using two criteria:
    - The units must match (both null or string-equal).
    - The names must match after normalization (ignoring case and removing a trailing 's').
4. For `add`, if a match is found, the quantities are summed. If not, the new `Ingredient` is appended. For `remove`, if the requested quantity exceeds the current quantity, a `WhatsCookingException` is thrown. If it exactly equals, the item is removed from the list; otherwise, it is subtracted.
5. The command logs the successful operation at `FINE` level and calls `Ui.showStockList` or `Ui.showMessage`.

#### Matching rules and floating-point safety

The `StockList` uses a private `normalizeName` method that converts the name to lowercase and removes a trailing 's'. This allows `stock add 3 eggs` and `stock remove 1 egg` to interact with the same item without requiring complex NLP.

When removing items, the implementation calculates `newQuantity = item.getQuantity() - toRemove.getQuantity()`. Because Java uses floating-point arithmetic for `double`, an exact subtraction like `5.0 - 5.0` might theoretically result in `0.000000001`. To prevent leaving ghost items with near-zero quantities, the code checks `if (newQuantity <= 0.0001)` before calling `ingredients.remove(item)`. Otherwise, it updates the quantity via `setQuantity`.

#### Alternatives and limitations

Storing stock as plain strings (e.g., "3 eggs") was considered but rejected. Numeric operations (adding and subtracting) would require re-parsing every time, which is error-prone. Reusing the `Ingredient` class ensures strict type checking.

A limitation of this implementation is the lack of automatic unit conversion. If the user adds `1 kg chicken` and attempts to remove `500 g chicken`, the command will fail because the units differ. Implementing unit conversion (kg to g, liters to ml) was deemed out of scope for v1.0 to prevent complexity. Additionally, the plural-normalization only strips trailing 's' and does not handle irregular plurals (e.g., "mice" to "mouse").

#### Defensive programming and integration

Assertions guard internal assumptions: `StockList` asserts that the incoming `Ingredient` is not null. User input errors are handled separately via `WhatsCookingException` and displayed by the `Ui`. The `StockList` is instantiated once in `WhatsCooking` and passed via the `Parser` to avoid static state, keeping the class testable.

#### Automated verification

* `StockListTest` covers successful additions, merging identical items, case-insensitivity, plural matching, exact removals, partial removals, and exceptions for insufficient stock.
* `StockCommandTest` verifies the command parsing, missing arguments, invalid ingredient formats, and correct delegation to `StockList`.
* Manual testing instructions for the stock feature are included in the section [Instructions for manual testing](#instructions-for-manual-testing).

### Recipe creation and editing: `add` and `edit`

*Section author: Lim Sin ([wunicorn21](https://github.com/wunicorn21)).*

`add` creates a recipe from one line of input, and `edit` changes the title, type or note of an existing
recipe. In v2.0, `add` also rejects a title that another recipe already uses (ignoring case and surrounding
spaces) and accepts an optional serving size with `s/`. Both commands accept the same kinds of values, so the
rules for checking those values are kept in one shared class. The
[User Guide](UserGuide.md#adding-a-recipe-add) describes the command syntax and output.

#### Classes involved

`AddCommand` and `EditCommand` decide the order and meaning of their arguments, while the shared checks for
individual values (title, type, serving size, ingredients, steps and note) live in the utility class
`RecipeInputParser`. This keeps both commands' error messages identical and avoids duplicated parsing code.
`RecipeList.containsTitle` keeps titles unique, and `Recipe.setTitle` and `Recipe.setType` were added for `edit`.

![Class diagram of recipe creation and editing](diagrams/recipe-input-classes.svg)

*Figure 7. Classes involved in `add` and `edit`. Only relevant members and relationships are shown;
`~` indicates package-private access and underlined methods are static.*
[PlantUML source](diagrams/recipe-input-classes.puml)

#### Command flow: `add`

The format is `add TITLE[, TYPE] [s/SERVINGS] [i/INGREDIENTS] [st/STEP]... [n/NOTE]`.

![Sequence diagram of a successful add command](diagrams/add-recipe.svg)

*Figure 8. A valid `add` command with a new title. Parsing by `Parser`, exception paths and the
application's subsequent save are described in the text and omitted here.*
[PlantUML source](diagrams/add-recipe.puml)

1. `AddCommand.execute` adds a leading space to the arguments, then splits them from the back: first at
   ` n/`, then at the first ` st/`, then at ` i/`, then at ` s/`, and finally at the first comma to separate
   the title from the type. Each prefix includes its leading space, so text such as `wasabi/` inside an
   ingredient is not mistaken for a prefix.
2. `RecipeInputParser.parseTitle` trims the title and rejects a blank one.
3. `RecipeList.containsTitle` compares the title with every stored title, ignoring case and surrounding
   spaces. A match throws a `WhatsCookingException` before any `Recipe` is created.
4. The command creates the `Recipe`, records the serving size with `setServingSize` (without scaling any
   quantity), and adds the ingredients and steps through `RecipeInputParser`.
5. Only after every part is valid does it call `RecipeList.add`. It then shows the confirmation and any
   possible-allergen warning through `Ui`. `WhatsCooking` saves the list after the command returns.

#### Command flow: `edit`

The format is `edit INDEX [title/NEW_TITLE] [type/NEW_TYPE] [n/NEW_NOTE]`. Unlike `add`, the parts may be
given in any order. `EditCommand` records where each prefix starts, sorts those positions, and takes each
value as the text up to the next prefix or the end of the line.

![Activity diagram of the checks made by the edit command](diagrams/edit-recipe-activity.svg)

*Figure 9. The checks `edit` makes, in order. Every check finishes before any value is changed.*
[PlantUML source](diagrams/edit-recipe-activity.puml)

1. The recipe number is parsed as a positive whole number and resolved with `RecipeList.get`.
2. The values for `title/`, `type/` and `n/` are found. A missing prefix leaves that field unchanged.
3. Each new value is checked with `RecipeInputParser`. A new title must not belong to another recipe;
   changing only the letter case of the recipe's own title is allowed.
4. Only then are the new values applied with `setTitle`, `setType` and `setNote`, and the updated recipe
   is displayed.

Checking everything before changing anything means a rejected `edit` leaves the recipe exactly as it was.
The prefix `type/` is used instead of `t/` because `t/` is reserved for a planned cooking-time field.

#### Alternatives and limitations

* **One-line input (current) or a guided, multi-step dialogue.** Early drafts of `add` asked for ingredients,
  then steps, then a note, ending each stage with `done!`. A single line was chosen because it is faster for
  users who type well, matches the other commands, and needs no stored state between inputs. Its cost is that
  long recipes produce long lines and the parts of `add` must be typed in order.
* **Shared checks (current) or checks inside each command.** Keeping separate copies in `add` and `edit`
  would let the two commands drift apart. The planned editing of ingredients and steps can reuse
  `addIngredients` and `addSteps` directly.
* **Selecting a recipe by number (current) or by title in `edit`.** A number is short and never ambiguous,
  whereas a title can contain text that looks like a prefix.
* **Limitations.** Extra spaces inside a title are significant, so `egg  taco` (two spaces) is not treated
  as a duplicate of `egg taco`. `edit` cannot yet change ingredients, steps or remove a type; these are
  planned for v2.0b.

## Product scope
### Target user profile

Home cooks who prefer a command-line interface for storing, finding and adjusting their recipes, as well as managing the ingredients they currently have at home.

### Value proposition

Keep recipe details in a local collection, find them quickly, resize quantities for a different
number of servings, and surface possible allergens named in the recorded ingredients, , and keep track of available ingredients to know what can be cooked right now.

## User Stories

|Version| As a ... | I want to ... | So that I can ...|
|----|---------|---------------|------------------|
|v1.0|home cook|resize a recipe's ingredient quantities for a new serving size|cook for a different number of people without calculating each amount manually|
|v1.0|home cook| retrieve a specific recipe by its index or name | view its full details quickly without scrolling through the whole list |
|v1.0|home cook| delete a recipe by its index or name | remove outdated or disliked recipes to keep my collection clean |
|v2.0|home cook|see possible allergens when adding or retrieving a recipe|know which recorded ingredients need further checking|
|v2.0|home cook| add, remove, and list ingredients I currently have at home | keep track of my inventory and know what I can cook right now |
|v2.0|frequent user|be stopped from adding a recipe whose title I already used|avoid keeping two different versions of the same dish|
|v2.0|host|record how many people a recipe serves when I add it|scale it correctly with `modify` later|
|v2.0|frequent cook|edit a recipe's title, type or note|keep my recipes up to date without deleting and re-adding them|

## Non-Functional Requirements

* An invalid resize must leave the original recipe unchanged; successful resizing must preserve its other details.
* Within a running session, quantity display rounding must not affect later resizing calculations.
* Allergen checking runs offline, without third-party runtime dependencies or a remote database.
* Detection must leave the recipe and its quantities unchanged and use a stable display order.
* The feature must work with the existing plain-text save format and Java 25 application.
* A rejected `add` or `edit` must leave the recipe list exactly as it was before the command.
* Recipe titles must stay unique (ignoring case), including for recipes loaded from a previous session.

## Glossary

* *Serving size* - The number of people for whom the recorded ingredient quantities are intended.
* *Exact quantity* - A quantity represented internally as a fraction rather than rounded display text.
* *Possible allergen* - A category whose configured keyword matches an ingredient name; this does not
  establish the actual composition of the ingredient.
* *Derived result* - A value recalculated from current recipe data instead of stored independently.
* *Stock* - The collection of ingredients the user currently has at home.
* *Prefix* - The short marker before a part of a command, such as `s/`, `i/`, `st/`, `n/`, `title/` or `type/`.
* *Duplicate title* - A title that matches an existing recipe's title when letter case and surrounding spaces
  are ignored.

## Instructions for manual testing

Run a built JAR in a separate test folder so these examples do not affect your personal recipe file.
Build it with `./gradlew shadowJar` (Windows: `.\gradlew.bat shadowJar`), copy
`build/libs/whatscooking.jar` into that folder, and launch `java -ea -jar whatscooking.jar`.

### Serving-size modification

Run these actions in order, starting without a recipe named `Resize Rice`. Keep the application
running between the fraction round-trip steps so this checks the in-memory calculation.

| Action | Expected observation |
| --- | --- |
| `add Resize Rice, main s/3 i/1 kg rice st/Cook n/Keep covered` | Recipe added for three servings. |
| `modify resize rice s/1` | Full-name lookup ignores case; the ingredient displays as `0.33 kg rice`. |
| `modify Resize Rice s/3`, then `retrieve Resize Rice` | Quantity returns to exactly `1 kg rice`; title, type, unit, step and note remain unchanged. |
| `modify Resize Rice s/3` | No-change message. |
| `modify Resize Rice s/0`, `modify Resize Rice s/1.5` and `modify Resize Rice s/2147483648` | Each is rejected with the positive-integer error. |
| `modify Resize Rice s/2 s/4` | Usage error for repeated parameters. |
| `modify Resize s/2` | Recipe-not-found error; a partial name is not accepted. |
| `retrieve Resize Rice` | Still three servings and `1 kg rice` after the rejected commands. |

### Allergen warnings

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

### Retrieve and Delete

Run these actions in order.

| Action | Expected observation |
| --- | --- |
| `add Retrieve Test, main i/1 egg st/Cook` | Recipe added. |
| `retrieve 1` | Displays the full recipe for `Retrieve Test`. |
| `retrieve retrieve test` | Displays the same recipe (case-insensitive name matching). |
| `retrieve 99` | Error: Recipe 99 does not exist. |
| `delete 1` | Successfully deletes the recipe. |
| `retrieve Retrieve Test` | Error: Recipe not found. |
| `delete apple` | Error: Recipe not found. |

### Stock management

Run these actions in order, starting with an empty stock. Keep the application running between steps to verify in-memory updates.

| Action | Expected observation |
| --- | --- |
| `stock add 3 eggs` | Stock updated; message says "Added to stock: 3 eggs". |
| `stock add 200g chicken` | Stock now has two different items. |
| `stock list` | Lists "3 eggs" and "200g chicken". |
| `stock remove 1 egg` | Case and plural matching work; message says "Removed from stock: 1 egg". |
| `stock list` | Lists "2.0 eggs" (or similar) and "200g chicken". |
| `stock remove 5 eggs` | Error: Not enough eggs in stock. You only have 2.0. |
| `stock remove 2 eggs` | Message says removed; `stock list` no longer shows eggs. |
| `stock add abc` | Error: Invalid ingredient format. |
| `stock unknown` | Error: Unknown stock action. |

### Adding and editing recipes

Run these actions in order in an empty test folder, so no saved recipe has the same title.

| Action | Expected observation |
| --- | --- |
| `add Egg Taco, snack s/2 i/2 eggs st/Cook n/Serve hot` | Recipe added; possible-allergen warning lists `eggs`. |
| `add egg taco` | Error: a recipe called "egg taco" already exists. `view` still lists one recipe. |
| `add Toast s/0`, `add Toast s/1.5` and `add Toast s/two` | Each is rejected with the serving-size error; no recipe is added. |
| `add Toast i/2 bread s/2` | Error: `s/` must come right after the title and type. |
| `add Pancakes, breakfast`, then `view` | Lists `Egg Taco (snack, serves 2)` and `Pancakes (breakfast, serves 1)`. |
| `edit 1 title/Egg Wrap n/Add chilli` | Shows `Updated recipe 1:` with the new title and note; type and serving size are unchanged. |
| `edit 1 title/PANCAKES` | Error: a recipe called "PANCAKES" already exists. Recipe 1 is unchanged. |
| `edit 1 title/egg wrap` | Allowed: only the letter case of the recipe's own title changes. |
| `edit 9 type/main`, `edit 0 type/main` and `edit abc type/main` | Each is rejected; no recipe changes. |
| `edit 1`, `edit 1 egg wrap` and `edit 1 type/` | Each is rejected with a format or empty-value error. |
| `bye`, restart in the same folder, then `retrieve 1` | The edited title, type and note reappear after loading. |