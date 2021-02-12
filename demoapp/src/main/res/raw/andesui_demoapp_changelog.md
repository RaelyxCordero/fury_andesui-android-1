# v3.7.0
## 🚀 Feature
- Added new icons for "To Agree" and "Info" (with outer circle)
- Added "Question" and "Mail" icons. | Authors: [@amacagno](https://github.com/amacagno)

## 🛠 Fixes
- Demoapp Dropdown activity scrollview container added to avoid cutting content
- Demoapp thumbnail showcase with same resource id as component fixed
- Dropdown label deleted in standalone type | Author: [@snti](https://github.com/snti)

# v3.6.1
## 🛠 Fixes
- Bump fix from version 3.5.1

# v3.6.0
## 🚀 Feature
- AndesButton With Drawable support | Authors [a0zamora](https://github.com/a0zamora)

## 🛠 Fixes
- Datepicker accent color

# v3.5.1
## 🛠 Fixes
- AndesMessage contructor overload for java classes compatibility | Authors [eportasML](https://github.com/eportasML)

# v3.5.0
## 🚀 Feature
- AndesMessage With Thumbnnail | Authors [a0zamora](https://github.com/a0zamora)

# v3.4.0
## 🚀 Features
- Dropdown | Author: [@snti](https://github.com/snti)

# v3.3.1
## 🚀 Features
- Added new icons for "Online Payments" and "QR"

## 🛠 Fixes
- Fix bodyPadding on CardComponent when padding is none from XML | Author: [@ariel-ramirez](https://github.com/ariel-ramirez)

# v3.3.0
## 🛠 Fixes
### AndesDatePicker | Author: [@Adrian De Seta](https://github.com/adriandeseta1985)
- Fix the size of the calendar api level < 21
- Fix color

## 🚀 Features
- Add bodyPadding property to AndesCard | Author: [@ariel-ramirez](https://github.com/ariel-ramirez)

# v3.2.0
## 🛠 Fixes
- Fix the size of the right and left componente of text
## 🚀 Features
- Migración Constraint layout v2

# v3.1.0
## 🚀 Features
- AndesList | Author: [@snti](https://github.com/snti)

# v3.0.0
## 🚀 Features
- Migration AndroidX

## 🛠 Fixes
- Fix constraint Radio Button

# v2.18.0
## 🚀 Features
- AndesDatePicker | Author: [@Adrian De Seta](https://github.com/adriandeseta1985)

## 🛠 Fixes
- AndesTagChoice left content default color change. | Authors: [@ArnaldoIbanez](https://github.com/ArnaldoIbanez)

# v2.17.0
## 🛠 Fixes
- Added Focus listener correctly on EditText of AndesTextField.

# v2.16.0
## 🚀 Features
- Added AndesCarousel | Author: [@Constanza Morillo](https://github.com/constanzamorillomeli)

# v2.15.0
## 🚀 Features
- AndesTextFieldCode | Author: [@Jorge Gonzalez](https://github.com/jorGonzalez291292)

## 🛠 Fixes
- Andes Coachmark: force only portrait mode and fix scroll up
- Improved AndesBottomSheet background dim animation

# v2.14.0
## 🚀 Features
- Added TagChoice | Author: [@tianmrn](https://github.com/tianmrn)

# v2.13.0
## 🚀 Features
- Andes BottomSheet | Author: [@fcano](https://github.com/fdcano)

## Added
- Added Andes icons (bill, cash, credit card, shipping, wallet)

## 🛠 Fixes
- Refactor AndesTag

## Developer Experience
- Added LeakCanary v2.5 to debug builds to detect memory leaks in an early stage.

# v2.12.0
## 🚀 Features
- Andes coachmark | Author: [@Marcos Picco](https://github.com/marcospicco)
- Auto enable Show Taps option when Android version is API 22 or below.

## Developer Experience
- Fixed crash when taping on "I want to contribute" in the Andes App.
- Minor UX improvements in the Andes App.

# v2.11.0
## 🚀 Features
- Changed target API level to 29.

## Developer Experience
- Linters upgraded to latest versions, and Findbugs replaced with Spotbugs.
- Added custom Android Lint config in favor of performance, accessibility and usability overrides.
- Let devs install both debug and mds showcase apps at the same time by adding `.debug` suffix to debug builds.
- Fix Detekt setup for components module as well as showcase app.
- Apply many suggestions from Detekt to both components and showcase modules. Ignore others in favor of enable linters from now on.
- Libraries upgraded from unit tests and showcase module.
- Gradle files cleanup.

## 🛠 Fixes
- AndesThumbnail: Use internal ImageView content drawable to keep aspect ratio. | Author: [@mzangl-meli](https://github.com/mzangl-meli)

# v2.10.1
## 🛠 Fixes
- AndesProgressIndicatorIndeterminate: Leak in progress animation | Author: [@orelvismeli](https://github.com/orelvismeli)

# v2.11.0
## 🚀 Features
- AndesTextField: Expose textFilter and textDigits @nesmarsollie

# v2.10.0
## 🚀 Features
- AndesTextField: Mask set text with mask | Author: [@fdiassilva](https://github.com/fdiassilva)

# v2.9.0
## 🚀 Features
- AndesButton: Progress indicator | Author: [@orelvismeli](https://github.com/orelvismeli)

# v2.8.0
## 🚀 Features
- AndesCard: Hierarchy primary/secondary | Author: [@ArqMobile](https://github.com/mercadolibre/fury_andesui-android)
- AndesCard: Actions support (link) | Author: [@ArqMobile](https://github.com/mercadolibre/fury_andesui-android)

# v2.7.1
## 🚀 Features
- AndesTextField: Mask support (i.e. passwords) | Author: @fdiassilva
- AndesTextField: Listener for onClick event in right icon | Author: @fdiassilva
- AndesTextField: Color change to right icon | Author: @fdiassilva

# v2.7.0
## Added
- Link action in AndesMessage Component.
- Link body in AndesMessage Component.
- Thumbnail Component, Icon type.
- RadioButtonGroup Component

# v2.6.0
## Added
- Checkbox Component.
- RadioButton Component.

# v2.5.1
## Added
- Add TextWatcher in TextField and TextArea.
- ShowCounter attribute added.
- The cursor position at the end of the text is set when changing the type.

# v2.5.0
## Added
- TAG Component.
- Snackbar Component.

# v2.4.0
## Added
- Badge component DOT.

## Changed
- Deprecate Font support.

## Fixed
- Proper set of default InputType in Textfield.

# v2.3.0
## Changed
- Minor UX improvements in the Andes App.
- Minor improvements in textfield.
- Minor improvements in textarea.

## Fixed
- The format property is added to the resources of the attrs.xml file, so that it can be compiled with AGP 3.6 and gradle 5 + androidx.
- Fix in AndesBadgePill. When Type, Hierarchy, or Size are changed programmatically, the application crashes

# v2.2.0
## Changed
- Remove modifier from AndesBadge.

## Fixed
- Label error textfield color.
- Helper and counter superpose.
- Error icon only showed with a helper text.

# v2.1.0
## Added
- Badge Component PILL. (Credits)
- Textfield component.
- Adding Detekt + Ktlint.
- Added more Andes icons needed. (Credits)
- Message dismiss callback.
- Message text can be selected.
- Added Font support.

## Changed
- Open icon provider.
- Message supports null body and gets invisible instead of throw error.
- Open drawable utils for painting and scaling icons.
- Migrated to Gradle 5.6.4
- Migrated to AGP 3.4.2
- R8 enabled.

# v2.0.1
## Removed
- Local font tests.

# v2.0.0
## Fixed
- Color palette and styles change.

## Changed
- Icons now are called by string path.
- The prefix was changed to "andes" instead of "andesui_"

## Added
- Offline needed icons.

## Removed
- Old unused icons.

# v1.4.2
## Added
- Test for Andes Message.

## Fixed
- Andes Message title property return body text.

# v1.4.1
## Fixed
- Background of Andes Message now displays properly on same background app color.

# v1.4.0
## Added
- What's New in the Andes app.
- Shortcut to the Andes Website in the home page of the Andes app as well as for each component showcase.
- Andes app version to the action bar to easily check which version of the Andes library you're on.
- Basic README for the Andes app module.

## Changed
- Andes app home page for a better UX.

## Fixed
- Random Andes Message generator in the Andes app now has a clear description.
- Translated Spanish wordings to English in the Andes app.
- Package for Andes Message showcase classes.
- Dependencies of Constraint layout and Card view.

# v1.3.0
## Updated
- Using WebP images to save size.

## Fixed
- Button now change to neutral Style.

# v1.2.2
## Fixed
- Some Message Types were still called States.
- Colors of the Message's buttons in quiet hierarchy.

## Added
- Stale Bot GitHub config.
- Buttons can have primary and secondary actions.
- AndesButton dynamic configuration changes in runtime.

## Refactor
- AndesColor created to wrap the use of primitives android color types.

# v1.2.1
## Fixed
- An AndesMessage couldn't be found by its id due to a missing param in the constructor super() call.
- Renamed `Highlight` type to `Neutral`.
- `States` are now `Types`.
- Changed App Name of Demo App.
- Main buttons of Demo App now have lateral margins.

## Added
- Enabled animateLayoutChanges() flag.

# v1.2.0
## Added
- Allows changes of state, hierarchy and dismiss in runtime.

## Fixed
- Properties getters and setters.

# v1.1.0
## Added
- Initial release for AndesMessage.

## Fixed
- Icon wasn't changing when AndesButton was disabled.
- Changelog updated

# v1.0.0
## Welcome!
- Initial release of Andes UI Library. AndesButton is included as the first component.
