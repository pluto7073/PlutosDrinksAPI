package ml.pluto7073.pdapi.client;

import ml.pluto7073.pdapi.client.gui.DrinkWorkstationScreen;
import ml.pluto7073.pdapi.client.gui.PDScreens;
import ml.pluto7073.pdapi.networking.PDPacketsS2C;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screens.MenuScreens;

public class PDAPIClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        PDPacketsS2C.register();

        MenuScreens.register(PDScreens.WORKSTATION_MENU_TYPE, DrinkWorkstationScreen::new);
    }

}
