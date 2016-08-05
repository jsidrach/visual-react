# Visual React
[Screenshots](docs/screenshots/README.md) |
[Levels](#levels) |
[License](#license) |
[Contributors](#contributors) |
[Guidelines](#guidelines) |
[Contributing](#contributing) |
[Assets](#assets) |
[Roadmap](#roadmap) |
[References](#references)

[TODO: Description]


## <a name="levels"></a> Levels

| Name       | Description                                          |
|:-----------|:-----------------------------------------------------|
| Collision  | Tap when the shapes are colliding                    |
| Color      | Tap after the color changes                          |
| Countdown  | Tap after the countdown passes zero                  |
| Fit        | Tap when one shape fits into the other one           |
| Labyrinth  | Tap after the two extremes are connected             |
| Light      | Tap after there are more light cells than dark cells |
| Pair       | Tap when there are two shapes equal                  |
| Singular   | Tap when there is a different shape among all        |
| TicTacToe  | Tap when three of the same type are in line          |
| Variety    | Tap when there are five or more different colors     |


## <a name="license"></a> License

[MIT](LICENSE) - Feel free to use and edit


## <a name="contributors"></a> Contributors

* :rooster: [Lu Yu](https://github.com/yulu1701): design, game engine, levels
* :monkey: [J. Sid](https://github.com/jsidrach): design, game engine, levels
* :monkey_face: [Chen Lai](https://github.com/claigit): testing
* :goat: [Zhongrong Jian](https://github.com/miaolegewang): testing


## <a name="guidelines"></a> Guidelines

### <a name="design-guidelines"></a> Design

* Follow the [Android Design Principles](https://developer.android.com/design/get-started/principles.html)
* Design levels that do not require previous knowledge
* Avoid using text whenever there is a cleaner alternative
* Use vector graphics, instead of rasterized ones
* Consistent look and feel


### <a name="development-guidelines"></a> Development

* Use [Android Studio](https://developer.android.com/studio/index.html)
* Follow the [Android Core App Quality Guidelines](https://developer.android.com/distribute/essentials/quality/core.html)
* Do not hardcode strings and constants, include them as resources
* Adhere to the existing color palette
* Use vector graphics (```.svg```) for images and icons
* Avoid using external libraries as much as possible, to ease the future implementation of the game in other platforms
* Auto format the code, and fix all warnings and errors before committing
* Use ```camelCase``` for code and ```snake_case``` for resources
* Keep the code simple


## <a name="contributing"></a> Contributing

### <a name="adding-a-level"></a> Adding a level

1. Create a new branch, named using the level name in lowercase - only commit to this branch while developing the level
2. If the level you are implementing not described in the [levels list](#levels), create an Issue first describing it (you don't want to code something that won't be incorporated into the game!), and ask for feedback
3. Once the level has been approved, create a class with the level name in ```src/main/java/sneakycoders/visualreact/level/levels/``` that extends the ```Level``` class - all your code will reside in the newly created class
4. If needed, create a new layout for your level, and save it in ```src/main/res/layout/level_<name>.xml```
5. Add your level name to the levels array in ```src/main/res/values/arrays.xml```
6. Add your level title and description to the ```src/main/res/values/strings.xml``` resource, following existing conventions
7. Implement and test your level - use random ranges for all possible conditions in the level (timeouts, sizes, etc.), and store the ranges in ```src/main/res/values/ingame.xml```
8. Run the Linter (in Android Studio: ```Analyze > Inspect Code```), and fix any issue detected
9. Be sure the code follows the development guidelines mentioned before in the development parent section
10. Edit the [README](README.md) to add your level to the [levels list](#levels) (if not present already)
11. Make screenshots of your level (in progress, fail, success) and store them in [docs/screenshots](docs/screenshots), following the already existing format - edit the screenshots [README.md](docs/screenshots/README.md) to include them
12. Save all assets used to ```docs/assets``` (in addition to the resources folder), and list them in [assets](#assets)
13. Add yourself to the [contributors list](#contributors) - you earned it!
14. Create a Pull Request to add the level to the main branch - the new code will be reviewed, improved, and eventually merged

### <a name="fixing-a-bug"></a> Fixing a bug

1. Create a new branch, named using ```bugfix/<bug>```, where ```<bug>``` is a short description of the bug - only commit to this branch
2. Implement the fix for the bug
3. Run the Linter (in Android Studio: ```Analyze > Inspect Code```), and fix any issue detected
4. Be sure the code follows the development guidelines mentioned before in the development parent section
5. Rebase interactively to squash all commits into a single one, and explain in the commit body what was the bug and how it has been fixed
6. Create a Pull Request to add the level to the main branch - the new code will be reviewed, improved, and eventually merged

### <a name="refactoring"></a> Refactoring / Improving code quality

1. Create a new branch, named using ```refactor/<description>```, where ```<description>``` is a short description of the improvements going to be made
2. Implement the improvements, making sure the functionality does not change
3. Run the Linter (in Android Studio: ```Analyze > Inspect Code```), and fix any issue detected
4. Be sure the code follows the development guidelines mentioned before in the development parent section
5. Rebase interactively to squash all commits into a single one, and explain in the commit body what were the improvements made
6. Create a Pull Request to add the level to the main branch - the new code will be reviewed, improved, and eventually merged


## <a name="assets"></a> Assets

List of assets used:

* [Play Icon](docs/assets/play_icon.svg): [Freepik](http://www.flaticon.com) ([CC 3.0 BY](http://creativecommons.org/licenses/by/3.0/))
* [Preferences Icon](docs/assets/preferences_icon.svg): [Egor Rumyantsev](http://www.flaticon.com/authors/egor-rumyantsev) ([CC 3.0 BY](http://creativecommons.org/licenses/by/3.0/))
* [Rematch Icon](docs/assets/rematch_icon.svg): [Vaadin](http://www.flaticon.com/authors/vaadin) ([CC 3.0 BY](http://creativecommons.org/licenses/by/3.0/))


## <a name="roadmap"></a> Roadmap

- [x] ~~Rethink design, winner/loser should be more clear~~
- [x] ~~Write Design Goals~~
- [x] ~~Write Development Goals~~
- [x] ~~Choose color palette~~
- [x] ~~Implement main screen~~
- [x] ~~Implement settings screen~~
  - [x] ~~Make sure at least one game is selected in the settings screen~~
- [x] ~~Improve translation of numbers~~
- [x] ~~Add support for bidirectional strings~~
- [x] ~~Dialog to confirm exit of match~~
- [x] ~~Make the level selector autogenerate dynamically~~
- [x] ~~Implement basic common class for games~~
- [ ] Implement games
  - [x] ~~[J] Color~~
  - [x] ~~[J] Countdown~~
  - [x] ~~[J] Light~~
  - [x] ~~[J] Collision~~
  - [ ] [J] Fit
  - [ ] [J] Labyrinth
  - [ ] [L] TicTacToe
  - [ ] [L] Variety
  - [ ] [L] Pair
  - [ ] [L] Singular
- [ ] Revisit all code to document it better
- [ ] Integrate Firebase
- [ ] Include ads in a non-intrusive way
  - [ ] Menus
  - [ ] Ingame (can be disabled)
- [ ] Distribute app to the rest of sneaky coders, test & QA
- [ ] Run Lint, fix issues detected
- [ ] Design app icon
- [ ] Write app description
- [ ] Review translation guidelines, RTL (left -> start, right -> end), format numbers, etc.
- [ ] Translate to Chinese and Spanish
- [ ] Translate to additional languages, use paid service if needed
- [ ] Think and document more possible levels for future versions
- [ ] Make screenshots
- [ ] Create SneakyCoders account on Play Store
- [ ] Make sure all the items in [this checklist](https://developer.android.com/distribute/tools/launch-checklist.html) are completed
- [ ] Publish the app
- [ ] ????
- [ ] Profit


## <a name="references"></a> References
Remove as implemented / no longer needed

- [Disable ads on runtime](http://stackoverflow.com/questions/4549401/correctly-disable-admob-ads)
