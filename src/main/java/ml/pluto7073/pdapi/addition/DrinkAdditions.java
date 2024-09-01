package ml.pluto7073.pdapi.addition;

import com.google.gson.JsonObject;
import ml.pluto7073.pdapi.PDAPI;
import ml.pluto7073.pdapi.networking.packet.s2c.SyncAdditionRegistryS2CPacket;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;
import java.util.Map;

public class DrinkAdditions {

    private static final Map<ResourceLocation, DrinkAddition> REGISTRY = new HashMap<>();
    private static final Map<ResourceLocation, DrinkAddition> STATIC_REGISTRY = new HashMap<>();
    public static final String ADDITIONS_NBT_KEY = "Additions";

    public static final DrinkAddition EMPTY = register(PDAPI.asId("empty"), new DrinkAddition.Builder().build());

    public static DrinkAddition register(ResourceLocation id, DrinkAddition addition) {
        return register(id, addition, true);
    }

    public static DrinkAddition register(ResourceLocation id, DrinkAddition addition, boolean staticAdd) {
        if (containsId(id)) {
            if (get(id).getCurrentWeight() >= addition.getCurrentWeight()) return get(id);
        }
        REGISTRY.put(id, addition);
        if (staticAdd) STATIC_REGISTRY.put(id, addition);
        return addition;
    }

    public static ResourceLocation getId(DrinkAddition addition) {
        for (Map.Entry<ResourceLocation, DrinkAddition> entry : REGISTRY.entrySet()) {
            if (addition.equals(entry.getValue())) {
                return entry.getKey();
            }
        }
        throw new IllegalArgumentException("Unregistered drink addition: " + addition.toString());
    }

    public static DrinkAddition get(ResourceLocation id) {
        return REGISTRY.get(id);
    }

    public static void resetRegistry() {
        REGISTRY.clear();
        REGISTRY.putAll(STATIC_REGISTRY);
    }

    public static boolean containsId(ResourceLocation id) {
        return REGISTRY.containsKey(id);
    }

    public static boolean containsAddition(DrinkAddition addition) {
        return REGISTRY.containsValue(addition);
    }

    public static boolean contains(ResourceLocation id, DrinkAddition addition) {
        return containsId(id) && containsAddition(addition) && get(id).equals(addition);
    }

    public static boolean contains(Map.Entry<ResourceLocation, DrinkAddition> entry) {
        return contains(entry.getKey(), entry.getValue());
    }

    public static void send(ServerPlayer entity) {

        ServerPlayNetworking.send(entity, new SyncAdditionRegistryS2CPacket(REGISTRY));

    }

}
