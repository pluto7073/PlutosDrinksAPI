package ml.pluto7073.pdapi.compat.rei;

import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.DisplaySerializerRegistry;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.util.EntryStacks;
import ml.pluto7073.pdapi.PDAPI;
import ml.pluto7073.pdapi.compat.rei.category.IngredientSequenceCategory;
import ml.pluto7073.pdapi.compat.rei.display.IngredientSequenceDisplay;
import ml.pluto7073.pdapi.item.PDItems;
import ml.pluto7073.pdapi.recipes.PDRecipeTypes;
import ml.pluto7073.pdapi.specialty.SpecialtyDrink;
import ml.pluto7073.pdapi.specialty.SpecialtyDrinkManager;

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

}
