package ml.pluto7073.pdapi.listeners;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import ml.pluto7073.pdapi.PDAPI;
import ml.pluto7073.pdapi.PDRegistries;
import ml.pluto7073.pdapi.addition.DrinkAddition;
import ml.pluto7073.pdapi.addition.DrinkAdditions;
import ml.pluto7073.pdapi.addition.action.OnDrinkAction;
import ml.pluto7073.pdapi.addition.action.OnDrinkSerializer;
import ml.pluto7073.pdapi.addition.chemicals.ConsumableChemicalRegistry;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

public class DrinkAdditionRegisterer implements SimpleSynchronousResourceReloadListener {

    public static final ResourceLocation PHASE = PDAPI.asId("phase/additions");

    public DrinkAdditionRegisterer() {
        ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register(PHASE, (player, joined) -> DrinkAdditions.send(player));
    }

    @Override
    public ResourceLocation getFabricId() {
        return PDAPI.asId("drink_addition_registerer");
    }

    @Override
    public void onResourceManagerReload(ResourceManager manager) {
        DrinkAdditions.resetRegistry();

        int i = 0;

        for (Map.Entry<ResourceLocation, Resource> entry : manager.listResources("drink_additions", id -> id.getPath().endsWith(".json")).entrySet()) {
            ResourceLocation id = new ResourceLocation(entry.getKey().getNamespace(),
                    entry.getKey().getPath()
                            .replace("drink_additions/", "")
                            .replace(".json", ""));
            try (InputStream stream = entry.getValue().open()) {
                JsonObject object = GsonHelper.parse(new InputStreamReader(stream));

                if (object.has("fabric:load_conditions")) {
                    boolean b = ResourceConditions.conditionsMatch(
                            GsonHelper.getAsJsonArray(object, "fabric:load_conditions"),
                            true
                    );

                    if (!b) continue;
                }

                DrinkAdditions.register(id, loadFromJson(id, object), false);
                i++;
            } catch (IOException e) {
                PDAPI.LOGGER.error("Could not load Drink Addition {}", id, e);
            }
        }

        PDAPI.LOGGER.info("Loaded {} additions", i);
    }

    public static DrinkAddition loadFromJson(ResourceLocation id, JsonObject object) {
        DrinkAddition.Builder builder = new DrinkAddition.Builder();
        ConsumableChemicalRegistry.forEach(handler -> {
            String name = handler.getName();
            if (object.has(name)) {
                builder.chemical(name, GsonHelper.getAsInt(object, name));
            }
        });
        if (object.has("changesColor")) {
            builder.changesColor(GsonHelper.getAsBoolean(object, "changesColor"));
        }
        if (object.has("color")) {
            builder.color(GsonHelper.getAsInt(object, "color"));
        }
        if (object.has("maxAmount")) {
            builder.maxAmount(GsonHelper.getAsInt(object, "maxAmount"));
        }
        if (object.has("onDrinkActions")) {
            JsonArray actionsArray = GsonHelper.getAsJsonArray(object, "onDrinkActions");
            for (JsonElement e : actionsArray) {
                if (!e.isJsonObject()) {
                    PDAPI.LOGGER.warn("Non-JsonObject item in 'onDrinkActions' in Drink Addition file: {}", id);
                    continue;
                }
                JsonObject actionObject = e.getAsJsonObject();
                    ResourceLocation type =
                            new ResourceLocation(GsonHelper.getAsString(actionObject, "type"));
                OnDrinkSerializer<?> template = PDRegistries.ON_DRINK_SERIALIZER.get(type);
                if (template == null) {
                    PDAPI.LOGGER.error("Could not load OnDrinkAction for add-in {} because of non-existent OnDrinkTemplate {}", id.toString(), GsonHelper.getAsString(actionObject, "type"));
                    continue;
                }
                try {
                    OnDrinkAction action = template.fromJson(actionObject);
                    builder.addAction(action);
                } catch (Exception ex) {
                    PDAPI.LOGGER.error("Could not load OnDrinkAction for addition {}", id, new RuntimeException(ex));
                }
            }
        }
        if (object.has("weight")) {
            builder.setWeight(GsonHelper.getAsInt(object, "weight"));
        }
        if (object.has("name")) {
            builder.name(GsonHelper.getAsString(object, "name"));
        }

        return builder.build();
    }

}
