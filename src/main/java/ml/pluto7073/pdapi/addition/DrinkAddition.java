package ml.pluto7073.pdapi.addition;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Keyable;
import com.mojang.serialization.codecs.RecordCodecBuilder;
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

public class DrinkAddition {

    public static final Codec<DrinkAddition> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(Codec.list(OnDrinkAction.CODEC).fieldOf("onDrinkActions").orElse(new ArrayList<>()).forGetter(DrinkAddition::actions),
                            Codec.BOOL.fieldOf("changesColor").orElse(false).forGetter(DrinkAddition::changesColor),
                            Codec.INT.fieldOf("color").orElse(0).forGetter(DrinkAddition::color),
                            Codec.simpleMap(Codec.STRING, Codec.INT, Keyable.forStrings(() -> ConsumableChemicalRegistry.ids().stream()))
                                    .fieldOf("chemicals").orElse(new HashMap<>()).forGetter(DrinkAddition::chemicals),
                            Codec.INT.fieldOf("maxAmount").orElse(0).forGetter(DrinkAddition::maxAmount),
                            Codec.STRING.fieldOf("name").orElse("").forGetter(addition -> addition.name),
                            Codec.INT.fieldOf("weight").orElse(0).forGetter(DrinkAddition::currentWeight))
                    .apply(instance, DrinkAddition::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, DrinkAddition> STREAM_CODEC =
            StreamCodec.of((buf, add) -> add.toNetwork(buf), DrinkAddition::fromNetwork);

    public static final Codec<DrinkAddition> COMPONENT_CODEC =
            ResourceLocation.CODEC.xmap(DrinkAdditionManager::get, DrinkAdditionManager::getId);

    private final OnDrinkAction[] actions;
    private final boolean changesColor;
    private final int color;
    private final Map<String, Integer> chemicals;
    private final int maxAmount;
    private final int currentWeight;
    private final String name;

    protected DrinkAddition(List<OnDrinkAction> actions, boolean changesColor, int color, Map<String, Integer> chemicals, int maxAmount, @Nullable String name, int currentWeight) {
        this.actions = actions.toArray(OnDrinkAction[]::new);
        this.changesColor = changesColor;
        this.color = color;
        this.chemicals = new HashMap<>(chemicals);
        ConsumableChemicalRegistry.fillChemicalMap(this.chemicals);
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

    public int color() {
        return color;
    }

    public Map<String, Integer> chemicals() {
        return chemicals;
    }

    public int maxAmount() {
        return maxAmount;
    }

    public int currentWeight() {
        return currentWeight;
    }

    public List<OnDrinkAction> actions() {
        return Lists.newArrayList(actions);
    }

    public void toNetwork(RegistryFriendlyByteBuf buf) {
        OnDrinkAction.STREAM_CODEC.apply(ByteBufCodecs.list()).encode(buf, actions());
        buf.writeBoolean(changesColor);
        buf.writeInt(color);
        buf.writeMap(chemicals, FriendlyByteBuf::writeUtf, FriendlyByteBuf::writeInt);
        buf.writeInt(maxAmount);
        buf.writeInt(currentWeight);
        buf.writeUtf(Objects.requireNonNullElse(name, ""));
    }

    public static DrinkAddition fromNetwork(RegistryFriendlyByteBuf buf) {
        List<OnDrinkAction> actions = OnDrinkAction.STREAM_CODEC.apply(ByteBufCodecs.list()).decode(buf);
        boolean changesColor = buf.readBoolean();
        int color = buf.readInt();
        Map<String, Integer> chemicals = buf.readMap(FriendlyByteBuf::readUtf, FriendlyByteBuf::readInt);
        int maxAmount = buf.readInt();
        int currentWeight = buf.readInt();
        String name = buf.readUtf();
        if (name.isEmpty()) name = null;
        return new DrinkAddition(actions, changesColor, color, chemicals, maxAmount, name, currentWeight);
    }

    public String getTranslationKey() {
        if (name != null && !name.isEmpty()) return name;
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
            return new DrinkAddition(actions, changesColor, color, chemicals, maxAmount, name, weight);
        }

    }

}
