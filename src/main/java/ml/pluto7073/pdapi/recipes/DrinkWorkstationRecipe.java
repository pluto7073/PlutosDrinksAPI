package ml.pluto7073.pdapi.recipes;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ml.pluto7073.pdapi.PDRegistries;
import ml.pluto7073.pdapi.addition.DrinkAddition;
import ml.pluto7073.pdapi.component.DrinkAdditions;
import ml.pluto7073.pdapi.component.PDComponents;
import ml.pluto7073.pdapi.item.PDItems;
import ml.pluto7073.pdapi.block.PDBlocks;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Stream;

@MethodsReturnNonnullByDefault
public record DrinkWorkstationRecipe(Ingredient base, Ingredient addition,
                                     ResourceLocation result) implements Recipe<Container> {

    @Override
    public boolean matches(Container inventory, Level world) {
        return base.test(inventory.getItem(0)) && addition.test(inventory.getItem(1));
    }

    @Override
    public ItemStack assemble(Container inventory, HolderLookup.Provider registryManager) {
        ItemStack stack = inventory.getItem(0).copy();
        DrinkAdditions resAdds = stack.get(PDComponents.ADDITIONS).withAddition(registryManager.lookupOrThrow(PDRegistries.DRINK_ADDITION_KEY)
                .getOrThrow(ResourceKey.create(PDRegistries.DRINK_ADDITION_KEY, result)));
        stack.set(PDComponents.ADDITIONS, resAdds);

        return stack;
    }

    public ItemStack assemble(Container inventory, Level level) {
        ItemStack stack = assemble(inventory, level.registryAccess());

        List<RecipeHolder<InProgressItemRecipe>> inProgressRecipes = level.getRecipeManager()
                .getRecipesFor(PDRecipeTypes.IN_PROGRESS_RECIPE_TYPE, inventory, level);
        if (!inProgressRecipes.isEmpty()) {
            DataComponentMap map = stack.getComponents();
            stack = inProgressRecipes.getFirst().value().assemble(inventory, level.registryAccess());
            for (DataComponentType<?> type : map.keySet()) {
                if (type == PDComponents.FROM_ITEM) continue;
                copyToStackYayGenerics(type, map, stack);
            }
        }
        return stack;
    }

    public static <T> void copyToStackYayGenerics(DataComponentType<T> type, DataComponentMap source, ItemStack destination) {
        destination.set(type, source.get(type));
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider provider) {
        ItemStack stack = base.getItems()[0].copy();
        Holder.Reference<DrinkAddition> addition = provider.lookupOrThrow(PDRegistries.DRINK_ADDITION_KEY).getOrThrow(ResourceKey.create(PDRegistries.DRINK_ADDITION_KEY, result));
        stack.set(PDComponents.ADDITIONS, new DrinkAdditions(List.of(addition)));
        return stack;
    }

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

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return NonNullList.of(Ingredient.EMPTY, base, addition);
    }

    private static ItemStack buildResult(Ingredient base, BiFunction<Container, Level, ItemStack> builder) {
        ItemStack res;
        if (Arrays.stream(base.getItems()).anyMatch(stack -> stack.is(PDItems.SPECIALTY_DRINK)) || base.getItems().length < 1) {
            res = new ItemStack(PDItems.SPECIALTY_DRINK);
        } else {
            res = base.getItems()[0].copy();
        }

        return builder.apply(new SimpleContainer(res), null);
    }

    @MethodsReturnNonnullByDefault
    public static class Serializer implements RecipeSerializer<DrinkWorkstationRecipe> {

        private static final MapCodec<DrinkWorkstationRecipe> CODEC = RecordCodecBuilder.mapCodec(instance ->
                instance.group(Ingredient.CODEC.fieldOf("base").forGetter(DrinkWorkstationRecipe::base),
                                Ingredient.CODEC.fieldOf("addition").forGetter(DrinkWorkstationRecipe::addition),
                                ResourceLocation.CODEC.fieldOf("result").forGetter(DrinkWorkstationRecipe::result))
                        .apply(instance, DrinkWorkstationRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, DrinkWorkstationRecipe> STREAM_CODEC =
                StreamCodec.of(Serializer::toNetwork, Serializer::fromNetwork);

        public Serializer() {
        }

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
