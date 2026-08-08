package ml.pluto7073.pdapi.recipes;

import com.google.gson.JsonObject;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ml.pluto7073.pdapi.component.PDComponents;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

@MethodsReturnNonnullByDefault
public record InProgressItemRecipe(Ingredient base, ItemStack result) implements Recipe<Container> {

    /**
     * Constructs a new Recipe for an In Progress item
     * @param base The base ingredient to convert
     * @param result The resulting in progress AbstractCustomizableDrinkItem
     */
    public InProgressItemRecipe {}

    @Override
    public boolean matches(Container inventory, Level world) {
        return base.test(inventory.getItem(0));
    }

    @Override
    public ItemStack assemble(Container inventory, HolderLookup.Provider registryManager) {
        ItemStack result = result().copy();

        result.set(PDComponents.FROM_ITEM, inventory.getItem(0).getItem());

        return result;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return result;
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

        public static final MapCodec<InProgressItemRecipe> CODEC = RecordCodecBuilder.mapCodec(instance ->
                instance.group(Ingredient.CODEC_NONEMPTY.fieldOf("base").forGetter(InProgressItemRecipe::base),
                        ItemStack.CODEC.fieldOf("result").forGetter(InProgressItemRecipe::result))
                        .apply(instance, InProgressItemRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, InProgressItemRecipe> STREAM_CODEC =
                StreamCodec.of(Serializer::toNetwork, Serializer::fromNetwork);

        private static InProgressItemRecipe fromNetwork(RegistryFriendlyByteBuf buf) {
            Ingredient baseItem = Ingredient.CONTENTS_STREAM_CODEC.decode(buf);
            ItemStack result = ItemStack.STREAM_CODEC.decode(buf);
            return new InProgressItemRecipe(baseItem, result);
        }

        private static void toNetwork(RegistryFriendlyByteBuf buf, InProgressItemRecipe recipe) {
            Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.base);
            ItemStack.STREAM_CODEC.encode(buf, recipe.result);
        }

        @Override
        public MapCodec<InProgressItemRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, InProgressItemRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }

}
