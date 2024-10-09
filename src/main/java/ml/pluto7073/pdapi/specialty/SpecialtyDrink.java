package ml.pluto7073.pdapi.specialty;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Keyable;
import com.mojang.serialization.codecs.RecordCodecBuilder;
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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@MethodsReturnNonnullByDefault
public class SpecialtyDrink {

    public static final Codec<SpecialtyDrink> CODEC =
            PDRegistries.SPECIALTY_DRINK_SERIALIZER.byNameCodec().dispatch(SpecialtyDrink::serializer, SpecialtyDrinkSerializer::codec);

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

    public ItemStack baseAsStack() {
        return new ItemStack(base);
    }

    public ResourceLocation[] steps() {
        return steps;
    }

    public OnDrinkAction[] actions() {
        return actions;
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
        ItemStack stack = baseAsStack();
        CompoundTag ogData = source.getOrCreateTagElement(AbstractCustomizableDrinkItem.DRINK_DATA_NBT_KEY);
        CompoundTag drinkData = ogData.copy();
        ListTag list = new ListTag();
        for (ResourceLocation step : steps) {
            list.add(StringTag.valueOf(step.toString()));
        }
        list.addAll(ogData.getList(DrinkAdditionManager.ADDITIONS_NBT_KEY, Tag.TAG_STRING));
        drinkData.put(DrinkAdditionManager.ADDITIONS_NBT_KEY, list);
        stack.getOrCreateTag().put(AbstractCustomizableDrinkItem.DRINK_DATA_NBT_KEY, drinkData);
        return stack;
    }

    public boolean matches(Container container) {
        ItemStack currentResult = container.getItem(0);
        if (!currentResult.is(base)) return false;
        ListTag additions = currentResult.getOrCreateTagElement(AbstractCustomizableDrinkItem.DRINK_DATA_NBT_KEY)
                .getList(DrinkAdditionManager.ADDITIONS_NBT_KEY, StringTag.TAG_STRING);
        if (steps.length != additions.size()) return false;
        for (int i = 0; i < additions.size(); i++) {
            String actual = additions.getString(i);
            String wanted = steps[i].toString();
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

    public void toNetwork(FriendlyByteBuf buf) {
        buf.writeResourceLocation(BuiltInRegistries.ITEM.getKey(base));
        NetworkingUtils.arrayToNetwork(buf, steps, FriendlyByteBuf::writeResourceLocation);
        buf.writeMap(chemicals, FriendlyByteBuf::writeUtf, FriendlyByteBuf::writeInt);
        buf.writeInt(color);
        NetworkingUtils.writeDrinkActionsList(buf, actions);
        buf.writeUtf(name);
    }

    public static class BaseSerializer implements SpecialtyDrinkSerializer {

        public static final Codec<SpecialtyDrink> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(BuiltInRegistries.ITEM.byNameCodec().fieldOf("base").forGetter(drink -> drink.base),
                                Codec.list(ResourceLocation.CODEC).fieldOf("additions").forGetter(drink -> List.of(drink.steps)),
                                Codec.list(OnDrinkAction.CODEC).fieldOf("onDrinkActions").forGetter(drink -> List.of(drink.actions)),
                                Codec.INT.fieldOf("color").forGetter(drink -> drink.color),
                                Codec.simpleMap(Codec.STRING, Codec.INT, Keyable.forStrings(() -> ConsumableChemicalRegistry.ids().stream()))
                                        .orElse(new HashMap<>()).fieldOf("chemicals").forGetter(drink -> drink.chemicals),
                                Codec.STRING.fieldOf("name").orElse("").forGetter(drink -> drink.name))
                        .apply(instance, SpecialtyDrink::new));

        @Override
        public Codec<SpecialtyDrink> codec() {
            return CODEC;
        }

        @Override
        public SpecialtyDrink fromNetwork(FriendlyByteBuf buf) {
            ResourceLocation base = buf.readResourceLocation();
            List<ResourceLocation> steps = NetworkingUtils.listFromNetwork(buf, FriendlyByteBuf::readResourceLocation);
            HashMap<String, Integer> chemicals = Maps.newHashMap(buf.readMap(FriendlyByteBuf::readUtf, FriendlyByteBuf::readInt));
            int color = buf.readInt();
            List<OnDrinkAction> list = NetworkingUtils.readDrinkActionsList(buf);
            String name = buf.readUtf();
            return new SpecialtyDrink(BuiltInRegistries.ITEM.get(base), steps, list, color, chemicals, name);
        }

        @Override
        public void toNetwork(SpecialtyDrink drink, FriendlyByteBuf buf) {
            drink.toNetwork(buf);
        }
    }

}
