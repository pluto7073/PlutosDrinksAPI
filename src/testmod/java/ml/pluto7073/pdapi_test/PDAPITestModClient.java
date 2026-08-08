package ml.pluto7073.pdapi_test;

import ml.pluto7073.pdapi.PDAPI;
import ml.pluto7073.pdapi.item.PDCreativeTabs;
import ml.pluto7073.pdapi.util.DrinkUtil;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.client.Minecraft;

public class PDAPITestModClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ColorProviderRegistry.ITEM.register((stack, i) ->
                i > 0 ? -1 :
                DrinkUtil.getColorForDrinkWithDefault(stack, 0x0000FF),
                PDAPITestMod.TEST_ITEM);
        ItemGroupEvents.modifyEntriesEvent(PDCreativeTabs.SPECIALTY_DRINKS_TAB)
                .register(entries -> entries.accept(PDAPITestMod.TEST_ITEM));
    }

}
