package ml.pluto7073.pdapi;

import ml.pluto7073.pdapi.addition.DrinkAddition;
import ml.pluto7073.pdapi.addition.DrinkAdditions;
import ml.pluto7073.pdapi.item.AbstractCustomizableDrinkItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtTypes;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

public final class DrinkUtil {

    private static final HashMap<String, Converter> OLD_CONVERSION_REGISTRY = new HashMap<>();
    private static final double CAFFEINE_HALF_LIFE_TICKS = 2500.0;

    public static void convertStackFromPlutosCoffee(ItemStack stack) {
        NbtCompound nbt = stack.getOrCreateNbt();
        if (!nbt.contains("Coffee")) return;
        Stack<String> currentPath = new Stack<>();
        currentPath.push("Coffee");
        NbtCompound oldCoffeeData = nbt.getCompound("Coffee");
        handleCompound(currentPath, oldCoffeeData);
        nbt.put(AbstractCustomizableDrinkItem.DRINK_DATA_NBT_KEY, oldCoffeeData);
        nbt.remove("Coffee");
    }

    private static void handleCompound(Stack<String> currentPath, NbtCompound compound) {
        for (String key : compound.getKeys()) {
            NbtElement element = compound.get(key);
            if (element == null) continue;
            currentPath.push(key);
            String current = convertPathStackToString(currentPath);
            if (OLD_CONVERSION_REGISTRY.containsKey(current)) {
                element = OLD_CONVERSION_REGISTRY.get(current).convert(element);
                compound.put(key, element);
                continue;
            }
            if (element.getType() == NbtCompound.COMPOUND_TYPE) {
                handleCompound(currentPath, (NbtCompound) element);
            }
            currentPath.pop();
        }
    }

    public static DrinkAddition[] getAdditionsFromStack(ItemStack stack) {
        convertStackFromPlutosCoffee(stack);
        NbtCompound drinkData = stack.getOrCreateSubNbt(AbstractCustomizableDrinkItem.DRINK_DATA_NBT_KEY);
        NbtList additions = drinkData.getList(DrinkAdditions.ADDITIONS_NBT_KEY, NbtElement.STRING_TYPE);
        ArrayList<DrinkAddition> additionsList = new ArrayList<>();
        for (int i = 0; i < additions.size(); i++) {
            String id = additions.getString(i);
            Identifier identifier = new Identifier(id);
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

    public static NbtElement stringAsNbt(String s) {
        NbtCompound compound = new NbtCompound();
        compound.putString("string", s);
        return compound.get("string");
    }

    public static void setPlayerCaffeine(PlayerEntity player, float caffeine) {
        // TODO
    }

    public static float calculateCaffeineDecay(int ticks, float originalCaffeine) {
        double exp = Math.pow(0.5, ticks / CAFFEINE_HALF_LIFE_TICKS);
        return (float) (exp * originalCaffeine);
    }

    public static float getPlayerCaffeine(PlayerEntity player) {
        return 0; // TODO
    }

    public interface Converter<T extends NbtElement> {
        T convert(T startingElement);
    }

}
