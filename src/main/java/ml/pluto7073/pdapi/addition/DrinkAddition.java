package ml.pluto7073.pdapi.addition;

import com.google.gson.JsonObject;
import ml.pluto7073.pdapi.PDAPI;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class DrinkAddition {

    private final OnDrink[] actions;
    private final boolean changesColor;
    private final int color;
    private final int caffeine;
    private final int maxAmount;
    private final int currentWeight;
    private final JsonObject originalData;

    public DrinkAddition(OnDrink[] actions, boolean changesColor, int color, int caffeine, int maxAmount, JsonObject originalData) {
        this(actions, changesColor, color, caffeine, maxAmount, originalData, 0);
    }

    protected DrinkAddition(OnDrink[] actions, boolean changesColor, int color, int caffeine, int maxAmount, JsonObject originalData, int currentWeight) {
        this.actions = actions;
        this.changesColor = changesColor;
        this.color = color;
        this.caffeine = caffeine;
        this.maxAmount = maxAmount;
        this.originalData = originalData;
        this.currentWeight = currentWeight;
    }

    public void onDrink(ItemStack stack, World world, LivingEntity user) {
        for (OnDrink action : actions) {
            action.onDrink(stack, world, user);
        }
    }

    public boolean changesColor() {
        return changesColor;
    }

    public int getColor() {
        return color;
    }

    public int getCaffeine() {
        return caffeine;
    }

    public int getMaxAmount() {
        return maxAmount;
    }

    public int getCurrentWeight() {
        return currentWeight;
    }

    public JsonObject asJsonObject() {
        return originalData;
    }

    public String getTranslationKey() {
        try {
            Identifier id = DrinkAdditions.getId(this);
            return "drink_addition." + id.getNamespace() + "." + id.getPath();
        } catch (IllegalArgumentException e) {
            PDAPI.LOGGER.error("Couldn't get translation key for a drink addition", e);
            return "drink_addition.pdapi.empty";
        }
    }

    public static class Builder {

        private final List<OnDrink> actions;
        private boolean changesColor;
        private int color;
        private int caffeine;
        private int maxAmount;
        private int weight;

        public Builder() {
            actions = new ArrayList<>();
            changesColor = false;
            color = 0;
            caffeine = 0;
            maxAmount = 0;
            weight = 0;
        }

        public Builder addAction(OnDrink action) {
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

        public Builder caffeine(int caffeine) {
            this.caffeine = caffeine;
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

        public DrinkAddition build(JsonObject data) {
            return new DrinkAddition(actions.toArray(new OnDrink[0]), changesColor, color, caffeine, maxAmount, data, weight);
        }

    }

}
