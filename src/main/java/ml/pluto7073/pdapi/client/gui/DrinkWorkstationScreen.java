package ml.pluto7073.pdapi.client.gui;

import ml.pluto7073.pdapi.PDAPI;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.ItemCombinerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class DrinkWorkstationScreen extends ItemCombinerScreen<DrinkWorkstationMenu> {

    private static final ResourceLocation TEXTURE = PDAPI.asId("textures/gui/container/drink_workstation.png");

    public DrinkWorkstationScreen(DrinkWorkstationMenu handler, Inventory playerInventory, Component title) {
        super(handler, playerInventory, title, TEXTURE);
        this.titleLabelX = 60;
        this.titleLabelY = 18;
    }

    protected void renderErrorIcon(GuiGraphics context, int x, int y) {
        if (this.hasInvalidRecipe()) {
            context.blit(TEXTURE, x + 99, y + 46, this.imageWidth, 0, 28, 21);
        }
    }

    private boolean hasInvalidRecipe() {
        return (this.menu).getSlot(0).hasItem() && (this.menu).getSlot(1).hasItem() && !(this.menu).getSlot((this.menu).getResultSlot()).hasItem();
    }

}
