package ml.pluto7073.pdapi.compat.rei.display;

import com.google.common.collect.Lists;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import ml.pluto7073.pdapi.compat.rei.DrinkREI;
import ml.pluto7073.pdapi.specialty.InProgressItemRegistry;
import ml.pluto7073.pdapi.specialty.SpecialtyDrink;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class IngredientSequenceDisplay extends BasicDisplay {

    public IngredientSequenceDisplay(SpecialtyDrink drink) {
        super(Util.make(() -> {
            ItemStack base = new ItemStack(drink.base());
            if (InProgressItemRegistry.isInProgressItem(base.getItem())) {
                base = new ItemStack(InProgressItemRegistry.getBase(base.getItem()));
            }
            List<EntryIngredient> list = new ArrayList<>(List.of(EntryIngredients.of(base)));
            list.addAll(DrinkREI.Util.condenseIngredients(drink.stepsToIngredientList()));
            return list;
        }), Collections.singletonList(EntryIngredients.of(drink.getAsItem())), Optional.of(drink.id()));
    }

    public IngredientSequenceDisplay(List<EntryIngredient> inputs, List<EntryIngredient> outputs, Optional<ResourceLocation> id) {
        super(inputs, outputs, id);
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return DrinkREI.INGREDIENT_SEQUENCE;
    }

}
