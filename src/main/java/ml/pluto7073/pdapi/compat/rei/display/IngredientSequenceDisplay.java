package ml.pluto7073.pdapi.compat.rei.display;

import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.registry.RecipeManagerContext;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import ml.pluto7073.pdapi.client.ClientDrinkUtil;
import ml.pluto7073.pdapi.compat.rei.DrinkREI;
import ml.pluto7073.pdapi.component.PDComponents;
import ml.pluto7073.pdapi.specialty.SpecialtyDrink;
import ml.pluto7073.pdapi.util.DrinkUtil;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class IngredientSequenceDisplay extends BasicDisplay {

    public IngredientSequenceDisplay(Holder<SpecialtyDrink> drink) {
        super(Util.make(() -> {
            ItemStack base = drink.value().base().buildItemStack();
            Ingredient baseIngredient = Ingredient.of(base);
            if (DrinkUtil.isInProgressItem(base.getItem(), RecipeManagerContext.getInstance().getRecipeManager())) {
                if (base.has(PDComponents.FROM_ITEM)) {
                    baseIngredient = Ingredient.of(base.get(PDComponents.FROM_ITEM));
                } else {
                    Item[] bases = DrinkUtil.getPossibleBases(base.getItem(),
                            RecipeManagerContext.getInstance().getRecipeManager());
                    if (bases.length > 0) {
                        baseIngredient = Ingredient.of(bases);
                    }
                }
            }
            List<EntryIngredient> list = new ArrayList<>(List.of(EntryIngredients.ofIngredient(baseIngredient)));
            list.addAll(DrinkREI.Util.condenseIngredients(stepsToIngredientList(drink.value())));
            return list;
        }), Collections.singletonList(EntryIngredients.of(SpecialtyDrink.getAsItem(drink))), Optional.of(DrinkUtil.unwrapKey(drink)));
    }

    public static List<Ingredient> stepsToIngredientList(SpecialtyDrink drink) {
        List<Ingredient> ingredients = new ArrayList<>();
        for (ResourceLocation addition : drink.steps()) {
            ingredients.add(ClientDrinkUtil.additionToIngredient(addition));
        }
        return ingredients;
    }

    public IngredientSequenceDisplay(List<EntryIngredient> inputs, List<EntryIngredient> outputs, Optional<ResourceLocation> id) {
        super(inputs, outputs, id);
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return DrinkREI.INGREDIENT_SEQUENCE;
    }

}
