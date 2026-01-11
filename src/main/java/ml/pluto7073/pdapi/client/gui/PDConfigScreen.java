package ml.pluto7073.pdapi.client.gui;

import ml.pluto7073.pdapi.PDAPI;
import ml.pluto7073.pdapi.config.PDClientConfig;
import ml.pluto7073.plutonium.client.gui.PlutoniumConfigScreen;
import ml.pluto7073.plutonium.config.ClientConfig;
import ml.pluto7073.plutonium.config.ServerConfigType;

public class PDConfigScreen extends PlutoniumConfigScreen {

    public static final PDConfigScreen INSTANCE = new PDConfigScreen();

    private PDConfigScreen() {
        super(PDClientConfig.INSTANCE, PDAPI.CONFIG_TYPE, PDAPI.ID);
    }

}
