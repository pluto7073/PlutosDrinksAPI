package ml.pluto7073.pdapi;

import ml.pluto7073.pdapi.addition.DrinkAddition;
import ml.pluto7073.pdapi.addition.DrinkAdditions;
import ml.pluto7073.pdapi.entity.PDTrackedData;
import ml.pluto7073.pdapi.item.AbstractCustomizableDrinkItem;
import ml.pluto7073.pdapi.item.PDItems;
import ml.pluto7073.pdapi.recipes.DrinkWorkstationRecipe;
import ml.pluto7073.pdapi.recipes.PDRecipeTypes;
import ml.pluto7073.pdapi.specialty.SpecialtyDrink;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;

import java.util.*;

public final class DrinkUtil {

    private static final HashMap<String, Converter> OLD_CONVERSION_REGISTRY = new HashMap<>();
    private static final double CAFFEINE_HALF_LIFE_TICKS = 2500.0;

    public static void convertStackFromPlutosCoffee(ItemStack stack) {
        CompoundTag nbt = stack.getOrCreateTag();
        if (!nbt.contains("Coffee")) return;
        Stack<String> currentPath = new Stack<>();
        currentPath.push("Coffee");
        CompoundTag oldCoffeeData = nbt.getCompound("Coffee");
        handleCompound(currentPath, oldCoffeeData);
        nbt.put(AbstractCustomizableDrinkItem.DRINK_DATA_NBT_KEY, oldCoffeeData);
        nbt.remove("Coffee");
    }

    private static void handleCompound(Stack<String> currentPath, CompoundTag compound) {
        for (String key : compound.getAllKeys()) {
            Tag element = compound.get(key);
            if (element == null) continue;
            currentPath.push(key);
            String current = convertPathStackToString(currentPath);
            if (OLD_CONVERSION_REGISTRY.containsKey(current)) {
                element = OLD_CONVERSION_REGISTRY.get(current).convert(element);
                compound.put(key, element);
                continue;
            }
            if (element.getId() == CompoundTag.TAG_COMPOUND) {
                handleCompound(currentPath, (CompoundTag) element);
            }
            currentPath.pop();
        }
    }

    public static Container copyContainerContents(Container source) {
        Container container = new SimpleContainer(source.getContainerSize());
        for (int i = 0; i < source.getContainerSize(); i++) {
            container.setItem(i, source.getItem(i).copy());
        }
        return container;
    }

    public static DrinkAddition[] getAdditionsFromStack(ItemStack stack) {
        convertStackFromPlutosCoffee(stack);
        CompoundTag drinkData = stack.getOrCreateTagElement(AbstractCustomizableDrinkItem.DRINK_DATA_NBT_KEY);
        ListTag additions = drinkData.getList(DrinkAdditions.ADDITIONS_NBT_KEY, Tag.TAG_STRING);
        ArrayList<DrinkAddition> additionsList = new ArrayList<>();
        for (int i = 0; i < additions.size(); i++) {
            String id = additions.getString(i);
            ResourceLocation identifier = new ResourceLocation(id);
            additionsList.add(DrinkAdditions.get(identifier));
        }
        return additionsList.toArray(new DrinkAddition[0]);
    }

    public static void registerOldToNewConverter(String nbtPath, Converter converter) {
        OLD_CONVERSION_REGISTRY.put(nbtPath, converter);
    }

    private static String convertPathStackToString(Stack<String> stack) {
        if (stack.isEmpty()) return "";
        StringBuilder builder = new StringBuilder(stack.get(0));
        for (int i = 1; i < stack.size(); i++) {
            builder.append("/").append(stack.get(i));
        }
        return builder.toString();
    }

    public static Tag stringAsNbt(String s) {
        CompoundTag compound = new CompoundTag();
        compound.putString("string", s);
        return compound.get("string");
    }

    public static void setPlayerCaffeine(Player player, float caffeine) {
        player.getEntityData().set(PDTrackedData.PLAYER_TICKS_SINCE_LAST_CAFFEINE, 0);
        player.getEntityData().set(PDTrackedData.PLAYER_CAFFEINE_AMOUNT, caffeine);
        player.getEntityData().set(PDTrackedData.PLAYER_ORIGINAL_CAFFEINE_AMOUNT, caffeine);
    }

    public static float calculateCaffeineDecay(int ticks, float originalCaffeine) {
        double exp = Math.pow(0.5, ticks / CAFFEINE_HALF_LIFE_TICKS);
        return (float) (exp * originalCaffeine);
    }

    public static float getPlayerCaffeine(Player player) {
        return player.getEntityData().get(PDTrackedData.PLAYER_CAFFEINE_AMOUNT);
    }

    public static SpecialtyDrink getSpecialDrink(ItemStack stack) {
        CompoundTag nbt = stack.getOrCreateTag();
        String id = nbt.getString("Drink");
        SpecialtyDrink drink = SpecialtyDrink.DRINKS.get(new ResourceLocation(id));
        if (drink == null) throw new IllegalArgumentException("Drink " + id + " does not exist");
        return drink;
    }

    public static ItemStack setSpecialDrink(ItemStack stack, SpecialtyDrink drink) {
        CompoundTag nbt = stack.getOrCreateTag();
        nbt.put("Drink", StringTag.valueOf(drink.id().toString()));
        stack.setTag(nbt);
        return stack;
    }

    public static int getDrinkColor(ItemStack stack) {
        if (!stack.is(PDItems.SPECIALTY_DRINK)) return -1;
        try {
            SpecialtyDrink drink = getSpecialDrink(stack);
            return drink.color();
        } catch (IllegalArgumentException e) {
            return 0xf918c5;
        }
    }

    public static Ingredient additionToIngredient(ResourceLocation additionId) {
        Level level = Minecraft.getInstance().level;
        if (level == null) {
            PDAPI.LOGGER.warn("Ingredient list for \"{}\" could not be determined cause you are not in a world", additionId);
            return Ingredient.EMPTY;
        }
        List<DrinkWorkstationRecipe> recipes = level.getRecipeManager().getAllRecipesFor(PDRecipeTypes.DRINK_WORKSTATION_RECIPE_TYPE)
                .stream().filter(r -> r.getResultId().equals(additionId)).toList();
        if (recipes.isEmpty()) return Ingredient.EMPTY;
        List<ItemStack> matchingStacks = new ArrayList<>();
        recipes.forEach(r -> matchingStacks.addAll(Arrays.asList(r.getAddition().getItems())));
        if (matchingStacks.isEmpty()) return Ingredient.EMPTY;
        return Ingredient.of(matchingStacks.stream());
    }

    public interface Converter<T extends Tag> {
        T convert(T startingElement);
    }

}
