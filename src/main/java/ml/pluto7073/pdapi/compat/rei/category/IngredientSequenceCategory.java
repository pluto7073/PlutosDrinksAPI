package ml.pluto7073.pdapi.compat.rei.category;

import com.mojang.blaze3d.systems.RenderSystem;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.DrawableConsumer;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryStacks;
import ml.pluto7073.pdapi.PDAPI;
import ml.pluto7073.pdapi.block.PDBlocks;
import ml.pluto7073.pdapi.compat.rei.DrinkREI;
import ml.pluto7073.pdapi.compat.rei.display.IngredientSequenceDisplay;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class IngredientSequenceCategory implements DisplayCategory<IngredientSequenceDisplay> {

    private static final ResourceLocation RECIPE_ARROW = PDAPI.asId("textures/gui/rei/arrow.png");

    @Override
    public List<Widget> setupDisplay(IngredientSequenceDisplay display, Rectangle bounds) {
        ArrayList<Widget> widgets = new ArrayList<>();
        int x = bounds.x;
        int y = (DisplayCategory.super.getDisplayHeight() + 32) / 2 + bounds.y;

        widgets.add(Widgets.createRecipeBase(bounds));

        // Base Drink
        widgets.add(Widgets.createSlot(new Point(x + 16, y - 28))
                .markInput().entries(display.getInputEntries().get(0)));
        widgets.add(Widgets.createDrawableWidget(arrow(new Point(16, y))));

        // Additions
        for (int i = 1; i < display.getInputEntries().size(); i++) {
            int offset = i > 10 ? 40 : i > 5 ? 20 : 0;
            int xOff = i > 10 ? i - 10 : i > 5 ? i - 5 : i;
            widgets.add(Widgets.createSlot(new Point(x + 16 + 20 * xOff, y - 28 + offset))
                    .markInput().entries(display.getInputEntries().get(i)));
        }

        // Output
        int xBase = Math.min(display.getInputEntries().size() - 1, 5);
        widgets.add(Widgets.createResultSlotBackground(new Point(x + bounds.getWidth() - 32, y + 40)));
        widgets.add(Widgets.createArrow(new Point(x + 14 + 20 * xBase, y + 38)));
        widgets.add(Widgets.createSlot(new Point(x + bounds.getWidth() - 32, y + 40))
                .markOutput().disableBackground().entries(display.getOutputEntries().get(0)));

        return widgets;
    }

    @Override
    public CategoryIdentifier<? extends IngredientSequenceDisplay> getCategoryIdentifier() {
        return DrinkREI.INGREDIENT_SEQUENCE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("title.pdapi.ingredient_sequence");
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(new ItemStack(PDBlocks.DRINK_WORKSTATION));
    }

    @Override
    public int getDisplayWidth(IngredientSequenceDisplay display) {
        return 64 + 20 * (Math.min(display.getInputEntries().size() - 1, 5)) + 14;
    }

    @Override
    public int getDisplayHeight() {
        return DisplayCategory.super.getDisplayHeight() + 52;
    }

    private static DrawableConsumer arrow(Point point) {
        final int x = point.x;
        final int y = point.y;
        return (graphics, mouseX, mouseY, delta) -> {
            RenderSystem.setShaderTexture(0, RECIPE_ARROW);
            graphics.blit(RECIPE_ARROW, x, y,0, 0, 18, 18);
        };
    }

}
