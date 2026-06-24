package ml.pluto7073.pdapi_test;

import ml.pluto7073.pdapi.PDAPI;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PDAPITestMod implements ModInitializer {

    public static final String MODID = "pdapi_test";
    public static final Logger LOGGER = LogManager.getLogger(MODID);

    public static final Item TEST_ITEM = new TestItem(new Item.Properties());

    @Override
    public void onInitialize() {
        Registry.register(BuiltInRegistries.ITEM, id("test_item"), TEST_ITEM);
        LOGGER.info("Test mod initialized");
    }

    public static ResourceLocation id(String id) {
        return new ResourceLocation(MODID, id);
    }

}
