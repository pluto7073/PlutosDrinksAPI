package ml.pluto7073.pdapi.recipes;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ml.pluto7073.pdapi.component.DrinkAdditions;
import ml.pluto7073.pdapi.component.PDComponents;
import ml.pluto7073.pdapi.util.DrinkUtil;
import ml.pluto7073.pdapi.addition.DrinkAdditionManager;
import ml.pluto7073.pdapi.block.PDBlocks;
import ml.pluto7073.pdapi.item.AbstractCustomizableDrinkItem;
import ml.pluto7073.pdapi.specialty.InProgressItemRegistry;
import ml.pluto7073.pdapi.tag.PDTags;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import java.util.stream.Stream;

@MethodsReturnNonnullByDefault
public class DrinkWorkstationRecipe implements Recipe<Container> {

    final Ingredient base;
    final Ingredient addition;
    final ResourceLocation result;

    public DrinkWorkstationRecipe(Ingredient base, Ingredient addition, ResourceLocation result) {
        this.base = base;
        this.addition = addition;
        this.result = result;
    }

    @Override
    public boolean matches(Container inventory, Level world) {
        return base.test(inventory.getItem(0)) && addition.test(inventory.getItem(1));
    }

    @Override
    public ItemStack assemble(Container inventory, HolderLookup.Provider registryManager) {
        return craft(inventory);
    }

    public ItemStack craft(Container inventory) {
        ItemStack stack = inventory.getItem(0).copy();
        if (stack.is(PDTags.HAS_IN_PROGRESS_ITEM)) {
            stack = new ItemStack(InProgressItemRegistry.getInProgress(stack.getItem()));
        }
        DrinkAdditions additions = stack.getOrDefault(PDComponents.ADDITIONS, DrinkAdditions.EMPTY)
                .withAddition(DrinkAdditionManager.get(result));
        stack.set(PDComponents.ADDITIONS, additions);
        return stack;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registryManager) {
        ItemStack stack = base.getItems()[0].copy();
        stack.set(PDComponents.ADDITIONS, DrinkAdditions.of(result));
        return stack;
    }

    public Ingredient getBase() { return base; }

    public Ingredient getAddition() { return addition; }

    public ResourceLocation getResult() { return result; }

    public boolean testAddition(ItemStack stack) {
        return addition.test(stack);
    }

    public boolean testBase(ItemStack stack) {
        return base.test(stack);
    }

    public ItemStack getToastSymbol() {
        return new ItemStack(PDBlocks.DRINK_WORKSTATION);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return PDRecipeTypes.DRINK_WORKSTATION_RECIPE_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return PDRecipeTypes.DRINK_WORKSTATION_RECIPE_TYPE;
    }

    public boolean isIncomplete() {
        return Stream.of(this.base, this.addition).anyMatch((ingredient) -> ingredient.getItems().length == 0);
    }

    @MethodsReturnNonnullByDefault
    public static class Serializer implements RecipeSerializer<DrinkWorkstationRecipe> {

        private static final MapCodec<DrinkWorkstationRecipe> CODEC = RecordCodecBuilder.mapCodec(instance ->
                instance.group(Ingredient.CODEC.fieldOf("base").forGetter(DrinkWorkstationRecipe::getBase),
                        Ingredient.CODEC.fieldOf("addition").forGetter(DrinkWorkstationRecipe::getAddition),
                        ResourceLocation.CODEC.fieldOf("result").forGetter(DrinkWorkstationRecipe::getResult))
                .apply(instance, DrinkWorkstationRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, DrinkWorkstationRecipe> STREAM_CODEC =
                StreamCodec.of(Serializer::toNetwork, Serializer::fromNetwork);

        public Serializer() {}

        @Override
        public MapCodec<DrinkWorkstationRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, DrinkWorkstationRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        public static DrinkWorkstationRecipe fromNetwork(RegistryFriendlyByteBuf buf) {
            Ingredient ingredient = Ingredient.CONTENTS_STREAM_CODEC.decode(buf);
            Ingredient ingredient2 = Ingredient.CONTENTS_STREAM_CODEC.decode(buf);
            ResourceLocation result = ResourceLocation.STREAM_CODEC.decode(buf);
            return new DrinkWorkstationRecipe(ingredient, ingredient2, result);
        }

        public static void toNetwork(RegistryFriendlyByteBuf buf, DrinkWorkstationRecipe recipe) {
            Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.base);
            Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.addition);
            ResourceLocation.STREAM_CODEC.encode(buf, recipe.result);
        }

    }

}
