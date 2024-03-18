package ml.pluto7073.pdapi.addition;

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

    public DrinkAddition(OnDrink[] actions, boolean changesColor, int color, int caffeine, int maxAmount) {
        this.actions = actions;
        this.changesColor = changesColor;
        this.color = color;
        this.caffeine = caffeine;
        this.maxAmount = maxAmount;
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

    public String getTranslationKey() {
        Identifier id = DrinkAdditions.getId(this);
        return "drink_addition." + id.getNamespace() + "." + id.getPath();
    }

    public static class Builder {

        private final List<OnDrink> actions;
        private boolean changesColor;
        private int color;
        private int caffeine;
        private int maxAmount;

        public Builder() {
            actions = new ArrayList<>();
            changesColor = false;
            color = 0;
            caffeine = 0;
            maxAmount = 0;
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

        public DrinkAddition build() {
            return new DrinkAddition(actions.toArray(new OnDrink[0]), changesColor, color, caffeine, maxAmount);
        }

    }

}
