package ml.pluto7073.pdapi.specialty;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
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

@MethodsReturnNonnullByDefault
public class SpecialtyDrink {

    private final ResourceLocation id;
    private final Item base;
    private final ResourceLocation[] steps;
    private final OnDrinkAction[] actions;
    private final int color;
    private final HashMap<String, Integer> chemicals;
    private final String name;

    public SpecialtyDrink(ResourceLocation id, Item base, ResourceLocation[] steps, OnDrinkAction[] actions, int color, HashMap<String, Integer> chemicals, @Nullable String name) {
        this.id = id;
        this.base = base;
        this.steps = steps;
        this.actions = actions;
        this.color = color;
        this.chemicals = chemicals;
        this.name = name != null ? name : id.toLanguageKey("drink");
    }

    public String languageKey() {
        return id.toLanguageKey("drink");
    }

    public ResourceLocation id() {
        return id;
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

    public HashMap<String, Integer> chemicals() {
        return chemicals;
    }

    public String name() {
        return name;
    }

    public ResourceLocation type() {
        return PDAPI.asId("specialty_drink");
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

    public ResourceLocation getId() {
        return id;
    }

    public List<Ingredient> stepsToIngredientList() {
        List<Ingredient> ingredients = new ArrayList<>();
        for (ResourceLocation addition : steps) {
            ingredients.add(DrinkUtil.additionToIngredient(addition));
        }
        return ingredients;
    }

    public void toNetwork(FriendlyByteBuf buf) {
        buf.writeResourceLocation(id);
        buf.writeResourceLocation(BuiltInRegistries.ITEM.getKey(base));
        NetworkingUtils.arrayToNetwork(buf, steps, FriendlyByteBuf::writeResourceLocation);
        buf.writeMap(chemicals, FriendlyByteBuf::writeUtf, FriendlyByteBuf::writeInt);
        buf.writeInt(color);
        NetworkingUtils.writeDrinkActionsList(buf, actions);
        buf.writeUtf(name);
    }

    public static SpecialtyDrink fromJson(ResourceLocation id, JsonObject data) {
        Item base = BuiltInRegistries.ITEM.get(new ResourceLocation(GsonHelper.getAsString(data, "base")));
        JsonArray additionsJson = GsonHelper.getAsJsonArray(data, "additions");
        List<ResourceLocation> additions = new ArrayList<>();
        for (JsonElement e : additionsJson) {
            additions.add(new ResourceLocation(e.getAsString()));
        }
        if (additions.size() > 15) throw new IllegalStateException("Specialty Drink \"" + id.toString() + "\" cannot have more than 15 steps");

        HashMap<String, Integer> chemicals = new HashMap<>();
        ConsumableChemicalRegistry.forEach(handler -> {
            if (data.has(handler.getName())) {
                chemicals.put(handler.getName(), GsonHelper.getAsInt(data, handler.getName()));
            }
        });

        int color = GsonHelper.getAsInt(data, "color");
        JsonArray actionsArray = GsonHelper.getAsJsonArray(data, "onDrinkActions");
        List<OnDrinkAction> actions = new ArrayList<>();
        for (JsonElement e : actionsArray) {
            if (!e.isJsonObject()) {
                PDAPI.LOGGER.warn("Non-JsonObject item in 'onDrinkActions' in Specialty file: {}", id);
                continue;
            }
            JsonObject actionObject = e.getAsJsonObject();
            ResourceLocation type = new ResourceLocation(GsonHelper.getAsString(actionObject, "type"));
            @SuppressWarnings("unchecked")
            OnDrinkSerializer<OnDrinkAction> serializer = (OnDrinkSerializer<OnDrinkAction>)
                    PDRegistries.ON_DRINK_SERIALIZER.get(type);
            if (serializer == null) throw new IllegalArgumentException("Unknown OnDrinkAction " + type);
            actions.add(serializer.fromJson(actionObject));
        }
        String name = null;
        if (data.has("name")) {
            name = GsonHelper.getAsString(data, "name");
        }
        return new SpecialtyDrink(id, base, additions.toArray(new ResourceLocation[0]), actions.toArray(new OnDrinkAction[0]), color, chemicals, name);
    }

    public static SpecialtyDrink fromNetwork(FriendlyByteBuf buf) {
        ResourceLocation id = buf.readResourceLocation();
        ResourceLocation base = buf.readResourceLocation();
        ResourceLocation[] steps = NetworkingUtils.listFromNetwork(buf, FriendlyByteBuf::readResourceLocation).toArray(new ResourceLocation[0]);
        HashMap<String, Integer> chemicals = Maps.newHashMap(buf.readMap(FriendlyByteBuf::readUtf, FriendlyByteBuf::readInt));
        int color = buf.readInt();
        List<OnDrinkAction> list = NetworkingUtils.readDrinkActionsList(buf);
        String name = buf.readUtf();
        return new SpecialtyDrink(id, BuiltInRegistries.ITEM.get(base), steps, list.toArray(OnDrinkAction[]::new), color, chemicals, name);
    }

}
