Pluto's Drinks API (or PDAPI for short) is a (not so) simple API mod for the Pluto's Drinks Series that adds customizable drinks.

## Features
While this is an API mod, it does add some Runtime features into the game, including:
- A Drink Workstation block for adding ingredients into drinks (Useless without another Drink mod)
- `/drink` command for setting/retrieving amount of caffeine (and other chemicals) in players
- Several built-in drink additions (list can be found on the [wiki](https://github.com/pluto7073/PlutosDrinksAPI/wiki)) and the ability to create custom drink additions using datapacks

## Heads Up
While you are free to use this in your own projects, this API is still in Beta and I am still developing the Pluto's Drinks Series, meaning I will often rewrite and fiddle around with various parts of the code thus causing several breaking changes in minor versions of the mod.

Nonetheless...

## Usage
If you wish to use this API in your own Mods, add the following to your `build.gradle` and `gradle.properties` files

`build.gradle`
```groovy
repositories {
  maven { url = "https://maven.shedaniel.me/" } // Cloth Config
  maven { url = "https://pluto-mod-maven.web.app/maven" } // PDAPI
}

dependencies {
  modImplementation "ml.pluto7073:pdapi:${minecraft_version}+${pdapi_version}"
}
```

`gradle.properties`
```properties
pdapi_version=MOST_RECENT_VERSION
```

## Forge Version?
No, I lack the time, attention span, and motivation to learn an entirely new modloader

## Modpacks?
Completely fine as long as you credit me and link back to the Modrinth or GitHub page for this mod.