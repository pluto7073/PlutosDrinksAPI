package ml.pluto7073.pdapi.addition;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ml.pluto7073.chemicals.Chemicals;
import ml.pluto7073.pdapi.PDRegistries;
import ml.pluto7073.pdapi.addition.action.OnDrinkAction;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiConsumer;

public record DrinkAddition(List<OnDrinkAction> actions, double volume, boolean changesColor, int color,
                            Map<ResourceLocation, Float> chemicals, int maxAmount, int currentWeight, String name) {

    public static final Codec<DrinkAddition> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(Codec.list(OnDrinkAction.CODEC).fieldOf("onDrinkActions").orElse(List.of()).forGetter(DrinkAddition::actions),
                            Codec.DOUBLE.fieldOf("volume").orElse(0.0).forGetter(DrinkAddition::volume),
                            Codec.BOOL.fieldOf("changesColor").orElse(false).forGetter(DrinkAddition::changesColor),
                            Codec.INT.fieldOf("color").orElse(0).forGetter(DrinkAddition::color),
                            Codec.simpleMap(ResourceLocation.CODEC, Codec.FLOAT, Chemicals.CHEMICAL_HANDLER).fieldOf("chemicals").orElse(Map.of())
                                    .forGetter(DrinkAddition::chemicals),
                            Codec.INT.fieldOf("maxAmount").orElse(0).forGetter(DrinkAddition::maxAmount),
                            Codec.STRING.fieldOf("name").orElse("").forGetter(addition -> addition.name),
                            Codec.INT.fieldOf("weight").orElse(0).forGetter(DrinkAddition::currentWeight))
                    .apply(instance, DrinkAddition::new));

    public static final Codec<Holder<DrinkAddition>> COMPONENT_CODEC = RegistryFixedCodec.create(PDRegistries.DRINK_ADDITION_KEY);
    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<DrinkAddition>> STREAM_COMPONENT_CODEC = ByteBufCodecs.holderRegistry(PDRegistries.DRINK_ADDITION_KEY);

    private DrinkAddition(List<OnDrinkAction> actions, double volume, boolean changesColor, int color, Map<ResourceLocation, Float> chemicals, int maxAmount, @Nullable String name, int currentWeight) {
        this(actions, volume, changesColor, color, chemicals, maxAmount, currentWeight, name);
    }

    public void onDrink(ItemStack stack, Level level, LivingEntity user) {
        for (OnDrinkAction action : actions) {
            action.onDrink(stack, level, user);
        }
    }

    @SuppressWarnings("UnusedReturnValue")
    public static class Builder {

        private final List<OnDrinkAction> actions;
        private double volume;
        private boolean changesColor;
        private int color;
        private final HashMap<ResourceLocation, Float> chemicals;
        private int maxAmount;
        private int weight;
        private String name;

        public Builder() {
            actions = new ArrayList<>();
            volume = 0.0;
            changesColor = false;
            color = 0;
            chemicals = new HashMap<>();
            maxAmount = 0;
            weight = 0;
            name = "";
        }

        public Builder addAction(OnDrinkAction action) {
            actions.add(action);
            return this;
        }

        public Builder volume(double volume) {
            this.volume = volume;
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

        public Builder chemical(ResourceLocation name, float amount) {
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
            return new DrinkAddition(ImmutableList.copyOf(actions), volume, changesColor, color, chemicals, maxAmount, name, weight);
        }

        public void save(ResourceLocation id, BiConsumer<ResourceLocation, DrinkAddition> output) {
            output.accept(id, build());
        }

    }

}
