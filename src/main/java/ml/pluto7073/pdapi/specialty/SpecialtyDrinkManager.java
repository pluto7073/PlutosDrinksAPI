package ml.pluto7073.pdapi.specialty;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.serialization.JsonOps;
import ml.pluto7073.pdapi.PDAPI;
import ml.pluto7073.pdapi.PDRegistries;
import ml.pluto7073.pdapi.networking.packet.clientbound.ClientboundSyncSpecialtyDrinkRegistryPacket;
import ml.pluto7073.pdapi.util.DrinkUtil;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceCondition;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions;
import net.minecraft.Util;
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
                ServerPlayNetworking.send(player, new ClientboundSyncSpecialtyDrinkRegistryPacket(DRINKS.entrySet().stream().map(entry ->
                        new SpecialtyDrinkHolder(entry.getKey(), entry.getValue())).toList())));
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
                    boolean b = ResourceCondition.CONDITION_CODEC.parse(JsonOps.INSTANCE, object.get("fabric:load_conditions"))
                            .getOrThrow().test(null);
                    if (!b) continue;
                }

                DRINKS.put(id, SpecialtyDrink.CODEC.parse(JsonOps.INSTANCE, object).getOrThrow());
            } catch (Exception e) {
                PDAPI.LOGGER.error("Couldn't load Specialty Drink {}", id, e);
            }
        }

        PDAPI.LOGGER.info("Loaded {} Specialty Drinks", DRINKS.size());
    }

    @Override
    public ArrayList<ResourceLocation> getFabricDependencies() {
        return new ArrayList<>();
    }

    public static void register(SpecialtyDrinkHolder holder) {
        DRINKS.put(holder.id(), holder.value());
    }

    public static SpecialtyDrink get(ResourceLocation id) {
        return DRINKS.getOrDefault(id, SpecialtyDrink.EMPTY);
    }

    public static ResourceLocation getId(SpecialtyDrink drink) {
        for (ResourceLocation id : DRINKS.keySet()) {
            if (drink.equals(DRINKS.get(id))) return id;
        }
        return ResourceLocation.withDefaultNamespace("empty");
    }

    public static Collection<SpecialtyDrink> values() {
        return DRINKS.values();
    }

    public static void reset() {
        DRINKS.clear();
    }

}
