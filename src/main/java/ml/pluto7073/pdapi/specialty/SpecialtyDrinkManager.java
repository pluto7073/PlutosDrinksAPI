package ml.pluto7073.pdapi.specialty;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.serialization.JsonOps;
import ml.pluto7073.pdapi.PDAPI;
import ml.pluto7073.pdapi.item.AbstractCustomizableDrinkItem;
import ml.pluto7073.pdapi.networking.packet.clientbound.ClientboundSyncSpecialtyDrinkRegistryPacket;
import ml.pluto7073.pdapi.util.DrinkUtil;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class SpecialtyDrinkManager implements SimpleSynchronousResourceReloadListener {

    public static final SpecialtyDrink EMPTY = new SpecialtyDrink(
            new SpecialtyDrink.ItemBase(Items.AIR),
            List.of(), List.of(), 0, 0xfc0ffc, Map.of(), "Drink"
    );

    public static final ResourceLocation PHASE = PDAPI.asId("phase/specialty_drinks");

    private final HashMap<ResourceLocation, SpecialtyDrink> registry = new HashMap<>();

    public SpecialtyDrinkManager() {
        ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register(PHASE, (player, joined) ->
                ServerPlayNetworking.send(player, new ClientboundSyncSpecialtyDrinkRegistryPacket(registry)));
    }

    @Override
    public ResourceLocation getFabricId() {
        return PDAPI.asId("specialty_drinks");
    }

    @Override
    public void onResourceManagerReload(ResourceManager manager) {
        registry.clear();

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

                SpecialtyDrink drink = SpecialtyDrink.CODEC.parse(JsonOps.INSTANCE, object).getOrThrow(false, s -> {
                    throw new JsonParseException(s);
                });

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

    @Override
    public ArrayList<ResourceLocation> getFabricDependencies() {
        return new ArrayList<>();
    }

    public void register(ResourceLocation id, SpecialtyDrink drink) {
        registry.put(id, drink);
    }

    public SpecialtyDrink get(ResourceLocation id) {
        return registry.get(id);
    }

    public ResourceLocation getId(SpecialtyDrink drink) {
        for (ResourceLocation id : registry.keySet()) {
            if (drink.equals(registry.get(id))) return id;
        }
        return new ResourceLocation("empty");
    }

    public Collection<SpecialtyDrink> values() {
        return registry.values();
    }

    public void reset() {
        registry.clear();
    }

}
