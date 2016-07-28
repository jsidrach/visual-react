# Visual React

[TODO: Description]

## Table of Contents

* [Screenshots](#screenshots)
* [Levels](#levels)
* [License](#license)
* [Guidelines](#guidelines)
  * [Design](#design-guidelines)
  * [Development](#development-guidelines)
* [Contributing](#contributing)
  * [Adding a Level](#adding-a-level)
* [Assets](#assets)
* [Roadmap](#roadmap)
* [References](#references)

## <a name="screenshots"></a> Screenshots

[TODO: Screenshots]


## <a name="levels"></a> Levels

| Name       | Description                                          |
|:-----------|:-----------------------------------------------------|
| Color      | Tap after the color changes                          |
| Countdown  | Tap after the countdown passes zero                  |
| Light      | Tap after there are more light cells than dark cells |
| Collision  | Tap when the shapes are colliding                    |
| TicTacToe  | Tap when three of the same type are in line          |
| Pair       | Tap when there are two shapes equal                  |
| Variety    | Tap when there are five different shapes             |
| Fit        | Tap after one shape fits into the other one          |
| Singular   | Tap when there is a different shape among all        |
| Labyrinth  | Tap when the two extremes are connected              |


## <a name="license"></a> License

[MIT](LICENSE) - Feel free to use and edit.


## <a name="guidelines"></a> Guidelines

### <a name="design-guidelines"></a> Design

* Follow the [Android Design Principles](https://developer.android.com/design/get-started/principles.html)
* Design levels that do not require previous knowledge
* Avoid using text whenever there is a cleaner alternative
* Use vector graphics, instead of rasterized ones
* Consistent look and feel


### <a name="development-guidelines"></a> Development

* Follow the [Android Core App Quality Guidelines](https://developer.android.com/distribute/essentials/quality/core.html)
* TODO: colors
* TODO: Android Studio
* Do not hardcode strings and constants
* Avoid using external libraries as much as possible, to ease the future implementation of the game in other platforms
* Auto format the code, and fix all warnings and errors before committing
* Use ```camelCase``` for code and ```snake_case``` for resources
* Keep the code simple


## <a name="contributing"></a> Contributing

### <a name="adding-a-level"></a> Adding a Level

* Create a new branch, named using the level name in lowercase - only commit to this branch while developing the level
* TODO: steps
* Run the Linter (in Android Studio: ```Analyze > Inspect Code```), and fix any issue detected
* Be sure the code follows the development guidelines mentioned before in the development parent section
* Create a Pull Request to add the level to the main branch - the new code will be reviewed and eventually merged


## <a name="assets"></a> Assets

List of assets used:

* [Play Icon](http://www.flaticon.com): Freepik ([CC 3.0 BY](http://creativecommons.org/licenses/by/3.0/))
* [Settings Icon](http://www.flaticon.com/authors/egor-rumyantsev): Egor Rumyantsev ([CC 3.0 BY](http://creativecommons.org/licenses/by/3.0/))
* [Rematch Icon](http://www.flaticon.com/authors/vaadin): Vaadin ([CC 3.0 BY](http://creativecommons.org/licenses/by/3.0/))

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
  - [ ] [J] Light
  - [ ] [J] Collision
  - [ ] [L] TicTacToe
  - [ ] [L] Pair
  - [ ] [L] Variety
  - [ ] [J] Fit
  - [ ] [L] Singular
  - [ ] [J] Labyrinth
- [ ] Revisit all code to document it better
- [ ] Integrate Firebase
- [ ] Include ads in a non-intrusive way
  - [ ] Menus
  - [ ] Ingame (can be disabled)
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
