package ml.pluto7073.pdapi.recipes;

import com.google.gson.JsonObject;
import ml.pluto7073.pdapi.DrinkUtil;
import ml.pluto7073.pdapi.addition.DrinkAdditions;
import ml.pluto7073.pdapi.block.PDBlocks;
import ml.pluto7073.pdapi.item.AbstractCustomizableDrinkItem;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.World;

import java.util.stream.Stream;

public class DrinkWorkstationRecipe implements Recipe<Inventory> {

    final Ingredient base;
    final Ingredient addition;
    final String result;
    private final Identifier id;

    public DrinkWorkstationRecipe(Identifier id, Ingredient base, Ingredient addition, String result) {
        this.base = base;
        this.addition = addition;
        this.result = result;
        this.id = id;
    }

    @Override
    public boolean matches(Inventory inventory, World world) {
        return base.test(inventory.getStack(0)) && addition.test(inventory.getStack(1));
    }

    @Override
    public ItemStack craft(Inventory inventory, DynamicRegistryManager registryManager) {
        return craft(inventory);
    }

    public ItemStack craft(Inventory inventory) {
        ItemStack stack = inventory.getStack(0).copy();
        NbtList resAdds = stack.getOrCreateSubNbt(AbstractCustomizableDrinkItem.DRINK_DATA_NBT_KEY)
                .getList(DrinkAdditions.ADDITIONS_NBT_KEY, NbtElement.STRING_TYPE);
        resAdds.add(DrinkUtil.stringAsNbt(result));
        stack.getOrCreateSubNbt(AbstractCustomizableDrinkItem.DRINK_DATA_NBT_KEY).put(DrinkAdditions.ADDITIONS_NBT_KEY, resAdds);

        return stack;
    }

    @Override
    public boolean fits(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public ItemStack getOutput(DynamicRegistryManager registryManager) {
        ItemStack stack = base.getMatchingStacks()[0].copy();
        NbtList adds = new NbtList();
        adds.add(DrinkUtil.stringAsNbt(result));
        stack.getOrCreateSubNbt(AbstractCustomizableDrinkItem.DRINK_DATA_NBT_KEY).put(DrinkAdditions.ADDITIONS_NBT_KEY, adds);
        return stack;
    }

    @Override
    public Identifier getId() {
        return this.id;
    }

    public boolean testAddition(ItemStack stack) {
        return addition.test(stack);
    }

    public boolean testBase(ItemStack stack) {
        return base.test(stack);
    }

    public ItemStack createIcon() {
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

    public boolean isEmpty() {
        return Stream.of(this.base, this.addition).anyMatch((ingredient) -> ingredient.getMatchingStacks().length == 0);
    }

    public static class Serializer implements RecipeSerializer<DrinkWorkstationRecipe> {

        public Serializer() {}

        @Override
        public DrinkWorkstationRecipe read(Identifier id, JsonObject jsonObject) {
            Ingredient ingredient = Ingredient.fromJson(JsonHelper.getObject(jsonObject, "base"));
            Ingredient ingredient2 = Ingredient.fromJson(JsonHelper.getObject(jsonObject, "addition"));
            String result = JsonHelper.getString(jsonObject, "result");
            return new DrinkWorkstationRecipe(id, ingredient, ingredient2, result);
        }

        @Override
        public DrinkWorkstationRecipe read(Identifier id, PacketByteBuf buf) {
            Ingredient ingredient = Ingredient.fromPacket(buf);
            Ingredient ingredient2 = Ingredient.fromPacket(buf);
            String result = buf.readString();
            return new DrinkWorkstationRecipe(id, ingredient, ingredient2, result);
        }

        @Override
        public void write(PacketByteBuf buf, DrinkWorkstationRecipe recipe) {
            recipe.base.write(buf);
            recipe.addition.write(buf);
            buf.writeString(recipe.result);
        }

    }

}
