package ml.pluto7073.pdapi.addition;

import com.google.gson.JsonObject;
import ml.pluto7073.pdapi.PDAPI;
import ml.pluto7073.pdapi.networking.packet.s2c.SyncAdditionRegistryS2CPacket;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class DrinkAdditions {

    private static final Map<Identifier, DrinkAddition> REGISTRY = new HashMap<>();
    private static final Map<Identifier, DrinkAddition> STATIC_REGISTRY = new HashMap<>();
    public static final String ADDITIONS_NBT_KEY = "Additions";

    public static final DrinkAddition EMPTY = register(PDAPI.asId("empty"), new DrinkAddition(new OnDrink[0], false, 0, 0, 0, new JsonObject()));

    public static DrinkAddition register(Identifier id, DrinkAddition addition) {
        return register(id, addition, true);
    }

    public static DrinkAddition register(Identifier id, DrinkAddition addition, boolean staticAdd) {
        REGISTRY.put(id, addition);
        if (staticAdd) STATIC_REGISTRY.put(id, addition);
        return addition;
    }

    public static Identifier getId(DrinkAddition addition) {
        for (Map.Entry<Identifier, DrinkAddition> entry : REGISTRY.entrySet()) {
            if (addition.equals(entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }

    public static DrinkAddition get(Identifier id) {
        return REGISTRY.get(id);
    }

    public static void resetRegistry() {
        REGISTRY.clear();
        REGISTRY.putAll(STATIC_REGISTRY);
    }

    public static boolean containsId(Identifier id) {
        return REGISTRY.containsKey(id);
    }

    public static boolean containsAddition(DrinkAddition addition) {
        return REGISTRY.containsValue(addition);
    }

    public static boolean contains(Identifier id, DrinkAddition addition) {
        return containsId(id) && containsAddition(addition) && get(id).equals(addition);
    }

    public static boolean contains(Map.Entry<Identifier, DrinkAddition> entry) {
        return contains(entry.getKey(), entry.getValue());
    }

    public static void send(ServerPlayerEntity entity) {

        Map<Identifier, JsonObject> additions = new HashMap<>();
        REGISTRY.forEach((id, add) -> additions.put(id, add.asJsonObject()));

        ServerPlayNetworking.send(entity, new SyncAdditionRegistryS2CPacket(additions));

    }

}
