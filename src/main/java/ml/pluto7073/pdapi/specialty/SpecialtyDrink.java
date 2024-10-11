package ml.pluto7073.pdapi.specialty;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Keyable;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ml.pluto7073.pdapi.component.DrinkAdditions;
import ml.pluto7073.pdapi.component.PDComponents;
import ml.pluto7073.pdapi.util.DrinkUtil;
import ml.pluto7073.pdapi.PDAPI;
import ml.pluto7073.pdapi.PDRegistries;
import ml.pluto7073.pdapi.addition.DrinkAdditionManager;
import ml.pluto7073.pdapi.addition.action.OnDrinkAction;
import ml.pluto7073.pdapi.addition.action.OnDrinkSerializer;
import ml.pluto7073.pdapi.addition.chemicals.ConsumableChemicalRegistry;
import ml.pluto7073.pdapi.item.AbstractCustomizableDrinkItem;
import ml.pluto7073.pdapi.item.PDItems;
import ml.pluto7073.pdapi.networking.NetworkingUtils;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.Util;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@MethodsReturnNonnullByDefault
public class SpecialtyDrink {

    public static SpecialtyDrink EMPTY = new SpecialtyDrink(Items.AIR, new ArrayList<>(), new ArrayList<>(), 0xf918c5, new HashMap<>(), null);

    public static final Codec<SpecialtyDrink> CODEC =
            PDRegistries.SPECIALTY_DRINK_SERIALIZER.byNameCodec().dispatch(SpecialtyDrink::serializer, SpecialtyDrinkSerializer::codec);

    public static final Codec<SpecialtyDrink> COMPONENT_CODEC =
            ResourceLocation.CODEC.xmap(SpecialtyDrinkManager::get, SpecialtyDrinkManager::getId);

    public static final StreamCodec<RegistryFriendlyByteBuf, SpecialtyDrink> STREAM_CODEC =
            ByteBufCodecs.registry(PDRegistries.SPECIALITY_DRINK_SERIALIZER_KEY).dispatch(SpecialtyDrink::serializer, SpecialtyDrinkSerializer::streamCodec);

    public static final StreamCodec<RegistryFriendlyByteBuf, SpecialtyDrink> STREAM_COMPONENT_CODEC =
            StreamCodec.of(ByteBufCodecs.fromCodecWithRegistries(COMPONENT_CODEC), ByteBufCodecs.fromCodecWithRegistries(COMPONENT_CODEC));

    private final Item base;
    private final ResourceLocation[] steps;
    private final OnDrinkAction[] actions;
    private final int color;
    private final Map<String, Integer> chemicals;
    private final String name;

    public SpecialtyDrink(Item base, List<ResourceLocation> steps, List<OnDrinkAction> actions, int color, Map<String, Integer> chemicals, @Nullable String name) {
        this.base = base;
        this.steps = steps.toArray(ResourceLocation[]::new);
        this.actions = actions.toArray(OnDrinkAction[]::new);
        this.color = color;
        this.chemicals = new HashMap<>(chemicals);
        ConsumableChemicalRegistry.fillChemicalMap(this.chemicals);
        this.name = name;
    }

    public String languageKey() {
        return id().toLanguageKey("drink");
    }

    public ResourceLocation id() {
        return SpecialtyDrinkManager.getId(this);
    }

    public Item base() {
        return base;
    }

    public List<ResourceLocation> steps() {
        return List.of(steps);
    }

    public List<OnDrinkAction> actions() {
        return List.of(actions);
    }

    public int color() {
        return color;
    }

    public Map<String, Integer> chemicals() {
        return chemicals;
    }

    public String name() {
        return name == null || name.isEmpty() ? languageKey() : name;
    }

    public ResourceLocation type() {
        return PDAPI.asId("specialty_drink");
    }

    public SpecialtyDrinkSerializer serializer() {
        return SpecialtyDrinkSerializer.DEFAULT_SERIALIZER;
    }

    public ItemStack getAsItem() {
        return DrinkUtil.setSpecialDrink(new ItemStack(PDItems.SPECIALTY_DRINK, 1), this);
    }

