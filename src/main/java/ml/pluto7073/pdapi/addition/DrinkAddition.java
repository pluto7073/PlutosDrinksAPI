package ml.pluto7073.pdapi.addition;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import ml.pluto7073.pdapi.PDAPI;
import ml.pluto7073.pdapi.PDRegistries;
import ml.pluto7073.pdapi.addition.action.OnDrinkAction;
import ml.pluto7073.pdapi.addition.action.OnDrinkSerializer;
import ml.pluto7073.pdapi.addition.chemicals.ConsumableChemicalRegistry;
import ml.pluto7073.pdapi.networking.NetworkingUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class DrinkAddition {

    private final OnDrinkAction[] actions;
    private final boolean changesColor;
    private final int color;
    private final Map<String, Integer> chemicals;
    private final int maxAmount;
    private final int currentWeight;
    private final String name;

    protected DrinkAddition(OnDrinkAction[] actions, boolean changesColor, int color, Map<String, Integer> chemicals, int maxAmount, @Nullable String name) {
        this(actions, changesColor, color, chemicals, maxAmount, name, 0);
    }

    protected DrinkAddition(OnDrinkAction[] actions, boolean changesColor, int color, Map<String, Integer> chemicals, int maxAmount, @Nullable String name, int currentWeight) {
        this.actions = actions;
        this.changesColor = changesColor;
        this.color = color;
        this.chemicals = chemicals;
        this.maxAmount = maxAmount;
        this.currentWeight = currentWeight;
        this.name = name;
    }

    public void onDrink(ItemStack stack, Level level, LivingEntity user) {
        for (OnDrinkAction action : actions) {
            action.onDrink(stack, level, user);
        }
    }

    public boolean changesColor() {
        return changesColor;
    }

    public int getColor() {
        return color;
    }

    public Map<String, Integer> getChemicals() {
        return chemicals;
    }

    public int getMaxAmount() {
        return maxAmount;
    }

    public int getCurrentWeight() {
        return currentWeight;
    }

    public void toNetwork(FriendlyByteBuf buf) {
        NetworkingUtils.writeDrinkActionsList(buf, actions);
        buf.writeBoolean(changesColor);
        buf.writeInt(color);
        buf.writeMap(chemicals, FriendlyByteBuf::writeUtf, FriendlyByteBuf::writeInt);
        buf.writeInt(maxAmount);
        buf.writeInt(currentWeight);
        buf.writeUtf(Objects.requireNonNullElse(name, ""));
    }

    public static DrinkAddition fromNetwork(FriendlyByteBuf buf) {
        List<OnDrinkAction> actions = NetworkingUtils.readDrinkActionsList(buf);
        boolean changesColor = buf.readBoolean();
        int color = buf.readInt();
        Map<String, Integer> chemicals = buf.readMap(FriendlyByteBuf::readUtf, FriendlyByteBuf::readInt);
        int maxAmount = buf.readInt();
        int currentWeight = buf.readInt();
        String name = buf.readUtf();
        if (name.isEmpty()) name = null;
        return new DrinkAddition(actions.toArray(OnDrinkAction[]::new), changesColor, color, chemicals, maxAmount, name, currentWeight);
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        if (changesColor) {
            json.addProperty("changesColor", true);
            json.addProperty("color", color);
        }
        if (maxAmount > 0) {
            json.addProperty("maxAmount", maxAmount);
        }
        chemicals.forEach((id, amount) -> {
            if (amount > 0) {
                json.addProperty(id, amount);
            }
        });
        if (currentWeight != 0) {
            json.addProperty("weight", currentWeight);
        }
        if (name != null) {
            json.addProperty("name", name);
        }
        JsonArray actions = new JsonArray();
        for (OnDrinkAction action : this.actions) {
            JsonObject a = new JsonObject();
            @SuppressWarnings("unchecked")
            OnDrinkSerializer<OnDrinkAction> serializer = (OnDrinkSerializer<OnDrinkAction>) action.serializer();
            ResourceLocation id = PDRegistries.ON_DRINK_SERIALIZER.getKey(serializer);
            if (id == null) {
                PDAPI.LOGGER.error("Couldn't serialize OnDrink Action");
                continue;
            }
            a.addProperty("type", id.toString());
            serializer.toJson(a, action);
            actions.add(a);
        }
        json.add("onDrinkActions", actions);
        return json;
    }

    public String getTranslationKey() {
        if (name != null) return name;
        try {
            ResourceLocation id = DrinkAdditionManager.getId(this);
            return id.toLanguageKey("drink_addition");
        } catch (IllegalArgumentException e) {
            PDAPI.LOGGER.error("Couldn't get translation key for a drink addition", e);
            return "drink_addition.pdapi.empty";
        }
    }

    @SuppressWarnings("UnusedReturnValue")
    public static class Builder {

        private final List<OnDrinkAction> actions;
        private boolean changesColor;
        private int color;
        private final HashMap<String, Integer> chemicals;
        private int maxAmount;
        private int weight;
        private String name;

        public Builder() {
            actions = new ArrayList<>();
            changesColor = false;
            color = 0;
            chemicals = new HashMap<>();
            ConsumableChemicalRegistry.forEach(handler -> chemical(handler.getName(), 0));
            maxAmount = 0;
            weight = 0;
            name = null;
        }

        public Builder addAction(OnDrinkAction action) {
            actions.add(action);
            return this;
        }

        public Builder changesColor(boolean changesColor) {
            this.changesColor = changesColor;
            return this;
        }

        public Builder color(int color) {
            this.color = color;
            return this;
        }

        public Builder chemical(String name, int amount) {
            chemicals.put(name, amount);
            return this;
        }

        public Builder maxAmount(int amount) {
            this.maxAmount = amount;
            return this;
        }

        public Builder setWeight(int weight) {
            this.weight = weight;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public DrinkAddition build() {
            return new DrinkAddition(actions.toArray(OnDrinkAction[]::new), changesColor, color, chemicals, maxAmount, name, weight);
        }

    }

}
