package ml.pluto7073.pdapi.datagen.provider;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import ml.pluto7073.pdapi.PDAPI;
import ml.pluto7073.pdapi.addition.action.OnDrinkAction;
import ml.pluto7073.pdapi.specialty.SpecialtyDrink;
import ml.pluto7073.pdapi.specialty.SpecialtyDrinkBase;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceCondition;
import net.fabricmc.fabric.impl.datagen.FabricDataGenHelper;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

@MethodsReturnNonnullByDefault
public abstract class SpecialtyDrinkProvider implements DataProvider {

    private final PackOutput.PathProvider drinkPathProvider;

    public SpecialtyDrinkProvider(FabricDataOutput out) {
        this.drinkPathProvider = out.createPathProvider(PackOutput.Target.DATA_PACK, "specialty_drinks");
    }

    public abstract void buildDrinks(BiConsumer<ResourceLocation, SpecialtyDrink> output);

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        Set<ResourceLocation> generated = Sets.newHashSet();
        List<CompletableFuture<?>> list = new ArrayList<>();

        buildDrinks((id, drink) -> {
            if (!generated.add(id))
                throw new IllegalStateException("Duplicate Drink " + id);

            JsonElement json = SpecialtyDrink.CODEC.encodeStart(JsonOps.INSTANCE, drink).getOrThrow();
            @Nullable ResourceCondition[] conditions = FabricDataGenHelper.consumeConditions(drink);
            FabricDataGenHelper.addConditions(json, conditions);

            list.add(DataProvider.saveStable(output, json, drinkPathProvider.json(id)));
        });

        return CompletableFuture.allOf(list.toArray(CompletableFuture[]::new));
    }

    @Override
    public String getName() {
        return "";
    }

    protected static DrinkBuilder builder(SpecialtyDrinkBase base) {
        return new DrinkBuilder(base);
    }

    protected static DrinkBuilder staticBaseBuilder(Item base) {
        return new DrinkBuilder(new SpecialtyDrink.ItemBase(base));
    }

    @SuppressWarnings("UnstableApiUsage")
    protected BiConsumer<ResourceLocation, SpecialtyDrink> withConditions(BiConsumer<ResourceLocation, SpecialtyDrink> exporter, ResourceCondition... conditions) {
        Preconditions.checkArgument(conditions.length > 0, "Must add at least one condition.");
        return (id, drink) -> {
            FabricDataGenHelper.addConditions(drink, conditions);
            exporter.accept(id, drink);
        };
    }

    public static final class DrinkBuilder {

        private final SpecialtyDrinkBase base;
        private final List<ResourceLocation> additions = new ArrayList<>();
        private final List<OnDrinkAction> actions = new ArrayList<>();
        private int color = -1;
        private double volume = 0;
        private final Map<ResourceLocation, Float> chemicals = new HashMap<>();
        private String name = "";

        public DrinkBuilder(SpecialtyDrinkBase base) {
            this.base = base;
        }

        public DrinkBuilder step(ResourceLocation id) {
            additions.add(id);
            return this;
        }

        public DrinkBuilder action(OnDrinkAction action) {
            actions.add(action);
            return this;
        }

        public DrinkBuilder color(int color) {
            this.color = color;
            return this;
        }

        public DrinkBuilder chemical(ResourceLocation id, float amount) {
            chemicals.put(id, amount);
            return this;
        }

        public DrinkBuilder name(String name) {
            this.name = name;
            return this;
        }

        public DrinkBuilder volume(double volume) {
            this.volume = volume;
            return this;
        }

        public void save(ResourceLocation id, BiConsumer<ResourceLocation, SpecialtyDrink> output) {
            if (color == -1) {
                PDAPI.LOGGER.warn("Drink {} is lacking a color", id);
            }
            if (additions.isEmpty()) {
                throw new IllegalStateException("Drink " + id + " is not craftable");
            }
            output.accept(id, new SpecialtyDrink(base, additions, actions, volume, color, chemicals, name));
        }

    }

}
