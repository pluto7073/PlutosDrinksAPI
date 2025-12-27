package ml.pluto7073.pdapi.config;

import ml.pluto7073.pdapi.PDAPI;
import ml.pluto7073.plutonium.annotations.BooleanOption;
import ml.pluto7073.plutonium.annotations.DoubleOption;
import ml.pluto7073.plutonium.annotations.IntOption;
import ml.pluto7073.plutonium.config.JointServerConfig;
import ml.pluto7073.plutonium.config.ServerConfigType;
import org.apache.logging.log4j.Logger;

public class PDCommonConfig extends JointServerConfig {

    public static final PDCommonConfig INSTANCE = new PDCommonConfig();

    @BooleanOption(defaultVal = true) public boolean doCaffeineOverdose;
    @DoubleOption(defaultVal = 1.0, hasTooltip = false) public double caffeineDamageMultiplier;
    @IntOption(defaultVal = 5000, hasTooltip = false) public int lethalCaffeineDose;

    public PDCommonConfig() {
        super("pdapi", PDAPI.LOGGER);
        load();
    }

}
