package ml.pluto7073.pdapi.addition;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.serialization.JsonOps;
import ml.pluto7073.pdapi.PDAPI;
import ml.pluto7073.pdapi.networking.packet.clientbound.ClientboundSyncAdditionRegistryPacket;
import ml.pluto7073.pdapi.util.DrinkUtil;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions;
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

    public static final String ADDITIONS_NBT_KEY = "Additions";
    public static final ResourceLocation PHASE = PDAPI.asId("phase/additions");

    private final Map<ResourceLocation, DrinkAddition> registry = new HashMap<>();

    public DrinkAdditionManager() {
        ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register(PHASE, (player, joined) -> send(player));
    }

    public DrinkAddition register(ResourceLocation id, DrinkAddition addition) {
        if (containsId(id)) {
            if (get(id).getCurrentWeight() >= addition.getCurrentWeight()) return get(id);
        }
        registry.put(id, addition);
        return addition;
    }

    public ResourceLocation getId(DrinkAddition addition) {
        for (Map.Entry<ResourceLocation, DrinkAddition> entry : registry.entrySet()) {
            if (Objects.equals(entry.getValue(), addition)) {
                return entry.getKey();
            }
        }
        throw new IllegalArgumentException("Unregistered drink addition: " + addition.toString());
    }

    public DrinkAddition get(ResourceLocation id) {
        return registry.get(id);
    }

    public void resetRegistry() {
        registry.clear();
    }

    public boolean containsId(ResourceLocation id) {
        return registry.containsKey(id);
    }

    public boolean containsAddition(DrinkAddition addition) {
        return registry.containsValue(addition);
    }

    public boolean contains(ResourceLocation id, DrinkAddition addition) {
        return containsId(id) && containsAddition(addition) && get(id).equals(addition);
    }

    public boolean contains(Map.Entry<ResourceLocation, DrinkAddition> entry) {
        return contains(entry.getKey(), entry.getValue());
    }

    public void send(ServerPlayer entity) {

        ServerPlayNetworking.send(entity, new ClientboundSyncAdditionRegistryPacket(registry));

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

                register(id, DrinkAddition.CODEC.parse(JsonOps.INSTANCE, object).getOrThrow(false, s -> {
                    throw new JsonParseException(s);
                }));
                i++;
            } catch (Exception e) {
                PDAPI.LOGGER.error("Could not load Drink Addition {}", id, e);
            }
        }

        PDAPI.LOGGER.info("Loaded {} additions", i);
    }

    @Override
    public ArrayList<ResourceLocation> getFabricDependencies() {
        return new ArrayList<>();
    }

}
