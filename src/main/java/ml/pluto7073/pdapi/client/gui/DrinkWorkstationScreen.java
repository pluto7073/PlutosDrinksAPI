package ml.pluto7073.pdapi.client.gui;

import ml.pluto7073.pdapi.PDAPI;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.ForgingScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class DrinkWorkstationScreen extends ForgingScreen<DrinkWorkstationScreenHandler> {

    private static final Identifier TEXTURE = PDAPI.asId("textures/gui/container/drink_workstation.png");

    public DrinkWorkstationScreen(DrinkWorkstationScreenHandler handler, PlayerInventory playerInventory, Text title) {
        super(handler, playerInventory, title, TEXTURE);
        this.titleX = 60;
        this.titleY = 18;
    }

    protected void drawInvalidRecipeArrow(DrawContext context, int x, int y) {
        if (this.hasInvalidRecipe()) {
            context.drawTexture(TEXTURE, x + 99, y + 46, this.backgroundWidth, 0, 28, 21);
        }
    }

    private boolean hasInvalidRecipe() {
        return (this.handler).getSlot(0).hasStack() && (this.handler).getSlot(1).hasStack() && !(this.handler).getSlot((this.handler).getResultSlotIndex()).hasStack();
    }

}