    public ItemStack getAsOriginalItemWithAdditions(ItemStack source) {
        ItemStack stack = new ItemStack(base);
        stack.set(PDComponents.ADDITIONS, DrinkAdditions.or(DrinkAdditions.of(steps()), source.getOrDefault(PDComponents.ADDITIONS, DrinkAdditions.EMPTY)));
        return stack;
    }

    public boolean matches(Container container) {
        ItemStack currentResult = container.getItem(0);
        if (!currentResult.is(base)) return false;
        List<ResourceLocation> additions = currentResult.getOrDefault(PDComponents.ADDITIONS, DrinkAdditions.EMPTY)
                .additions().stream().map(DrinkAdditionManager::getId).toList();
        if (steps.length != additions.size()) return false;
        for (int i = 0; i < additions.size(); i++) {
            ResourceLocation actual = additions.get(i);
            ResourceLocation wanted = steps[i];
            if (!actual.equals(wanted)) return false;
        }
        return true;
    }

    public List<Ingredient> stepsToIngredientList() {
        List<Ingredient> ingredients = new ArrayList<>();
        for (ResourceLocation addition : steps) {
            ingredients.add(DrinkUtil.additionToIngredient(addition));
        }
        return ingredients;
    }

    public static class BaseSerializer implements SpecialtyDrinkSerializer {

        public static final MapCodec<SpecialtyDrink> CODEC = RecordCodecBuilder.mapCodec(instance ->
                instance.group(BuiltInRegistries.ITEM.byNameCodec().fieldOf("base").forGetter(SpecialtyDrink::base),
                                Codec.list(ResourceLocation.CODEC).fieldOf("additions").forGetter(SpecialtyDrink::steps),
                                Codec.list(OnDrinkAction.CODEC).fieldOf("onDrinkActions").forGetter(SpecialtyDrink::actions),
                                Codec.INT.fieldOf("color").forGetter(SpecialtyDrink::color),
                                Codec.simpleMap(Codec.STRING, Codec.INT, Keyable.forStrings(() -> ConsumableChemicalRegistry.ids().stream()))
                                        .orElse(new HashMap<>()).fieldOf("chemicals").forGetter(SpecialtyDrink::chemicals),
                                Codec.STRING.fieldOf("name").orElse("").forGetter(SpecialtyDrink::name))
                        .apply(instance, SpecialtyDrink::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, SpecialtyDrink> STREAM_CODEC =
                StreamCodec.of(BaseSerializer::toNetwork, BaseSerializer::fromNetwork);

        @Override
        public MapCodec<SpecialtyDrink> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, SpecialtyDrink> streamCodec() {
            return STREAM_CODEC;
        }

        public static SpecialtyDrink fromNetwork(RegistryFriendlyByteBuf buf) {
            ResourceLocation base = buf.readResourceLocation();
            List<ResourceLocation> steps = NetworkingUtils.listFromNetwork(buf, FriendlyByteBuf::readResourceLocation);
            HashMap<String, Integer> chemicals = Maps.newHashMap(buf.readMap(FriendlyByteBuf::readUtf, FriendlyByteBuf::readInt));
            int color = buf.readInt();
            List<OnDrinkAction> list = OnDrinkAction.STREAM_CODEC.apply(ByteBufCodecs.list()).decode(buf);
            String name = buf.readUtf();
            return new SpecialtyDrink(BuiltInRegistries.ITEM.get(base), steps, list, color, chemicals, name);
        }

        public static void toNetwork(RegistryFriendlyByteBuf buf, SpecialtyDrink drink) {
            buf.writeResourceLocation(BuiltInRegistries.ITEM.getKey(drink.base));
            NetworkingUtils.arrayToNetwork(buf, drink.steps, FriendlyByteBuf::writeResourceLocation);
            buf.writeMap(drink.chemicals, FriendlyByteBuf::writeUtf, FriendlyByteBuf::writeInt);
            buf.writeInt(drink.color);
            OnDrinkAction.STREAM_CODEC.apply(ByteBufCodecs.list()).encode(buf, drink.actions());
            buf.writeUtf(Objects.requireNonNullElse(drink.name, ""));
        }

    }

}
