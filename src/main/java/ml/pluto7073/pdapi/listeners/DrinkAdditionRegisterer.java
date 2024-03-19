package ml.pluto7073.pdapi.listeners;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import ml.pluto7073.pdapi.PDAPI;
import ml.pluto7073.pdapi.addition.DrinkAddition;
import ml.pluto7073.pdapi.addition.DrinkAdditions;
import ml.pluto7073.pdapi.addition.OnDrink;
import ml.pluto7073.pdapi.addition.OnDrinkTemplate;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

public class DrinkAdditionRegisterer implements SimpleSynchronousResourceReloadListener {

    public static final Identifier PHASE = PDAPI.asId("phase/additions");

    public DrinkAdditionRegisterer() {
        ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register(PHASE, (player, joined) -> DrinkAdditions.send(player));
    }

    @Override
    public Identifier getFabricId() {
        return PDAPI.asId("drink_addition_registerer");
    }

    @Override
    public void reload(ResourceManager manager) {
        DrinkAdditions.resetRegistry();

        int i = 0;

        for (Map.Entry<Identifier, Resource> entry : manager.findResources("drink_additions", id -> id.getPath().endsWith(".json")).entrySet()) {
            Identifier id = new Identifier(entry.getKey().getNamespace(),
                    entry.getKey().getPath()
                            .replace("drink_additions/", "")
                            .replace(".json", ""));
            try (InputStream stream = entry.getValue().getInputStream()) {
                JsonObject object = JsonHelper.deserialize(new InputStreamReader(stream));

                DrinkAdditions.register(id, loadFromJson(id, object));
                i++;
            } catch (IOException e) {
                PDAPI.LOGGER.error("Could not load Drink Addition " + id, e);
            }
        }

        PDAPI.LOGGER.info("Loaded {} additions", i);
    }

    public static DrinkAddition loadFromJson(Identifier id, JsonObject object) {
        DrinkAddition.Builder daBuilder = new DrinkAddition.Builder();
        if (object.has("caffeine")) {
            daBuilder.caffeine(JsonHelper.getInt(object, "caffeine"));
        }
        if (object.has("changesColor")) {
            daBuilder.changesColor(JsonHelper.getBoolean(object, "changesColor"));
        }
        if (object.has("color")) {
            daBuilder.color(JsonHelper.getInt(object, "color"));
        }
        if (object.has("maxAmount")) {
            daBuilder.maxAmount(JsonHelper.getInt(object, "maxAmount"));
        }
        if (object.has("onDrinkActions")) {
            JsonArray actionsArray = JsonHelper.getArray(object, "onDrinkActions");
            for (JsonElement e : actionsArray) {
                if (!e.isJsonObject()) {
                    PDAPI.LOGGER.warn("Non-JsonObject item in 'onDrinkActions' in Drink Action file: " + id);
                    continue;
                }
                JsonObject actionObject = e.getAsJsonObject();
                OnDrinkTemplate template = OnDrinkTemplate.get(new Identifier(JsonHelper.getString(actionObject, "type")));
                OnDrink action = template.parseJson(id, actionObject);
                daBuilder.addAction(action);
            }
        }

        return daBuilder.build(object);
    }

}
