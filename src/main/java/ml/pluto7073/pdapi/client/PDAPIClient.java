package ml.pluto7073.pdapi.client;

import ml.pluto7073.pdapi.client.gui.DrinkWorkstationScreen;
import ml.pluto7073.pdapi.client.gui.PDScreens;
import ml.pluto7073.pdapi.networking.PDPacketsS2C;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

public class PDAPIClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        PDPacketsS2C.register();

        HandledScreens.register(PDScreens.WORKSTATION_HANDLER_TYPE, DrinkWorkstationScreen::new);
    }

}
