package ml.pluto7073.pdapi.datagen.provider;

import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import ml.pluto7073.pdapi.addition.DrinkAddition;
import ml.pluto7073.pdapi.specialty.SpecialtyDrink;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.resource.conditions.v1.ConditionJsonProvider;
import net.fabricmc.fabric.impl.datagen.FabricDataGenHelper;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@MethodsReturnNonnullByDefault
public abstract class DrinkAdditionProvider implements DataProvider {

    private final PackOutput.PathProvider additionPathProvider;

    public DrinkAdditionProvider(FabricDataOutput out) {
        this.additionPathProvider = out.createPathProvider(PackOutput.Target.DATA_PACK, "drink_additions");
    }

    public abstract void buildAdditions(BiConsumer<ResourceLocation, DrinkAddition> consumer);

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        Set<ResourceLocation> generatedAdditions = Sets.newHashSet();
        List<CompletableFuture<?>> list = new ArrayList<>();

        buildAdditions((id, addition) -> {
            if (!generatedAdditions.add(id)) {
                throw new IllegalStateException("Duplicate Addition " + id);
            }

            JsonElement json = DrinkAddition.CODEC.encodeStart(JsonOps.INSTANCE, addition).getOrThrow(false, s -> {});
            ConditionJsonProvider[] conditions = FabricDataGenHelper.consumeConditions(addition);
            ConditionJsonProvider.write((JsonObject) json, conditions);

            list.add(DataProvider.saveStable(output, json, additionPathProvider.json(id)));
        });
        return CompletableFuture.allOf(list.toArray(CompletableFuture[]::new));
    }

    @Override
    public String getName() {
        return "Drink Additions";
    }

    protected static DrinkAddition.Builder builder() {
        return new DrinkAddition.Builder();
    }

    @SuppressWarnings("UnstableApiUsage")
    protected BiConsumer<ResourceLocation, DrinkAddition> withConditions(BiConsumer<ResourceLocation, DrinkAddition> output, ConditionJsonProvider... conditions) {
        return (id, drink) -> {
            FabricDataGenHelper.addConditions(drink, conditions);
            output.accept(id, drink);
        };
    }

}
