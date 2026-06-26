
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