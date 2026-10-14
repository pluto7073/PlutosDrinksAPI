package ml.pluto7073.pdapi.specialty;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.serialization.JsonOps;
import ml.pluto7073.pdapi.PDAPI;
import ml.pluto7073.pdapi.item.AbstractCustomizableDrinkItem;
import ml.pluto7073.pdapi.networking.clientbound.SyncSpecialtyDrinksRegistryPayload;
import ml.pluto7073.pdapi.util.DrinkUtil;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceCondition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class SpecialtyDrinkManager implements SimpleSynchronousResourceReloadListener {

    private final HashMap<ResourceLocation, SpecialtyDrink> registry = new HashMap<>();

    @Override
    public ResourceLocation getFabricId() {
        return PDAPI.asId("specialty_drink");
    }

    @Override
    public void onResourceManagerReload(ResourceManager manager) {
        registry.clear();

        for (Map.Entry<ResourceLocation, Resource> entry :
                manager.listResources("specialty_drink", id -> id.getPath().endsWith(".json")).entrySet()) {
            ResourceLocation id = DrinkUtil.getAsId(entry.getKey(), "specialty_drink");
            try (InputStream stream = entry.getValue().open()) {
                JsonObject object = GsonHelper.parse(new InputStreamReader(stream));

                if (object.has("fabric:load_conditions")) {
                    ResourceCondition condition = ResourceCondition.CONDITION_CODEC.parse(JsonOps.INSTANCE, object.get("fabric:load_conditions")).getOrThrow(JsonParseException::new);

                    if (!condition.test(null)) continue;
                }

                SpecialtyDrink drink = SpecialtyDrink.CODEC.parse(JsonOps.INSTANCE, object).getOrThrow(JsonParseException::new);

                Item base = drink.base().buildItemStack().getItem();

                if (!(base instanceof AbstractCustomizableDrinkItem)) {
                    throw new IllegalStateException("Drink base for " + id + " must be an " +
                            "instance of AbstractCustomizableDrinkItem but " + base + "is not");
                }

                registry.put(id, drink);
            } catch (IOException e) {
                PDAPI.LOGGER.error("Couldn't load Specialty Drink {}", id, e);
            }
        }

        PDAPI.LOGGER.info("Loaded {} Specialty Drinks", registry.size());
    }

    public SpecialtyDrink get(ResourceLocation id) {
        return registry.get(id);
    }

    public void register(ResourceLocation id, SpecialtyDrink drink) {
        registry.put(id, drink);
    }

    public ResourceLocation getId(SpecialtyDrink drink) {
        for (ResourceLocation id : registry.keySet()) {
            if (drink.equals(registry.get(id))) return id;
        }
        return ResourceLocation.withDefaultNamespace("empty");
    }

    public Collection<DrinkHolder> values() {
        return registry.entrySet().stream().map(e -> new DrinkHolder(e.getKey(), e.getValue())).collect(Collectors.toUnmodifiableSet());
    }

    public void reset() {
        registry.clear();
    }

    public void initStaticServer() {
        ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register((player, joined) ->
                ServerPlayNetworking.send(player, new SyncSpecialtyDrinksRegistryPayload(registry)));
    }

    public record DrinkHolder(ResourceLocation key, SpecialtyDrink value) {}

}
