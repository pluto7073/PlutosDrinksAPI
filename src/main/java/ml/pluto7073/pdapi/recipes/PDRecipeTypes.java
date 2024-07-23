package ml.pluto7073.pdapi.recipes;

import ml.pluto7073.pdapi.PDAPI;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

public final class PDRecipeTypes {

    public static final RecipeType<DrinkWorkstationRecipe> DRINK_WORKSTATION_RECIPE_TYPE;
    public static final RecipeSerializer<DrinkWorkstationRecipe> DRINK_WORKSTATION_RECIPE_SERIALIZER;

    public static <S extends RecipeSerializer<T>, T extends Recipe<?>> S registerRecipeSerializer(String id, S serializer) {
        return Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, PDAPI.asId(id), serializer);
    }

    static {
        DRINK_WORKSTATION_RECIPE_SERIALIZER = registerRecipeSerializer("drink_workstation", new DrinkWorkstationRecipe.Serializer());
        DRINK_WORKSTATION_RECIPE_TYPE = Registry.register(BuiltInRegistries.RECIPE_TYPE, PDAPI.asId("drink_workstation"), new RecipeType<DrinkWorkstationRecipe>() {
            public String toString() {
                return "pdapi:drink_workstation";
            }
        });
    }

    public static void init() {}

}
