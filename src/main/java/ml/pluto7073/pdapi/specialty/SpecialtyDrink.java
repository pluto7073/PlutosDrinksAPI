package ml.pluto7073.pdapi.specialty;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ml.pluto7073.chemicals.Chemicals;
import ml.pluto7073.pdapi.util.DrinkUtil;
import ml.pluto7073.pdapi.PDAPI;
import ml.pluto7073.pdapi.PDRegistries;
import ml.pluto7073.pdapi.addition.DrinkAdditionManager;
import ml.pluto7073.pdapi.addition.action.OnDrinkAction;
import ml.pluto7073.pdapi.addition.action.OnDrinkSerializer;
import ml.pluto7073.pdapi.item.AbstractCustomizableDrinkItem;
import ml.pluto7073.pdapi.item.PDItems;
import ml.pluto7073.pdapi.networking.NetworkingUtils;
import net.minecraft.MethodsReturnNonnullByDefault;
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
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@MethodsReturnNonnullByDefault
public class SpecialtyDrink {

    public static final Codec<SpecialtyDrink> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(SpecialtyDrinkBase.CODEC.fieldOf("base").forGetter(SpecialtyDrink::base),
                    Codec.list(ResourceLocation.CODEC).fieldOf("additions").forGetter(SpecialtyDrink::steps),
                    Codec.list(OnDrinkAction.CODEC).fieldOf("onDrinkActions").forGetter(drink -> List.of(drink.actions)),
                    Codec.INT.fieldOf("color").forGetter(SpecialtyDrink::color),
                    Codec.simpleMap(ResourceLocation.CODEC, Codec.FLOAT, Chemicals.REGISTRY)
                            .fieldOf("chemicals").orElse(Map.of()).forGetter(drink -> drink.chemicals),
                    Codec.STRING.fieldOf("name").orElse("").forGetter(drink -> drink.name))
            .apply(instance, SpecialtyDrink::new));

    private final SpecialtyDrinkBase base;
    private final ResourceLocation[] steps;
    private final OnDrinkAction[] actions;
    private final int color;
    private final Map<ResourceLocation, Float> chemicals;
    private final String name;

    public SpecialtyDrink(SpecialtyDrinkBase base, List<ResourceLocation> steps, List<OnDrinkAction> actions, int color, Map<ResourceLocation, Float> chemicals, @Nullable String name) {
        this.base = base;
        this.steps = steps.toArray(ResourceLocation[]::new);
        this.actions = actions.toArray(OnDrinkAction[]::new);
        this.color = color;
        this.chemicals = chemicals;
        this.name = name == null ? "" : name;
    }

    public String languageKey() {
        return id().toLanguageKey("drink");
    }

    public ResourceLocation id() {
        return SpecialtyDrinkManager.getId(this);
    }

    public SpecialtyDrinkBase base() {
        return base;
    }

    public List<ResourceLocation> steps() {
        return List.of(steps);
    }

    public List<OnDrinkAction> actions() {
        ArrayList<OnDrinkAction> stepActions = new ArrayList<>();
        for (ResourceLocation step : steps) {
            stepActions.addAll(DrinkAdditionManager.get(step).actions());
        }
        stepActions.addAll(List.of(actions));
        return ImmutableList.copyOf(stepActions);
    }

    public int color() {
        return color;
    }

    public Map<ResourceLocation, Float> chemicals() {
        Map<ResourceLocation, Float> base = chemicals;
        for (ResourceLocation step : steps) {
            base = DrinkUtil.or(base, DrinkAdditionManager.get(step).getChemicals(), Float::sum);
        }
        return base;
    }

    public String name() {
        return name == null || name.isEmpty() ? languageKey() : name;
    }

    public ItemStack getAsItem() {
        return DrinkUtil.setSpecialDrink(new ItemStack(PDItems.SPECIALTY_DRINK, 1), this);
    }

    public ItemStack getAsOriginalItemWithAdditions(ItemStack source) {
        ItemStack stack = base.buildItemStack();
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
        if (!base.matches(currentResult)) return false;
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
        buf.writeResourceLocation(PDRegistries.SPECIALTY_DRINK_BASE.getKey(base.serializer()));
        base.serializer().toNetwork(buf, base);
        NetworkingUtils.arrayToNetwork(buf, steps, FriendlyByteBuf::writeResourceLocation);
        buf.writeMap(chemicals, FriendlyByteBuf::writeResourceLocation, FriendlyByteBuf::writeFloat);
        buf.writeInt(color);
        NetworkingUtils.writeDrinkActionsList(buf, actions);
        buf.writeUtf(name);
    }

    public static SpecialtyDrink fromNetwork(FriendlyByteBuf buf) {
        ResourceLocation baseSerializer = buf.readResourceLocation();
        SpecialtyDrinkBase base = PDRegistries.SPECIALTY_DRINK_BASE.getOptional(baseSerializer).orElseThrow().fromNetwork(buf);
        List<ResourceLocation> steps = NetworkingUtils.listFromNetwork(buf, FriendlyByteBuf::readResourceLocation);
        HashMap<ResourceLocation, Float> chemicals = Maps.newHashMap(buf.readMap(FriendlyByteBuf::readResourceLocation, FriendlyByteBuf::readFloat));
        int color = buf.readInt();
        List<OnDrinkAction> list = NetworkingUtils.readDrinkActionsList(buf);
        String name = buf.readUtf();
        return new SpecialtyDrink(base, steps, list, color, chemicals, name);
    }

    public static class ItemBase implements SpecialtyDrinkBase {

        private final Item item;

        public ItemBase(Item item) {
            this.item = item;
        }

        @Override
        public ItemStack buildItemStack() {
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

        public static final Codec<ItemBase> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(BuiltInRegistries.ITEM.byNameCodec().fieldOf("item").forGetter(base -> base.item))
                        .apply(instance, ItemBase::new));

        @Override
        public Codec<? extends SpecialtyDrinkBase> codec() {
            return CODEC;
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, SpecialtyDrinkBase base) {
            if (!(base instanceof ItemBase item)) throw new IllegalStateException();
            buf.writeResourceLocation(BuiltInRegistries.ITEM.getKey(item.item));
        }

        @Override
        public SpecialtyDrinkBase fromNetwork(FriendlyByteBuf buf) {
            Item item = BuiltInRegistries.ITEM.get(buf.readResourceLocation());
            return new ItemBase(item);
        }
    }

}
