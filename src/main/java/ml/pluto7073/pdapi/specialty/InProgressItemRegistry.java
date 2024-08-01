package ml.pluto7073.pdapi.specialty;

import net.minecraft.world.item.Item;

import java.util.HashMap;

public class InProgressItemRegistry {

    private static final HashMap<Item, Item> REGISTRY = new HashMap<>();

    public static void register(Item base, Item inProgress) {
        REGISTRY.put(base, inProgress);
    }

    public static Item getInProgress(Item base) {
        return REGISTRY.get(base);
    }

}
