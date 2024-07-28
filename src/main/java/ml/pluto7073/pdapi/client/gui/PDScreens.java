package ml.pluto7073.pdapi.client.gui;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;

public final class PDScreens {

    public static final MenuType<DrinkWorkstationMenu> WORKSTATION_MENU_TYPE;

    public static void init() {}

    static {
        WORKSTATION_MENU_TYPE = Registry.register(BuiltInRegistries.MENU, "plutoscoffee:coffee_workstation", new MenuType<>(DrinkWorkstationMenu::new, FeatureFlags.VANILLA_SET));
    }

}
