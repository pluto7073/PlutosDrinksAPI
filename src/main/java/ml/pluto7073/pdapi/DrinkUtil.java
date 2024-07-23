package ml.pluto7073.pdapi;

import ml.pluto7073.pdapi.addition.DrinkAddition;
import ml.pluto7073.pdapi.addition.DrinkAdditions;
import ml.pluto7073.pdapi.entity.PDTrackedData;
import ml.pluto7073.pdapi.item.AbstractCustomizableDrinkItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Stack;

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

    public interface Converter<T extends Tag> {
        T convert(T startingElement);
    }

}
