package ml.pluto7073.pdapi.addition;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Keyable;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ml.pluto7073.chemicals.Chemicals;
import ml.pluto7073.pdapi.PDAPI;
import ml.pluto7073.pdapi.addition.action.OnDrinkAction;
import ml.pluto7073.pdapi.addition.chemicals.ConsumableChemicalRegistry;
import ml.pluto7073.pdapi.networking.NetworkingUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiConsumer;

public class DrinkAddition {

    public static final Codec<DrinkAddition> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(Codec.list(OnDrinkAction.CODEC).fieldOf("onDrinkActions").orElse(List.of()).forGetter(DrinkAddition::actions),
                    Codec.DOUBLE.fieldOf("volume").orElse(0.0).forGetter(DrinkAddition::volume),
                    Codec.BOOL.fieldOf("changesColor").orElse(false).forGetter(DrinkAddition::changesColor),
                    Codec.INT.fieldOf("color").orElse(0).forGetter(DrinkAddition::getColor),
                    Codec.simpleMap(ResourceLocation.CODEC, Codec.FLOAT, Chemicals.CHEMICAL_HANDLER).fieldOf("chemicals").orElse(Map.of())
                            .forGetter(DrinkAddition::getChemicals),
                    Codec.INT.fieldOf("maxAmount").orElse(0).forGetter(DrinkAddition::getMaxAmount),
                    Codec.STRING.fieldOf("name").orElse("").forGetter(addition -> addition.name),
                    Codec.INT.fieldOf("weight").orElse(0).forGetter(DrinkAddition::getCurrentWeight))
            .apply(instance, DrinkAddition::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, DrinkAddition> STREAM_CODEC =
            StreamCodec.of((buf, add) -> add.toNetwork(buf), DrinkAddition::fromNetwork);

    public static final Codec<DrinkAddition> COMPONENT_CODEC =
            ResourceLocation.CODEC.xmap(DrinkAdditionManager::get, DrinkAdditionManager::getId);

    private final List<OnDrinkAction> actions;
    private final double volume;
    private final boolean changesColor;
    private final int color;
    private final Map<ResourceLocation, Float> chemicals;
    private final int maxAmount;
    private final int currentWeight;
    private final String name;

    protected DrinkAddition(List<OnDrinkAction> actions, double volume, boolean changesColor, int color, Map<ResourceLocation, Float> chemicals, int maxAmount, @Nullable String name, int currentWeight) {
        this.actions = actions;
        this.volume = volume;
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

    public List<OnDrinkAction> actions() {
        return actions;
    }

    public double volume() {
        return volume;
    }

    public boolean changesColor() {
        return changesColor;
    }

    public int getColor() {
        return color;
    }

    public Map<ResourceLocation, Float> getChemicals() {
        return chemicals;
    }

    public int getMaxAmount() {
        return maxAmount;
    }

    public int getCurrentWeight() {
        return currentWeight;
    }

    public void toNetwork(RegistryFriendlyByteBuf buf) {
        OnDrinkAction.STREAM_CODEC.apply(ByteBufCodecs.list()).encode(buf, actions());
        buf.writeDouble(volume);
        buf.writeBoolean(changesColor);
        buf.writeInt(color);
        buf.writeMap(chemicals, FriendlyByteBuf::writeResourceLocation, FriendlyByteBuf::writeFloat);
        buf.writeInt(maxAmount);
        buf.writeInt(currentWeight);
        buf.writeUtf(Objects.requireNonNullElse(name, ""));
    }

    public static DrinkAddition fromNetwork(RegistryFriendlyByteBuf buf) {
        List<OnDrinkAction> actions = OnDrinkAction.STREAM_CODEC.apply(ByteBufCodecs.list()).decode(buf);
        double volume = buf.readDouble();
        boolean changesColor = buf.readBoolean();
        int color = buf.readInt();
        Map<ResourceLocation, Float> chemicals = buf.readMap(FriendlyByteBuf::readResourceLocation, FriendlyByteBuf::readFloat);
        int maxAmount = buf.readInt();
        int currentWeight = buf.readInt();
        String name = buf.readUtf();
        if (name.isEmpty()) name = null;
        return new DrinkAddition(actions, volume, changesColor, color, chemicals, maxAmount, name, currentWeight);
    }

    public String getTranslationKey(Level level) {
        if (name != null && !name.isEmpty()) return name;
        try {
            ResourceLocation id = level.getDrinkAdditionManager().getId(this);
            return id.toLanguageKey("drink_addition");
        } catch (IllegalArgumentException e) {
            PDAPI.LOGGER.error("Couldn't get translation key for a drink addition", e);
            return "drink_addition.pdapi.empty";
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
