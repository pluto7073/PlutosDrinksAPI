package ml.pluto7073.pdapi.recipes;

import com.google.gson.JsonObject;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

@MethodsReturnNonnullByDefault
public record InProgressItemRecipe(ResourceLocation id, Ingredient base, ItemStack result) implements Recipe<Container> {

    /**
     * Constructs a new Recipe for an In Progress item
     * @param id The recipe ID
     * @param base The base ingredient to convert
     * @param result The resulting in progress AbstractCustomizableDrinkItem
     */
    public InProgressItemRecipe {}

    @Override
    public boolean matches(Container inventory, Level world) {
        return base.test(inventory.getItem(0));
    }

    @Override
    public ItemStack assemble(Container inventory, RegistryAccess registryManager) {
        ItemStack result = result().copy();

        result.getOrCreateTag().putString("FromItem",
                BuiltInRegistries.ITEM.getKey(inventory.getItem(0).getItem()).toString());

        return result;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryManager) {
        return result;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return PDRecipeTypes.IN_PROGRESS_RECIPE_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return PDRecipeTypes.IN_PROGRESS_RECIPE_TYPE;
    }

    public static class Serializer implements RecipeSerializer<InProgressItemRecipe> {

        @Override
        public InProgressItemRecipe fromJson(ResourceLocation id, JsonObject json) {
            Ingredient baseItem = Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "base"));
            ItemStack resultStack = ShapedRecipe.itemStackFromJson(
                    GsonHelper.getAsJsonObject(json, "result"));
            resultStack.setCount(1);
            return new InProgressItemRecipe(id, baseItem, resultStack);
        }

        @Override
        public InProgressItemRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            Ingredient baseItem = Ingredient.fromNetwork(buf);
            ItemStack result = buf.readItem();
            return new InProgressItemRecipe(id, baseItem, result);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, InProgressItemRecipe recipe) {
            recipe.base.toNetwork(buf);
            buf.writeItem(recipe.result);
        }
    }

}
