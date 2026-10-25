
## Changes
- Holding down the shift key will drink an entire drink without sipping it

## Fixes
- Advancements triggered upon drinking customizable drinks don't trigger
- Specialty Drinks apply actions on each sip instead of after the drink is fully drank
- No Sip Action doesn't work

---

## Fixes
- Specialty drinks not taking into account base drink data

---

## Changes
- Drink colors are now computed subtractively to get more accurate colors
- To conform with Dynamic Registries in the 1.21.1 version, the `drink_additions` and `specialty_drinks`
folders have been moved to `pdapi/drink_addition` and `pdapi/specialty_drink` respectively.  See wiki
for new datapack instructions if unclear

### API Changes
- Color computation methods have been moved from `DrinkUtil` to the new `ColorUtil` class

---

## Fixes
- Specialty drink base can't be found from an empty item stack, causing a crash

---

## Fixes
- Parsing drink additions from item NBT can include `null` additions causing crashes
- Client sends empty Drink and Addition registries to itself overriding newly received additions and drinks on Singleplayer

---

## Fixes
- Client copy of specialty drinks and drink additions stored in wrong location causing crashes and errors when changing dimensions

---

## Fixes
- Items with no/empty tags have issues stacking together

---

## Changes
- Mod now requires Java 21 and Fabric Loader 0.18
- Milk no longer restores hunger
- Specialty Drink items are now called Specialty Drinks, and the drink name is shown in the tooltip

## Fixes
- Fixed a bug causing dedicated servers to crash on startup

---

## Fixes
- Drinks that have been drank from should not be editable with a workstation

---

## Additions
- Added an empty mug item for use in Pluto's Coffee and Tea Time
- Drinks in mugs can be given additions by right clicking on the cup with the ingredient

## Changes
- Sip amounts are no longer determined by item durability, rather by a custom tag called `Sipped`.
- The "Specialty Drinks" creative tab icon has been changed to the PDAPI Icon (a custom item only obtainable through commands)
- The current level must be used to obtain the Specialty Drink and Drink Addition managers
  - use `Level.getSpecialtyDrinkManager()` and `Level.getDrinkAdditionManager`
- Adds support for (and embeds) Chemicals 2.0.x, using `ConsumedInstances` and `AbsorptionType`s

---

## Changes
- Server config is no longer controlled by gamerules, and now uses a server config.
  - Modmenu can be used to edit the config in game, or you can edit the `pdapi_server.json` in the config folder
  - Cloth Config is required to edit the config using ModMenu
- There's also a new client config, where you can edit Shaking settings (`pdapi_client.json`)

---

## Additions
- The sipping feature from bartending has been added to PDAPI, now all drinks inheriting `AbstractCustomizableDrinkItem` are drank in sips
  - Total ounces are determined via base drink and additions added

## Changes
- Drink Additions now have a `volume` component that is used to determine sip size. Default is 0
  - Built in addition volumes:
    - Chorus Fruit - 0.25oz
    - Glow Berries - 0.25oz
    - Honey - 0.25oz
    - Ice - 4oz
    - Milk - 1oz
    - Sugar - 0.25oz
    - Pumpkin Slice (farmersdelight) - 0.25oz
- Specialty drinks have an optional volume component, if not specified then volume will be determined from base and additions
- Items cannot be added to drinks that have already been partially consumed