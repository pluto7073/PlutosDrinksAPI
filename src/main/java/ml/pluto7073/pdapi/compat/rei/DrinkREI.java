package ml.pluto7073.pdapi.compat.rei;

import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.DisplaySerializerRegistry;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.api.common.util.EntryStacks;
import ml.pluto7073.pdapi.PDAPI;
import ml.pluto7073.pdapi.compat.rei.category.IngredientSequenceCategory;
import ml.pluto7073.pdapi.compat.rei.display.IngredientSequenceDisplay;
import ml.pluto7073.pdapi.item.PDItems;
import ml.pluto7073.pdapi.recipes.PDRecipeTypes;
import ml.pluto7073.pdapi.specialty.SpecialtyDrink;
import ml.pluto7073.pdapi.specialty.SpecialtyDrinkManager;
import ml.pluto7073.pdapi.util.DrinkUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DrinkREI implements REIClientPlugin {

    public static final CategoryIdentifier<IngredientSequenceDisplay> INGREDIENT_SEQUENCE = CategoryIdentifier.of(PDAPI.asId("ingredient_sequence"));

    @Override
    public void registerCategories(CategoryRegistry registry) {
        registry.add(new IngredientSequenceCategory());
        registry.addWorkstations(INGREDIENT_SEQUENCE, EntryStacks.of(PDItems.DRINK_WORKSTATION));
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        registry.registerFiller(SpecialtyDrink.class, IngredientSequenceDisplay::new);
        SpecialtyDrinkManager.values().forEach(registry::add);
    }

    @Override
    public void registerDisplaySerializer(DisplaySerializerRegistry registry) {
        registry.register(INGREDIENT_SEQUENCE, BasicDisplay.Serializer.ofSimple(IngredientSequenceDisplay::new));
    }

    public static final class Util {

        public static List<EntryIngredient> condenseIngredients(List<Ingredient> baseList) {
            ArrayList<List<ItemStack>> list = new ArrayList<>();
            for (Ingredient i : baseList) {
                if (list.isEmpty()) {
                    list.add(List.of(i.getItems()));
                    continue;
                }
                if (DrinkUtil.sameItems(Arrays.stream(i.getItems()).map(ItemStack::getItem).toArray(Item[]::new),
                        list.get(list.size() - 1).stream().map(ItemStack::getItem).toArray(Item[]::new))) {
                    list.get(list.size() - 1).forEach(stack -> stack.grow(1));
                } else {
                    list.add(List.of(Arrays.stream(i.getItems()).map(ItemStack::copy).toArray(ItemStack[]::new)));
                }
            }
            return list.stream().map(EntryIngredients::ofItemStacks).toList();
        }

    }

}
