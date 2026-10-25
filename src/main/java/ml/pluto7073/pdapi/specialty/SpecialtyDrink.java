package ml.pluto7073.pdapi.specialty;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ml.pluto7073.chemicals.Chemicals;
import ml.pluto7073.pdapi.addition.DrinkAddition;
import ml.pluto7073.pdapi.component.DrinkAdditions;
import ml.pluto7073.pdapi.component.PDComponents;
import ml.pluto7073.pdapi.addition.action.NoSipAction;
import ml.pluto7073.pdapi.addition.action.OnDrinkSerializer;
import ml.pluto7073.pdapi.addition.action.OnDrinkSerializers;
import ml.pluto7073.pdapi.PDRegistries;
import ml.pluto7073.pdapi.addition.action.OnDrinkAction;
import ml.pluto7073.pdapi.item.PDItems;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@MethodsReturnNonnullByDefault
public class SpecialtyDrink {

    public static final Codec<SpecialtyDrink> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(SpecialtyDrinkBase.CODEC.fieldOf("base").forGetter(SpecialtyDrink::base),
                    Codec.list(ResourceLocation.CODEC).fieldOf("additions").forGetter(SpecialtyDrink::steps),
                    Codec.list(OnDrinkAction.CODEC).fieldOf("onDrinkActions").forGetter(drink -> List.of(drink.actions)),
                    Codec.DOUBLE.fieldOf("volume").orElse(0.0).forGetter(SpecialtyDrink::volume),
                    Codec.INT.fieldOf("color").orElse(-1).forGetter(SpecialtyDrink::color),
                    Codec.simpleMap(ResourceLocation.CODEC, Codec.FLOAT, Chemicals.CHEMICAL_HANDLER)
                            .fieldOf("chemicals").orElse(Map.of()).forGetter(SpecialtyDrink::chemicals),
                    Codec.STRING.fieldOf("name").orElse("").forGetter(drink -> drink.name))
            .apply(instance, SpecialtyDrink::new));

    public static final Holder<SpecialtyDrink> EMPTY = Holder.direct(new SpecialtyDrink(
            new SpecialtyDrink.ItemBase(Items.AIR),
            List.of(), List.of(), 0, 0xfc0ffc, Map.of(), "Drink"
    ));

    public static final Codec<Holder<SpecialtyDrink>> COMPONENT_CODEC = RegistryFixedCodec.create(PDRegistries.SPECIALITY_DRINK_KEY);
    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<SpecialtyDrink>> STREAM_COMPONENT_CODEC = ByteBufCodecs.holderRegistry(PDRegistries.SPECIALITY_DRINK_KEY);

    private final SpecialtyDrinkBase base;
    private final ResourceLocation[] steps;
    private final OnDrinkAction[] actions;
    private final double volume;
    private final int color;
    private final Map<ResourceLocation, Float> chemicals;
    private final String name;

    public SpecialtyDrink(SpecialtyDrinkBase base, List<ResourceLocation> steps, List<OnDrinkAction> actions, double volume, int color, Map<ResourceLocation, Float> chemicals, @Nullable String name) {
        this.base = base;
        this.steps = steps.toArray(ResourceLocation[]::new);
        this.actions = actions.toArray(OnDrinkAction[]::new);
        this.volume = volume;
        this.color = color;
        this.chemicals = chemicals;
        this.name = name == null ? "" : name;
    }

    public SpecialtyDrinkBase base() {
        return base;
    }

    public List<ResourceLocation> steps() {
        return List.of(steps);
    }

    public List<OnDrinkAction> actions(HolderLookup.Provider provider) {
        ArrayList<OnDrinkAction> stepActions = new ArrayList<>();
        for (ResourceLocation step : steps) {
            var addition = provider.lookupOrThrow(PDRegistries.DRINK_ADDITION_KEY)
                    .get(ResourceKey.create(PDRegistries.DRINK_ADDITION_KEY, step));
            if (addition.isEmpty()) continue;
            stepActions.addAll(addition.get().value().actions());
        }
        stepActions.addAll(List.of(actions));
        return ImmutableList.copyOf(stepActions);
    }

    public boolean noSip() {
        return Arrays.stream(actions).anyMatch(action -> action.serializer() == OnDrinkSerializers.NO_SIP);
    }

    public double volume() {
        return volume;
    }

    public int color() {
        return color;
    }

    public Map<ResourceLocation, Float> chemicals() {
        return chemicals;
    }

    public Optional<String> name() {
        return name.isEmpty() ? Optional.empty() : Optional.of(name);
    }

    public ItemStack getBaseItem(ItemStack source, HolderLookup.Provider provider) {
        ItemStack stack = base.buildItemStack(provider);
        DrinkAdditions baseAdditions = source.getOrDefault(PDComponents.ADDITIONS, DrinkAdditions.EMPTY);
        var registry = provider.lookupOrThrow(PDRegistries.DRINK_ADDITION_KEY);
        List<Holder<DrinkAddition>> additions = Arrays.stream(steps)
                .map(id -> (Holder<DrinkAddition>) registry.getOrThrow(ResourceKey.create(PDRegistries.DRINK_ADDITION_KEY, id)))
                .toList();
        stack.set(PDComponents.ADDITIONS, DrinkAdditions.or(new DrinkAdditions(additions), baseAdditions));
        return stack;
    }

    public boolean matches(Container container) {
        ItemStack currentResult = container.getItem(0);
        if (!base.matches(currentResult)) return false;
        List<Holder<DrinkAddition>> additions = currentResult.getOrDefault(PDComponents.ADDITIONS, DrinkAdditions.EMPTY).additions();
        if (steps.length != additions.size()) return false;
        for (int i = 0; i < additions.size(); i++) {
            ResourceLocation actual = additions.get(i).unwrapKey().orElseThrow().location();
            ResourceLocation wanted = steps[i];
            if (!actual.equals(wanted)) return false;
        }
        return true;
    }

    public static ItemStack getAsItem(Holder<SpecialtyDrink> drink) {
        ItemStack stack = new ItemStack(PDItems.SPECIALTY_DRINK, 1);
        stack.set(PDComponents.SPECIALTY_DRINK, drink);
        return stack;
    }

    public static String languageKey(Holder<SpecialtyDrink> drink) {
        if (drink.value().name.isEmpty()) {
            return drink.unwrapKey().orElseThrow().location().toLanguageKey("drink");
        }
        return drink.value().name;
    }

    public static class ItemBase implements SpecialtyDrinkBase {

        private final Item item;

        public ItemBase(Item item) {
            this.item = item;
        }

        @Override
        public ItemStack buildItemStack(HolderLookup.Provider provider) {
            return new ItemStack(item);
        }

        @Override
        public boolean matches(ItemStack stack) {
            return stack.is(item);
        }

        @Override
        public SpecialtyDrinkBaseSerializer serializer() {
            return SpecialtyDrinkBaseSerializer.ITEM_BASE;
        }

    }

    public static class ItemBaseSerializer implements SpecialtyDrinkBaseSerializer {

        public static final MapCodec<ItemBase> CODEC = RecordCodecBuilder.mapCodec(instance ->
                instance.group(BuiltInRegistries.ITEM.byNameCodec().fieldOf("item").forGetter(base -> base.item))
                        .apply(instance, ItemBase::new));

        @Override
        public MapCodec<? extends SpecialtyDrinkBase> codec() {
            return CODEC;
        }

    }

}
