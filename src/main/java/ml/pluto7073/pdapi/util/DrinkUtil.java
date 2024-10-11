package ml.pluto7073.pdapi.util;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import ml.pluto7073.pdapi.PDAPI;
import ml.pluto7073.pdapi.addition.DrinkAddition;
import ml.pluto7073.pdapi.addition.DrinkAdditionManager;
import ml.pluto7073.pdapi.addition.chemicals.ConsumableChemicalRegistry;
import ml.pluto7073.pdapi.component.DrinkAdditions;
import ml.pluto7073.pdapi.component.PDComponents;
import ml.pluto7073.pdapi.item.AbstractCustomizableDrinkItem;
import ml.pluto7073.pdapi.item.PDItems;
import ml.pluto7073.pdapi.recipes.DrinkWorkstationRecipe;
import ml.pluto7073.pdapi.recipes.PDRecipeTypes;
import ml.pluto7073.pdapi.specialty.SpecialtyDrink;
import ml.pluto7073.pdapi.specialty.SpecialtyDrinkManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;

import java.util.*;
import java.util.function.Function;

public final class DrinkUtil {

    private static final HashMap<String, Converter<Tag>> OLD_CONVERSION_REGISTRY = new HashMap<>();
    private static final double CAFFEINE_HALF_LIFE_TICKS = 2500.0;

    public static ResourceLocation getAsId(ResourceLocation file, String dir) {
        return file.withPath(s -> s.replace(dir + '/', "").replace(".json", ""));
    }

    public static <T> Comparator<T> alphabetizer(Function<T, String> toString) {
        return (o1, o2) -> {
            String first = toString.apply(o1);
            String second = toString.apply(o2);
            int length = Math.min(first.length(), second.length());
            for (int i = 0; i < length; i++) {
                char c1 = first.charAt(i);
                char c2 = second.charAt(i);
                if (c1 == c2) continue;
                return Character.compare(c1, c2);
            }
            return Integer.compare(first.length(), second.length());
        };
    }

    public static boolean dev() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    public static <T> List<T> condense(List<T> base) {
        ArrayList<T> list = new ArrayList<>();

        for (T t : base) {
            if (list.isEmpty()) {
                list.add(t);
                continue;
            }
            if (t.equals(list.getLast())) continue;
            list.add(t);
        }
        return list;
    }

    public static <T> boolean sameItems(T[] array1, T[] array2) {
        if (array1 == array2) return true;
        if (array1 == null || array2 == null) return false;
        if (array1.length != array2.length) return false;
        List<T> list1 = List.of(array1);
        List<T> list2 = Lists.newArrayList(array2);
        for (T t1 : list1) {
            if (!list2.contains(t1)) return false;
            list2.remove(t1);
        }
        return list2.isEmpty();
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
        DrinkAdditions additions = stack.get(PDComponents.ADDITIONS);
        assert additions != null: stack.getItem() + " doesn't contain 'pdapi:additions'";
        return additions.additions().toArray(new DrinkAddition[0]);
    }

    public static void registerOldToNewConverter(String nbtPath, Converter<Tag> converter) {
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

    public static float calculateCaffeineDecay(int ticks, float originalCaffeine) {
        double exp = Math.pow(0.5, ticks / CAFFEINE_HALF_LIFE_TICKS);
        return (float) (exp * originalCaffeine);
    }

    public static float getPlayerCaffeine(Player player) {
        return ConsumableChemicalRegistry.CAFFEINE.get(player);
    }

    public static SpecialtyDrink getSpecialDrink(ItemStack stack) {
        return stack.getOrDefault(PDComponents.SPECIALTY_DRINK, SpecialtyDrink.EMPTY);
    }

    public static ItemStack setSpecialDrink(ItemStack stack, SpecialtyDrink drink) {
        stack.set(PDComponents.SPECIALTY_DRINK, drink);
        return stack;
    }

    public static int getDrinkColor(ItemStack stack) {
        if (!stack.is(PDItems.SPECIALTY_DRINK)) return -1;
        try {
            SpecialtyDrink drink = getSpecialDrink(stack);
            return 255 << 24 | drink.color();
        } catch (IllegalArgumentException e) {
            return 0xfff918c5;
        }
    }

    @Environment(EnvType.CLIENT)
    public static Ingredient additionToIngredient(ResourceLocation additionId) {
        Level level = Minecraft.getInstance().level;
        if (level == null) {
            PDAPI.LOGGER.warn("Ingredient list for \"{}\" could not be determined cause you are not in a world", additionId);
            return Ingredient.EMPTY;
        }
        List<RecipeHolder<DrinkWorkstationRecipe>> recipes = level.getRecipeManager().getAllRecipesFor(PDRecipeTypes.DRINK_WORKSTATION_RECIPE_TYPE)
                .stream().filter(r -> r.value().getResult().equals(additionId)).toList();
        if (recipes.isEmpty()) return Ingredient.EMPTY;
        List<ItemStack> matchingStacks = new ArrayList<>();
        recipes.forEach(r -> matchingStacks.addAll(Arrays.asList(r.value().getAddition().getItems())));
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
                .stream().filter(r -> r.value().getResult().equals(additionId)).toList();
        if (recipes.isEmpty()) return Ingredient.EMPTY;
        List<ItemStack> matchingStacks = new ArrayList<>();
        recipes.forEach(r -> matchingStacks.addAll(Arrays.asList(r.value().getBase().getItems())));
        if (matchingStacks.isEmpty()) return Ingredient.EMPTY;
        return Ingredient.of(matchingStacks.stream());
    }

    public interface Converter<T extends Tag> {
        T convert(T startingElement);
    }

}
