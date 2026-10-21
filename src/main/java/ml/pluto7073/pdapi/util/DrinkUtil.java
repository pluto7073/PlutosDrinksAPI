package ml.pluto7073.pdapi.util;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import ml.pluto7073.pdapi.PDAPI;
import ml.pluto7073.pdapi.addition.DrinkAddition;
import ml.pluto7073.pdapi.addition.DrinkAdditionManager;
import ml.pluto7073.pdapi.addition.chemicals.CaffeineHandler;
import ml.pluto7073.pdapi.item.AbstractCustomizableDrinkItem;
import ml.pluto7073.pdapi.item.PDItems;
import ml.pluto7073.pdapi.recipes.DrinkWorkstationRecipe;
import ml.pluto7073.pdapi.recipes.InProgressItemRecipe;
import ml.pluto7073.pdapi.recipes.PDRecipeTypes;
import ml.pluto7073.pdapi.specialty.SpecialtyDrink;
import ml.pluto7073.pdapi.specialty.SpecialtyDrinkManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class DrinkUtil {

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

    public static <K, V> Map<K, V> or(Map<K, V> first, Map<K, V> second, BiFunction<V, V, V> combiner) {
        HashMap<K, V> result = new HashMap<>();
        first.forEach((k, v) -> {
            if (second.containsKey(k)) {
                result.put(k, combiner.apply(v, second.get(k)));
            } else {
                result.put(k, v);
            }
        });
        second.forEach((k, v) -> {
            if (!result.containsKey(k)) {
                result.put(k, v);
            }
        });
        return ImmutableMap.copyOf(result);
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

    public static Container copyContainerContents(Container source) {
        Container container = new SimpleContainer(source.getContainerSize());
        for (int i = 0; i < source.getContainerSize(); i++) {
            container.setItem(i, source.getItem(i).copy());
        }
        return container;
    }

    public static DrinkAddition[] getAdditionsFromStack(ItemStack stack, Level level) {
        CompoundTag drinkData = stack.getOrCreateTagElement(AbstractCustomizableDrinkItem.DRINK_DATA_NBT_KEY);
        return getAdditionsFromTag(drinkData, level);
    }

    public static DrinkAddition[] getAdditionsFromTag(CompoundTag drinkData, Level level) {
        ListTag additions = drinkData.getList(DrinkAdditionManager.ADDITIONS_NBT_KEY, Tag.TAG_STRING);
        ArrayList<DrinkAddition> additionsList = new ArrayList<>();
        for (int i = 0; i < additions.size(); i++) {
            String id = additions.getString(i);
            ResourceLocation identifier = new ResourceLocation(id);
            DrinkAddition addition = level.getDrinkAdditionManager().get(identifier);
            if (addition == null) continue;
            additionsList.add(addition);
        }
        return additionsList.toArray(new DrinkAddition[0]);
    }

    public static Tag stringAsNbt(String s) {
        CompoundTag compound = new CompoundTag();
        compound.putString("string", s);
        return compound.get("string");
    }

    public static SpecialtyDrink getSpecialDrink(ItemStack stack, Level level) {
        if (!stack.is(PDItems.SPECIALTY_DRINK)) return SpecialtyDrinkManager.EMPTY;
        CompoundTag nbt = stack.getOrCreateTag();
        String id = nbt.getString("Drink");
        SpecialtyDrink drink = level.getSpecialtyDrinkManager().get(new ResourceLocation(id));
        if (drink == null) return SpecialtyDrinkManager.EMPTY;
        return drink;
    }

    public static ItemStack setSpecialDrink(ItemStack stack, SpecialtyDrink drink, Level level) {
        CompoundTag nbt = stack.getOrCreateTag();
        nbt.put("Drink", StringTag.valueOf(drink.id(level.getSpecialtyDrinkManager()).toString()));
        stack.setTag(nbt);
        return stack;
    }

    public static boolean isInProgressItem(Item item, RecipeManager recipes) {
        List<InProgressItemRecipe> items = recipes.getAllRecipesFor(PDRecipeTypes.IN_PROGRESS_RECIPE_TYPE);
        for (InProgressItemRecipe recipe : items) {
            if (recipe.getResultItem(null).is(item)) return true;
        }
        return false;
    }

    public static Item[] getPossibleBases(Item item, RecipeManager manager) {
        return manager.getAllRecipesFor(PDRecipeTypes.IN_PROGRESS_RECIPE_TYPE)
                .stream()
                .filter(recipe -> recipe.getResultItem(null).is(item))
                .map(InProgressItemRecipe::base)
                .flatMap(ingredient -> Stream.of(ingredient.getItems()))
                .map(ItemStack::getItem).toArray(Item[]::new);
    }

}
