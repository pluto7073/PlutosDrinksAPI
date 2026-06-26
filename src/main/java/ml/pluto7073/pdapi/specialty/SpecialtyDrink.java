package ml.pluto7073.pdapi.specialty;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ml.pluto7073.chemicals.Chemicals;
import ml.pluto7073.pdapi.item.SpecialtyDrinkItem;
import ml.pluto7073.pdapi.util.DrinkUtil;
import ml.pluto7073.pdapi.PDRegistries;
import ml.pluto7073.pdapi.addition.DrinkAdditionManager;
import ml.pluto7073.pdapi.addition.action.OnDrinkAction;
import ml.pluto7073.pdapi.item.AbstractCustomizableDrinkItem;
import ml.pluto7073.pdapi.item.PDItems;
import ml.pluto7073.pdapi.networking.NetworkingUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
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

    public String languageKey(SpecialtyDrinkManager manager) {
        return id(manager).toLanguageKey("drink");
    }

    public ResourceLocation id(SpecialtyDrinkManager manager) {
        return manager.getId(this);
    }

    public SpecialtyDrinkBase base() {
        return base;
    }

    public List<ResourceLocation> steps() {
        return List.of(steps);
    }

    public List<OnDrinkAction> actions(Level level) {
        ArrayList<OnDrinkAction> stepActions = new ArrayList<>();
        for (ResourceLocation step : steps) {
            stepActions.addAll(level.getDrinkAdditionManager().get(step).actions());
        }
        stepActions.addAll(List.of(actions));
        return ImmutableList.copyOf(stepActions);
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

    public String name(Level level) {
        return name == null || name.isEmpty() ? languageKey(level.getSpecialtyDrinkManager()) : name;
    }

    public ItemStack getAsItem(Level level) {
        return DrinkUtil.setSpecialDrink(new ItemStack(PDItems.SPECIALTY_DRINK, 1), this, level);
    }

    public ItemStack getBaseItem(ItemStack source) {
        ItemStack stack = base.buildItemStack();
        CompoundTag ogData = source.getOrCreateTagElement(AbstractCustomizableDrinkItem.DRINK_DATA_NBT_KEY);
        CompoundTag newData = source.getOrCreateTag().copy();
        newData.remove("Drink");
        CompoundTag drinkData = newData.getCompound(AbstractCustomizableDrinkItem.DRINK_DATA_NBT_KEY);
        ListTag list = new ListTag();
        for (ResourceLocation step : steps) {
            list.add(StringTag.valueOf(step.toString()));
        }
        list.addAll(ogData.getList(DrinkAdditionManager.ADDITIONS_NBT_KEY, Tag.TAG_STRING));
        drinkData.put(DrinkAdditionManager.ADDITIONS_NBT_KEY, list);
        stack.setTag(newData);
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

    public void toNetwork(FriendlyByteBuf buf) {
        buf.writeResourceLocation(PDRegistries.SPECIALTY_DRINK_BASE.getKey(base.serializer()));
        base.serializer().toNetwork(buf, base);
        NetworkingUtils.arrayToNetwork(buf, steps, FriendlyByteBuf::writeResourceLocation);
        buf.writeMap(chemicals, FriendlyByteBuf::writeResourceLocation, FriendlyByteBuf::writeFloat);
        buf.writeDouble(volume);
        buf.writeInt(color);
        NetworkingUtils.writeDrinkActionsList(buf, actions);
        buf.writeUtf(name);
    }

    public static SpecialtyDrink fromNetwork(FriendlyByteBuf buf) {
        ResourceLocation baseSerializer = buf.readResourceLocation();
        SpecialtyDrinkBase base = PDRegistries.SPECIALTY_DRINK_BASE.getOptional(baseSerializer).orElseThrow().fromNetwork(buf);
        List<ResourceLocation> steps = NetworkingUtils.listFromNetwork(buf, FriendlyByteBuf::readResourceLocation);
        HashMap<ResourceLocation, Float> chemicals = Maps.newHashMap(buf.readMap(FriendlyByteBuf::readResourceLocation, FriendlyByteBuf::readFloat));
        double volume = buf.readDouble();
        int color = buf.readInt();
        List<OnDrinkAction> list = NetworkingUtils.readDrinkActionsList(buf);
        String name = buf.readUtf();
        return new SpecialtyDrink(base, steps, list, volume, color, chemicals, name);
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
