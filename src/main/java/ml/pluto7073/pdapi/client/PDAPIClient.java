package ml.pluto7073.pdapi.client;

import ml.pluto7073.pdapi.block.PDBlocks;
import ml.pluto7073.pdapi.util.DrinkUtil;
import ml.pluto7073.pdapi.client.gui.DrinkWorkstationScreen;
import ml.pluto7073.pdapi.client.gui.PDScreens;
import ml.pluto7073.pdapi.item.PDItems;
import ml.pluto7073.pdapi.networking.PDClientboundPackets;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.RenderType;

public class PDAPIClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        PDClientboundPackets.register();

        MenuScreens.register(PDScreens.WORKSTATION_MENU_TYPE, DrinkWorkstationScreen::new);

        ColorProviderRegistry.ITEM.register((stack, index) -> index > 0 ? -1 : DrinkUtil.getDrinkColor(stack, Minecraft.getInstance().level), PDItems.SPECIALTY_DRINK);
        ColorProviderRegistry.ITEM.register((stack, index) -> index > 0 ? 0 : -1, PDItems.MUG);
        ColorProviderRegistry.BLOCK.register((state, getter, pos, index) -> index > 0 ? 0 : -1, PDBlocks.MUG);

        BlockRenderLayerMap.INSTANCE.putBlock(PDBlocks.MUG, RenderType.cutoutMipped());
    }

}
