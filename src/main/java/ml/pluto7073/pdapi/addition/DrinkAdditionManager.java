package ml.pluto7073.pdapi.addition;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.serialization.JsonOps;
import ml.pluto7073.pdapi.PDAPI;
import ml.pluto7073.pdapi.PDRegistries;
import ml.pluto7073.pdapi.addition.action.OnDrinkAction;
import ml.pluto7073.pdapi.addition.action.OnDrinkSerializer;
import ml.pluto7073.pdapi.addition.chemicals.ConsumableChemicalRegistry;
import ml.pluto7073.pdapi.networking.packet.clientbound.ClientboundSyncAdditionRegistryPacket;
import ml.pluto7073.pdapi.util.DrinkUtil;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class DrinkAdditionManager implements SimpleSynchronousResourceReloadListener {

    private static final Map<ResourceLocation, DrinkAddition> REGISTRY = new HashMap<>();
    private static final Map<ResourceLocation, DrinkAddition> STATIC_REGISTRY = new HashMap<>();

    public static final String ADDITIONS_NBT_KEY = "Additions";
    public static final DrinkAddition EMPTY = register(PDAPI.asId("empty"), new DrinkAddition.Builder().build());
    public static final ResourceLocation PHASE = PDAPI.asId("phase/additions");

    public DrinkAdditionManager() {
        ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register(PHASE, (player, joined) -> send(player));
    }

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

        ServerPlayNetworking.send(entity, new ClientboundSyncAdditionRegistryPacket(REGISTRY));

    }

    @Override
    public ResourceLocation getFabricId() {
        return PDAPI.asId("drink_addition_registerer");
    }

    @Override
    public void onResourceManagerReload(ResourceManager manager) {
        resetRegistry();

        int i = 0;

        for (Map.Entry<ResourceLocation, Resource> entry : manager.listResources("drink_additions", id -> id.getPath().endsWith(".json")).entrySet()) {
            ResourceLocation id = DrinkUtil.getAsId(entry.getKey(), "drink_additions");
            try (InputStream stream = entry.getValue().open()) {
                JsonObject object = GsonHelper.parse(new InputStreamReader(stream));

                if (object.has("fabric:load_conditions")) {
                    boolean b = ResourceConditions.conditionsMatch(
                            GsonHelper.getAsJsonArray(object, "fabric:load_conditions"),
                            true
                    );

                    if (!b) continue;
                }

                register(id, loadFromJson(id, object), false);
                i++;
            } catch (IOException e) {
                PDAPI.LOGGER.error("Could not load Drink Addition {}", id, e);
            }
        }

        PDAPI.LOGGER.info("Loaded {} additions", i);
    }

    @Override
    public ArrayList<ResourceLocation> getFabricDependencies() {
        return new ArrayList<>();
    }

    public static DrinkAddition loadFromJson(ResourceLocation id, JsonObject object) {
        return Util.getOrThrow(DrinkAddition.CODEC.parse(JsonOps.INSTANCE, object), JsonParseException::new);
    }

}
