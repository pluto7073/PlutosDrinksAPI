package ml.pluto7073.pdapi.recipes;

import ml.pluto7073.pdapi.PDAPI;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public final class PDRecipeTypes {

    public static final RecipeType<DrinkWorkstationRecipe> DRINK_WORKSTATION_RECIPE_TYPE;
    public static final RecipeSerializer<DrinkWorkstationRecipe> DRINK_WORKSTATION_RECIPE_SERIALIZER;

    public static <S extends RecipeSerializer<T>, T extends Recipe<?>> S registerRecipeSerializer(String id, S serializer) {
        return Registry.register(Registries.RECIPE_SERIALIZER, PDAPI.asId(id), serializer);
    }

    static {
        DRINK_WORKSTATION_RECIPE_SERIALIZER = registerRecipeSerializer("drink_workstation", new DrinkWorkstationRecipe.Serializer());
        DRINK_WORKSTATION_RECIPE_TYPE = Registry.register(Registries.RECIPE_TYPE, PDAPI.asId("drink_workstation"), new RecipeType<DrinkWorkstationRecipe>() {
            public String toString() {
                return "pdapi:drink_workstation";
            }
        });
    }

    public static void init() {}

}
