package ml.pluto7073.pdapi.specialty;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import ml.pluto7073.pdapi.DrinkUtil;
import ml.pluto7073.pdapi.PDAPI;
import ml.pluto7073.pdapi.addition.DrinkAddition;
import ml.pluto7073.pdapi.addition.DrinkAdditions;
import ml.pluto7073.pdapi.addition.OnDrink;
import ml.pluto7073.pdapi.addition.OnDrinkTemplate;
import ml.pluto7073.pdapi.item.AbstractCustomizableDrinkItem;
import ml.pluto7073.pdapi.item.PDItems;
import ml.pluto7073.pdapi.networking.NetworkingUtils;
import ml.pluto7073.pdapi.recipes.DrinkWorkstationRecipe;
import ml.pluto7073.pdapi.recipes.PDRecipeTypes;
import ml.pluto7073.pdapi.tag.PDTags;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.RegistryAccess;
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
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@MethodsReturnNonnullByDefault
public record SpecialtyDrink(ResourceLocation id, Item base, ResourceLocation[] steps, OnDrink[] actions, int color, int caffeine, String name) implements Recipe<Container> {

    public static final HashMap<ResourceLocation, SpecialtyDrink> DRINKS = new HashMap<>();

    public SpecialtyDrink(ResourceLocation id, Item base, ResourceLocation[] steps, OnDrink[] actions, int color, int caffeine, @Nullable String name) {
        this.id = id;
        this.base = base;
        this.steps = steps;
        this.actions = actions;
        this.color = color;
        this.caffeine = caffeine;
        this.name = name != null ? name : "drink." + id.getNamespace() + "." + id.getPath();
        DRINKS.put(id, this);
    }

    public ItemStack getAsItem() {
        return DrinkUtil.setSpecialDrink(new ItemStack(PDItems.SPECIALTY_DRINK, 1), this);
    }

    public ItemStack getAsOriginalItemWithAdditions(ItemStack source) {
        ItemStack stack = new ItemStack(base, 1);
        CompoundTag ogData = source.getOrCreateTagElement(AbstractCustomizableDrinkItem.DRINK_DATA_NBT_KEY);
        CompoundTag drinkData = ogData.copy();
        ListTag list = new ListTag();
        for (ResourceLocation step : steps) {
            list.add(StringTag.valueOf(step.toString()));
        }
        list.addAll(ogData.getList(DrinkAdditions.ADDITIONS_NBT_KEY, Tag.TAG_STRING));
        drinkData.put(DrinkAdditions.ADDITIONS_NBT_KEY, list);
        stack.getOrCreateTag().put(AbstractCustomizableDrinkItem.DRINK_DATA_NBT_KEY, drinkData);
        return stack;
    }

    @Override
    public boolean matches(Container container, Level level) {
        ItemStack currentResult = container.getItem(0);
        if (!currentResult.is(base)) return false;
        ListTag additions = currentResult.getOrCreateTagElement(AbstractCustomizableDrinkItem.DRINK_DATA_NBT_KEY)
                .getList(DrinkAdditions.ADDITIONS_NBT_KEY, StringTag.TAG_STRING);
        if (steps.length != additions.size()) return false;
        for (int i = 0; i < additions.size(); i++) {
            String actual = additions.getString(i);
            String wanted = steps[i].toString();
            if (!actual.equals(wanted)) return false;
        }
        return true;
    }

    @Override
    public ItemStack assemble(Container container, RegistryAccess registryAccess) {
        return getAsItem();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return getAsItem();
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return PDRecipeTypes.SPECIALTY_DRINK_RECIPE_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return PDRecipeTypes.SPECIALTY_DRINK_RECIPE_TYPE;
    }

    @MethodsReturnNonnullByDefault
    public static class Serializer implements RecipeSerializer<SpecialtyDrink> {

        public Serializer() {}

        @Override
        public SpecialtyDrink fromJson(ResourceLocation id, JsonObject data) {
            Item base = BuiltInRegistries.ITEM.get(new ResourceLocation(GsonHelper.getAsString(data, "base")));
            JsonArray additionsJson = GsonHelper.getAsJsonArray(data, "additions");
            List<ResourceLocation> additions = new ArrayList<>();
            for (JsonElement e : additionsJson) {
                additions.add(new ResourceLocation(e.getAsString()));
            }
            int caffeine = GsonHelper.getAsInt(data, "caffeine");
            int color = GsonHelper.getAsInt(data, "color");
            JsonArray actionsArray = GsonHelper.getAsJsonArray(data, "onDrinkActions");
            List<OnDrink> actions = new ArrayList<>();
            for (JsonElement e : actionsArray) {
                if (!e.isJsonObject()) {
                    PDAPI.LOGGER.warn("Non-JsonObject item in 'onDrinkActions' in Specialty file: {}", id);
                    continue;
                }
                JsonObject actionObject = e.getAsJsonObject();
                OnDrinkTemplate template;
                try {
                    template = OnDrinkTemplate.get(new ResourceLocation(GsonHelper.getAsString(actionObject, "type")));
                } catch (IllegalStateException ex) {
                    PDAPI.LOGGER.error("Could not load on drink action for add-in {} because of non-existent OnDrinkTemplate {}", id.toString(), GsonHelper.getAsString(actionObject, "type"), ex);
                    continue;
                }
                actions.add(template.parseJson(id, actionObject));
            }
            String name = null;
            if (data.has("name")) {
                name = GsonHelper.getAsString(data, "name");
            }
            return new SpecialtyDrink(id, base, additions.toArray(new ResourceLocation[0]), actions.toArray(new OnDrink[0]), color, caffeine, name);
        }

        @Override
        public SpecialtyDrink fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            ResourceLocation base = buf.readResourceLocation();
            ResourceLocation[] steps = NetworkingUtils.listFromNetwork(buf, FriendlyByteBuf::readResourceLocation).toArray(new ResourceLocation[0]);
            int caffeine = buf.readInt();
            int color = buf.readInt();
            List<JsonObject> objects = NetworkingUtils.listFromNetwork(buf, NetworkingUtils::readJsonObject);
            OnDrink[] actions = new OnDrink[objects.size()];
            for (int i = 0; i < actions.length; i++) {
                JsonObject actionObject = objects.get(i);
                OnDrinkTemplate template;
                try {
                    template = OnDrinkTemplate.get(new ResourceLocation(GsonHelper.getAsString(actionObject, "type")));
                } catch (IllegalStateException ex) {
                    PDAPI.LOGGER.error("Could not load on drink action for add-in {} because of non-existent OnDrinkTemplate {}", id.toString(), GsonHelper.getAsString(actionObject, "type"), ex);
                    continue;
                }
                actions[i] = (template.parseJson(id, actionObject));
            }
            String name = buf.readUtf();
            return new SpecialtyDrink(id, BuiltInRegistries.ITEM.get(base), steps, actions, color, caffeine, name);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, SpecialtyDrink recipe) {
            buf.writeResourceLocation(BuiltInRegistries.ITEM.getKey(recipe.base));
            NetworkingUtils.arrayToNetwork(buf, recipe.steps, FriendlyByteBuf::writeResourceLocation);
            buf.writeInt(recipe.caffeine);
            buf.writeInt(recipe.color);
            JsonObject[] actions = NetworkingUtils.convertToJson(recipe.actions, OnDrink::toJson);
            NetworkingUtils.arrayToNetwork(buf, actions, NetworkingUtils::writeJsonObjectStart);
            buf.writeUtf(recipe.name);
        }

    }

}
