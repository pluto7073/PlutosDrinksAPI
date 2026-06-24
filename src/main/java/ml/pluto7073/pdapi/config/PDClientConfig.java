package ml.pluto7073.pdapi.config;

import ml.pluto7073.pdapi.PDAPI;
import ml.pluto7073.plutonium.annotations.BooleanOption;
import ml.pluto7073.plutonium.annotations.DoubleOption;
import ml.pluto7073.plutonium.config.JointClientConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class PDClientConfig extends JointClientConfig {

    public static final PDClientConfig INSTANCE = new PDClientConfig();

    @BooleanOption(defaultVal = true, hasTooltip = false) public boolean doCaffeineShake;
    @DoubleOption(defaultVal = 300.0) public double caffeineShakeThreshold;

    public PDClientConfig() {
        super("pdapi", PDAPI.LOGGER);
        load();
    }

}
