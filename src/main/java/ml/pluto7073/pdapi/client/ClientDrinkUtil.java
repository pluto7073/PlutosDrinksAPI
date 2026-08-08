package ml.pluto7073.pdapi.client;

import ml.pluto7073.pdapi.PDAPI;
import ml.pluto7073.pdapi.recipes.DrinkWorkstationRecipe;
import ml.pluto7073.pdapi.recipes.PDRecipeTypes;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClientDrinkUtil {

    @Environment(EnvType.CLIENT)
    public static Ingredient additionToIngredient(ResourceLocation additionId) {
        Level level = Minecraft.getInstance().level;
        if (level == null) {
            PDAPI.LOGGER.warn("Ingredient list for \"{}\" could not be determined cause you are not in a world", additionId);
            return Ingredient.EMPTY;
        }
        List<RecipeHolder<DrinkWorkstationRecipe>> recipes = level.getRecipeManager().getAllRecipesFor(PDRecipeTypes.DRINK_WORKSTATION_RECIPE_TYPE)
                .stream().filter(r -> r.id().equals(additionId)).toList();
        if (recipes.isEmpty()) return Ingredient.EMPTY;
        List<ItemStack> matchingStacks = new ArrayList<>();
        recipes.forEach(r -> matchingStacks.addAll(Arrays.asList(r.value().addition().getItems())));
        if (matchingStacks.isEmpty()) return Ingredient.EMPTY;
        return Ingredient.of(matchingStacks.stream());
    }

    @Environment(EnvType.CLIENT)
    public static Ingredient getValidBasesForAddition(ResourceLocation additionId) {
        Level level = Minecraft.getInstance().level;
        if (level == null) {
            PDAPI.LOGGER.warn("Valid bases for \"{}\" can only be retrieved when a level is loaded", additionId);
            return Ingredient.EMPTY;
        }
        List<RecipeHolder<DrinkWorkstationRecipe>> recipes = level.getRecipeManager().getAllRecipesFor(PDRecipeTypes.DRINK_WORKSTATION_RECIPE_TYPE)
                .stream().filter(r -> r.value().result().equals(additionId)).toList();
        if (recipes.isEmpty()) return Ingredient.EMPTY;
        List<ItemStack> matchingStacks = new ArrayList<>();
        recipes.forEach(r -> matchingStacks.addAll(Arrays.asList(r.value().base().getItems())));
        if (matchingStacks.isEmpty()) return Ingredient.EMPTY;
        return Ingredient.of(matchingStacks.stream());
    }

}
