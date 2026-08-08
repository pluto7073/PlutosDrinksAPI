package ml.pluto7073.pdapi.compat.rei;

import com.google.common.collect.Lists;
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
import ml.pluto7073.pdapi.PDRegistries;
import ml.pluto7073.pdapi.compat.rei.category.DrinkAdditionCategory;
import ml.pluto7073.pdapi.compat.rei.category.IngredientSequenceCategory;
import ml.pluto7073.pdapi.compat.rei.display.DrinkAdditionDisplay;
import ml.pluto7073.pdapi.compat.rei.display.IngredientSequenceDisplay;
import ml.pluto7073.pdapi.item.PDItems;
import ml.pluto7073.pdapi.recipes.DrinkWorkstationRecipe;
import ml.pluto7073.pdapi.recipes.PDRecipeTypes;
import ml.pluto7073.pdapi.specialty.SpecialtyDrink;
import ml.pluto7073.pdapi.util.DrinkUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DrinkREI implements REIClientPlugin {

    public static final CategoryIdentifier<IngredientSequenceDisplay> INGREDIENT_SEQUENCE = CategoryIdentifier.of(PDAPI.asId("ingredient_sequence"));
    public static final CategoryIdentifier<DrinkAdditionDisplay> DRINK_ADDITION = CategoryIdentifier.of(PDAPI.asId("drink_addition"));

    @Override
    public void registerCategories(CategoryRegistry registry) {
        registry.add(new IngredientSequenceCategory());
        registry.addWorkstations(INGREDIENT_SEQUENCE, EntryStacks.of(PDItems.DRINK_WORKSTATION));
        registry.add(new DrinkAdditionCategory());
        registry.addWorkstations(DRINK_ADDITION, EntryStacks.of(PDItems.DRINK_WORKSTATION));
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        registry.registerRecipeFiller(DrinkWorkstationRecipe.class, PDRecipeTypes.DRINK_WORKSTATION_RECIPE_TYPE, DrinkAdditionDisplay::new);
        Level level = Minecraft.getInstance().level;
        if (level == null) return;
        List<Holder.Reference<SpecialtyDrink>> drinks = level.registryAccess().lookupOrThrow(PDRegistries.SPECIALITY_DRINK_KEY).listElements().toList();
        if (drinks.isEmpty()) return;
        registry.registerFiller(((Holder<SpecialtyDrink>) drinks.getFirst()).getClass(), IngredientSequenceDisplay::new);
        drinks.forEach(registry::add);
    }

    @Override
    public void registerDisplaySerializer(DisplaySerializerRegistry registry) {
        registry.register(INGREDIENT_SEQUENCE, BasicDisplay.Serializer.ofSimple(IngredientSequenceDisplay::new));
        registry.register(DRINK_ADDITION, BasicDisplay.Serializer.ofRecipeLess(DrinkAdditionDisplay::new));
    }

    public static final class Util {

        public static List<EntryIngredient> condenseIngredients(List<Ingredient> baseList) {
            ArrayList<List<ItemStack>> list = new ArrayList<>();
            for (Ingredient i : baseList) {
                if (list.isEmpty()) {
                    list.add(Lists.newArrayList(i.getItems()));
                    continue;
                }
                if (DrinkUtil.sameItems(Arrays.stream(i.getItems()).map(ItemStack::getItem).toArray(Item[]::new),
                        list.getLast().stream().map(ItemStack::getItem).toArray(Item[]::new))) {
                    list.getLast().replaceAll(stack -> stack.copyWithCount(stack.getCount() + 1));
                } else {
                    list.add(Lists.newArrayList(Arrays.stream(i.getItems()).map(ItemStack::copy).toArray(ItemStack[]::new)));
                }
            }
            return list.stream().map(EntryIngredients::ofItemStacks).toList();
        }

    }

}
