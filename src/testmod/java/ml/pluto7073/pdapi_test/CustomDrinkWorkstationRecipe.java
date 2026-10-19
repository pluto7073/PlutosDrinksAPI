package ml.pluto7073.pdapi_test;

import ml.pluto7073.pdapi.recipes.DrinkWorkstationRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;

public abstract class CustomDrinkWorkstationRecipe extends DrinkWorkstationRecipe {

    public CustomDrinkWorkstationRecipe(Ingredient base, Ingredient addition, ResourceLocation result) {
        super(base, addition, result);
    }

}
