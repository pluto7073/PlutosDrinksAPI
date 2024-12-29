package ml.pluto7073.pdapi.compat.rei.category;

import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryStacks;
import ml.pluto7073.pdapi.compat.rei.DrinkREI;
import ml.pluto7073.pdapi.compat.rei.display.DrinkAdditionDisplay;
import ml.pluto7073.pdapi.item.PDItems;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class DrinkAdditionCategory implements DisplayCategory<DrinkAdditionDisplay> {
    @Override
    public CategoryIdentifier<? extends DrinkAdditionDisplay> getCategoryIdentifier() {
        return DrinkREI.DRINK_ADDITION;
    }

    @Override
    public List<Widget> setupDisplay(DrinkAdditionDisplay display, Rectangle bounds) {
        ArrayList<Widget> widgets = new ArrayList<>();
        Point base = new Point(bounds.getCenterX() - 41, bounds.getCenterY() - 13);
        widgets.add(Widgets.createRecipeBase(bounds));
        widgets.add(Widgets.createArrow(new Point(base.x + 27, base.y + 4)));
        widgets.add(Widgets.createResultSlotBackground(new Point(base.x + 61, base.y + 5)));
        widgets.add(Widgets.createSlot(new Point(base.x + 61, base.y + 5))
                .entries(display.getOutputEntries().get(0))
                .disableBackground()
                .markOutput());
        widgets.add(Widgets.createSlot(new Point(base.x + 4, base.y + 5))
                .entries(display.getInputEntries().get(1)).markInput());
        widgets.add(Widgets.createSlot(new Point(base.x - 16, base.y + 5))
                .entries(display.getInputEntries().get(0)).markInput());
        return widgets;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("title.pdapi.drink_addition");
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(PDItems.DRINK_WORKSTATION);
    }
}
