package ml.pluto7073.pdapi.specialty;

import net.minecraft.world.item.Item;

import java.util.HashMap;

public class InProgressItemRegistry {

    private static final HashMap<Item, Item> REGISTRY = new HashMap<>();

    public static boolean isInProgressItem(Item item) {
        return REGISTRY.containsValue(item);
    }

    public static Item getBase(Item inProgress) {
        for (Item key : REGISTRY.keySet()) {
            Item val = REGISTRY.get(key);
            if (val == inProgress) return key;
        }
        throw new IllegalArgumentException("Item is not an in progress item");
    }

    public static void register(Item base, Item inProgress) {
        REGISTRY.put(base, inProgress);
    }

    public static Item getInProgress(Item base) {
        return REGISTRY.get(base);
    }

}
