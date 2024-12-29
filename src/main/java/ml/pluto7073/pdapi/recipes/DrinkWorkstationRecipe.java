package ml.pluto7073.pdapi.recipes;

import com.google.gson.JsonObject;
import ml.pluto7073.pdapi.item.PDItems;
import ml.pluto7073.pdapi.util.DrinkUtil;
import ml.pluto7073.pdapi.addition.DrinkAdditionManager;
import ml.pluto7073.pdapi.block.PDBlocks;
import ml.pluto7073.pdapi.item.AbstractCustomizableDrinkItem;
import ml.pluto7073.pdapi.specialty.InProgressItemRegistry;
import ml.pluto7073.pdapi.tag.PDTags;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Stream;

@MethodsReturnNonnullByDefault
public class DrinkWorkstationRecipe implements Recipe<Container> {

    final Ingredient base;
    final Ingredient addition;
    ItemStack resultStack = null;
    final String result;
    private final ResourceLocation id;

    public DrinkWorkstationRecipe(ResourceLocation id, Ingredient base, Ingredient addition, String result) {
        this.base = base;
        this.addition = addition;
        this.result = result;
        this.id = id;
    }

    @Override
    public boolean matches(Container inventory, Level world) {
        return base.test(inventory.getItem(0)) && addition.test(inventory.getItem(1));
    }

    @Override
    public ItemStack assemble(Container inventory, RegistryAccess registryManager) {
        return craft(inventory);
    }

    public ItemStack craft(Container inventory) {
        ItemStack stack = inventory.getItem(0).copy();
        ListTag resAdds = stack.getOrCreateTagElement(AbstractCustomizableDrinkItem.DRINK_DATA_NBT_KEY)
                .getList(DrinkAdditionManager.ADDITIONS_NBT_KEY, Tag.TAG_STRING);
        resAdds.add(DrinkUtil.stringAsNbt(result));
        stack.getOrCreateTagElement(AbstractCustomizableDrinkItem.DRINK_DATA_NBT_KEY).put(DrinkAdditionManager.ADDITIONS_NBT_KEY, resAdds);
        if (stack.is(PDTags.HAS_IN_PROGRESS_ITEM)) {
            CompoundTag tag = stack.getOrCreateTag();
            stack = new ItemStack(InProgressItemRegistry.getInProgress(stack.getItem()));
            stack.setTag(tag);
        }
        return stack;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryManager) {
        if (resultStack == null) {
            resultStack = buildResult(base, this::craft);
        }
        return resultStack;
    }

    public ResourceLocation getResultId() {
        return new ResourceLocation(result);
    }

    public Ingredient getBase() { return base; }

    public Ingredient getAddition() { return addition; }

    @Override
    public ResourceLocation getId() {
        return this.id;
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

    private static ItemStack buildResult(Ingredient base, Function<Container, ItemStack> builder) {
        ItemStack res;
        if (Arrays.stream(base.getItems()).anyMatch(stack -> stack.is(PDItems.SPECIALTY_DRINK)) || base.getItems().length < 1) {
            res = new ItemStack(PDItems.SPECIALTY_DRINK);
        } else {
            res = base.getItems()[0].copy();
        }

        return builder.apply(new SimpleContainer(res));
    }

    @MethodsReturnNonnullByDefault
    public static class Serializer implements RecipeSerializer<DrinkWorkstationRecipe> {

        public Serializer() {}

        @Override
        public DrinkWorkstationRecipe fromJson(ResourceLocation id, JsonObject jsonObject) {
            Ingredient ingredient = Ingredient.fromJson(GsonHelper.getAsJsonObject(jsonObject, "base"));
            Ingredient ingredient2 = Ingredient.fromJson(GsonHelper.getAsJsonObject(jsonObject, "addition"));
            String result = GsonHelper.getAsString(jsonObject, "result");
            return new DrinkWorkstationRecipe(id, ingredient, ingredient2, result);
        }

        @Override
        public DrinkWorkstationRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            Ingredient ingredient = Ingredient.fromNetwork(buf);
            Ingredient ingredient2 = Ingredient.fromNetwork(buf);
            String result = buf.readUtf();
            return new DrinkWorkstationRecipe(id, ingredient, ingredient2, result);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, DrinkWorkstationRecipe recipe) {
            recipe.base.toNetwork(buf);
            recipe.addition.toNetwork(buf);
            buf.writeUtf(recipe.result);
        }

    }

}
