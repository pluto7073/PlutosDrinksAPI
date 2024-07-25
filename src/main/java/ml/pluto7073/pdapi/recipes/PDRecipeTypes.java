package ml.pluto7073.pdapi.recipes;

import ml.pluto7073.pdapi.PDAPI;
import ml.pluto7073.pdapi.specialty.SpecialtyDrink;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

public final class PDRecipeTypes {

    public static final RecipeType<DrinkWorkstationRecipe> DRINK_WORKSTATION_RECIPE_TYPE;
    public static final RecipeSerializer<DrinkWorkstationRecipe> DRINK_WORKSTATION_RECIPE_SERIALIZER;

    public static final RecipeType<SpecialtyDrink> SPECIALTY_DRINK_RECIPE_TYPE;
    public static final RecipeSerializer<SpecialtyDrink> SPECIALTY_DRINK_RECIPE_SERIALIZER;

    public static <S extends RecipeSerializer<T>, T extends Recipe<?>> S registerRecipeSerializer(String id, S serializer) {
        return Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, PDAPI.asId(id), serializer);
    }

    public static <T extends Recipe<?>> RecipeType<T> registerType(String id) {
        ResourceLocation i = PDAPI.asId(id);
        return Registry.register(BuiltInRegistries.RECIPE_TYPE, i, new RecipeType<T>() {
            public String toString() {
                return i.toString();
            }
        });
    }

    static {
        DRINK_WORKSTATION_RECIPE_SERIALIZER = registerRecipeSerializer("drink_workstation", new DrinkWorkstationRecipe.Serializer());
        DRINK_WORKSTATION_RECIPE_TYPE = registerType("drink_workstation");

        SPECIALTY_DRINK_RECIPE_SERIALIZER = registerRecipeSerializer("specialty_drink", new SpecialtyDrink.Serializer());
        SPECIALTY_DRINK_RECIPE_TYPE = registerType("specialty_drink");
    }

    public static void init() {}

}
