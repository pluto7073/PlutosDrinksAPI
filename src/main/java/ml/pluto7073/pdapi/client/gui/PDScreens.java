package ml.pluto7073.pdapi.client.gui;

import ml.pluto7073.pdapi.PDAPI;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandlerType;

public final class PDScreens {

    public static final ScreenHandlerType<DrinkWorkstationScreenHandler> WORKSTATION_HANDLER_TYPE;

    public static void init() {}

    static {
        WORKSTATION_HANDLER_TYPE = Registry.register(Registries.SCREEN_HANDLER, PDAPI.asId("drink_workstation"), new ScreenHandlerType<>(DrinkWorkstationScreenHandler::new, FeatureFlags.VANILLA_FEATURES));
    }

}
