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

    public static int subtractiveMix(Collection<Integer> colors) {
        double key = 0;
        double cyan = 0;
        double magenta = 0;
        double yellow = 0;
        int count = 0;
        for (CMYK cmyk : colors.stream().map(CMYK::fromRGBInt).toList()) {
            key += cmyk.key;
            cyan += cmyk.cyan;
            magenta += cmyk.magenta;
            yellow += cmyk.yellow;
            count++;
        }
        return new CMYK(key / count, cyan / count, magenta / count, yellow / count).toRGB();
    }

    public record CMYK(double key, double cyan, double magenta, double yellow) {

        public static CMYK fromRGBInt(int color) {
            double red = (color >> 16 & 255) / 255.0;
            double green = (color >> 8 & 255) / 255.0;
            double blue = (color & 255) / 255.0;
            double key = 1 - Math.max(red, Math.max(green, blue));
            double cyan = (1 - red - key) / (1 - key);
            double magenta = (1 - green - key) / (1 - key);
            double yellow = (1 - blue - key) / (1 - key);
            return new CMYK(key, cyan, magenta, yellow);
        }

        public static CMYK mix(CMYK first, CMYK second) {
            double key = (first.key + second.key) / 2.0;
            double cyan = (first.cyan + second.cyan) / 2.0;
            double magenta = (first.magenta + second.magenta) / 2.0;
            double yellow = (first.yellow + second.yellow) / 2.0;
            return new CMYK(key, cyan, magenta, yellow);
        }

        public int toRGB() {
            int red = (int) (255 * (1 - cyan) * (1 - key));
            int green = (int) (255 * (1 - magenta) * (1 - key));
            int blue = (int) (255 * (1 - yellow) * (1 - key));
            return red << 16 | green << 8 | blue;
        }

    }

    @Deprecated
    public static int averageColors(Collection<Integer> colors) {
        if (colors.isEmpty()) return 0xFFFFFF;
        int r = 0;
        int g = 0;
        int b = 0;
        for (int color : colors) {
            r += (color >> 16 & 255);
            g += (color >> 8 & 255);
            b += (color & 255);
        }
        r /= colors.size();
        g /= colors.size();
        b /= colors.size();
        return r << 16 | g << 8 | b;
    }

    /**
     * Computes the color of a drink subtractively based on the additions and base color of the drink
     * @param drink The stack to compute
     * @param base The starting color of the drink
     * @param multiplicity How much influence the starting color has on the overall result. Effectively
     *                     how many times is the base color counted before the addition colors
     * @param level The current level
     * @return An RGB int with the resulting color
     */
    public static int getColorForDrinkWithDefault(ItemStack drink, int base, int multiplicity, Level level) {
        DrinkAddition[] additions = DrinkUtil.getAdditionsFromStack(drink, level);
        List<Integer> colors = Arrays.stream(additions).filter(DrinkAddition::changesColor)
                .map(DrinkAddition::getColor).collect(Collectors.toCollection(ArrayList::new));
        for (int i = 0; i < multiplicity; i++) {
            colors.addFirst(base);
        }
        return subtractiveMix(colors);
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

    private static String convertPathStackToString(Stack<String> stack) {
        if (stack.isEmpty()) return "";
        StringBuilder builder = new StringBuilder(stack.getFirst());
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

    public static float getPlayerCaffeine(Player player) {
        return CaffeineHandler.INSTANCE.get(player);
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

    public static int getDrinkColor(ItemStack stack, Level level) {
        if (!stack.is(PDItems.SPECIALTY_DRINK)) return -1;
        try {
            SpecialtyDrink drink = getSpecialDrink(stack, level);
            return drink.color();
        } catch (IllegalArgumentException e) {
            return 0xf918c5;
        }
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
