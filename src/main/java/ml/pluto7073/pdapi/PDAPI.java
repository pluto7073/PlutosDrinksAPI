package ml.pluto7073.pdapi;

import ml.pluto7073.pdapi.addition.action.OnDrinkSerializers;
import ml.pluto7073.pdapi.addition.chemicals.CaffeineHandler;
import ml.pluto7073.pdapi.block.PDBlocks;
import ml.pluto7073.pdapi.client.gui.PDMenuTypes;
import ml.pluto7073.pdapi.component.PDComponents;
import ml.pluto7073.pdapi.util.PDCommonConfig;
import ml.pluto7073.pdapi.entity.effect.PDMobEffects;
import ml.pluto7073.pdapi.item.PDCreativeTabs;
import ml.pluto7073.pdapi.item.PDItems;
import ml.pluto7073.pdapi.recipes.PDRecipeTypes;
import ml.pluto7073.pdapi.specialty.SpecialtyDrinkBaseSerializer;
import ml.pluto7073.plutonium.PlutoniumConfig;
import ml.pluto7073.plutonium.config.ServerConfigType;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PDAPI implements ModInitializer {

    public static final String ID = "pdapi";
    public static final Logger LOGGER = LogManager.getLogger("PDAPI");
    public static final ServerConfigType<PDCommonConfig> CONFIG_TYPE =
            Registry.register(PlutoniumConfig.SERVER_CONFIG_TYPES, asId("common"), new ServerConfigType<>(PDCommonConfig.INSTANCE, PDCommonConfig::new));


    @Override
    public void onInitialize() {
        OnDrinkSerializers.init();
        SpecialtyDrinkBaseSerializer.init();
        PDRegistries.initSynced();
        PDRecipeTypes.init();
        CaffeineHandler.init();
        PDBlocks.init();
        PDComponents.init();
        PDItems.init();
        PDCreativeTabs.init();
        PDMobEffects.init();
        PDMenuTypes.init();

        LOGGER.info("Pluto's Drinks API ready!");
    }

    public static ResourceLocation asId(String name) {
        return new ResourceLocation(ID, name);
    }

}
