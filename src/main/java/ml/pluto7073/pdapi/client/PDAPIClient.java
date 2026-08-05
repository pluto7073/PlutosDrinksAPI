package ml.pluto7073.pdapi.client;

import ml.pluto7073.pdapi.PDAPI;
import ml.pluto7073.pdapi.block.PDBlocks;
import ml.pluto7073.pdapi.item.PDCreativeTabs;
import ml.pluto7073.pdapi.specialty.SpecialtyDrink;
import ml.pluto7073.pdapi.specialty.SpecialtyDrinkManager;
import ml.pluto7073.pdapi.util.DrinkUtil;
import ml.pluto7073.pdapi.client.gui.DrinkWorkstationScreen;
import ml.pluto7073.pdapi.client.gui.PDMenuTypes;
import ml.pluto7073.pdapi.item.PDItems;
import ml.pluto7073.pdapi.networking.PDClientboundPackets;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Items;

@Environment(EnvType.CLIENT)
public class PDAPIClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        initEntries();
        PDClientboundPackets.registerReceivers();

        MenuScreens.register(PDMenuTypes.WORKSTATION_MENU_TYPE, DrinkWorkstationScreen::new);

        ColorProviderRegistry.ITEM.register((stack, index) -> index > 0 ? -1 : DrinkUtil.getDrinkColor(stack, Minecraft.getInstance().level), PDItems.SPECIALTY_DRINK);
        ColorProviderRegistry.ITEM.register((stack, index) -> index > 0 ? 0 : -1, PDItems.MUG);
        ColorProviderRegistry.BLOCK.register((state, getter, pos, index) -> index > 0 ? 0 : -1, PDBlocks.MUG);

        BlockRenderLayerMap.INSTANCE.putBlock(PDBlocks.MUG, RenderType.cutoutMipped());
    }

    public static void initEntries() {
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS).register(entries -> {
            entries.addAfter(Items.SMITHING_TABLE, PDItems.DRINK_WORKSTATION);
            entries.addAfter(Items.FLOWER_POT, PDItems.MUG);
        });
        ItemGroupEvents.modifyEntriesEvent(PDCreativeTabs.SPECIALTY_DRINKS_TAB).register(stacks -> {
            SpecialtyDrinkManager manager = Minecraft.getInstance().level.getSpecialtyDrinkManager();
            for (SpecialtyDrink d : manager.values()
                    .stream().sorted(DrinkUtil.alphabetizer(drink -> drink.languageKey(manager))).toList()) {
                stacks.accept(d.getAsItem(Minecraft.getInstance().level));
            }
        });

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.FOOD_AND_DRINKS).register(entries -> entries.addAfter(Items.MILK_BUCKET, PDItems.MILK_BOTTLE));

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.INGREDIENTS).register(entries -> entries.addAfter(Items.GLASS_BOTTLE, PDItems.MUG));
    }

}
