package ml.pluto7073.pdapi.specialty;

import com.google.gson.JsonObject;
import ml.pluto7073.pdapi.PDAPI;
import ml.pluto7073.pdapi.PDRegistries;
import ml.pluto7073.pdapi.networking.packet.clientbound.ClientboundSyncSpecialtyDrinkRegistryPacket;
import ml.pluto7073.pdapi.util.DrinkUtil;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public class SpecialtyDrinkManager implements SimpleSynchronousResourceReloadListener {

    private static final HashMap<ResourceLocation, SpecialtyDrink> DRINKS = new HashMap<>();

    public static final ResourceLocation PHASE = PDAPI.asId("phase/specialty_drinks");

    public SpecialtyDrinkManager() {
        ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register(PHASE, (player, joined) ->
                ServerPlayNetworking.send(player, new ClientboundSyncSpecialtyDrinkRegistryPacket(DRINKS)));
    }

    @Override
    public ResourceLocation getFabricId() {
        return PDAPI.asId("specialty_drinks");
    }

    @Override
    public void onResourceManagerReload(ResourceManager manager) {
        DRINKS.clear();

        for (Map.Entry<ResourceLocation, Resource> entry :
                manager.listResources("specialty_drinks", id -> id.getPath().endsWith(".json")).entrySet()) {
            ResourceLocation id = DrinkUtil.getAsId(entry.getKey(), "specialty_drinks");
            try (InputStream stream = entry.getValue().open()) {
                JsonObject object = GsonHelper.parse(new InputStreamReader(stream));

                if (object.has("fabric:load_conditions")) {
                    boolean b = ResourceConditions.conditionsMatch(
                            GsonHelper.getAsJsonArray(object, "fabric:load_conditions"),
                            true
                    );

                    if (!b) continue;
                }
                ResourceLocation type;

                if (object.has("type")) {
                    type = new ResourceLocation(GsonHelper.getAsString(object, "type"));
                } else type = PDAPI.asId("specialty_drink");

                SpecialtyDrinkSerializer serializer = PDRegistries.SPECIALTY_DRINK_SERIALIZER.getOptional(type)
                        .orElseThrow(() -> new IllegalArgumentException("No such Specialty Drink serializer: " + type));

                SpecialtyDrink drink = serializer.fromJson(id, object);

                DRINKS.put(id, drink);
            } catch (IOException e) {
                PDAPI.LOGGER.error("Couldn't load Specialty Drink {}", id, e);
            }
        }

        PDAPI.LOGGER.info("Loaded {} Specialty Drinks", DRINKS.size());
    }

    @Override
    public ArrayList<ResourceLocation> getFabricDependencies() {
        return new ArrayList<>();
    }

    public static void register(ResourceLocation id, SpecialtyDrink drink) {
        DRINKS.put(id, drink);
    }

    public static SpecialtyDrink get(ResourceLocation id) {
        return DRINKS.get(id);
    }

    public static ResourceLocation getId(SpecialtyDrink drink) {
        for (ResourceLocation id : DRINKS.keySet()) {
            if (drink.equals(DRINKS.get(id))) return id;
        }
        return new ResourceLocation("empty");
    }

    public static Collection<SpecialtyDrink> values() {
        return DRINKS.values();
    }

    public static void reset() {
        DRINKS.clear();
    }

}
