package ml.pluto7073.pdapi.datagen.provider;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import ml.pluto7073.pdapi.addition.DrinkAddition;
import ml.pluto7073.pdapi.specialty.SpecialtyDrink;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceCondition;
import net.fabricmc.fabric.impl.datagen.FabricDataGenHelper;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
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
    private final CompletableFuture<HolderLookup.Provider> registries;

    public DrinkAdditionProvider(FabricDataOutput out, CompletableFuture<HolderLookup.Provider> completableFuture) {
        this.additionPathProvider = out.createPathProvider(PackOutput.Target.DATA_PACK, "drink_additions");
        this.registries = completableFuture;
    }

    public abstract void buildAdditions(BiConsumer<ResourceLocation, DrinkAddition> consumer);

    public final CompletableFuture<?> run(CachedOutput writer) {
        return this.registries.thenCompose((provider) -> this.run(writer, provider));
    }

    @SuppressWarnings("UnstableApiUsage")
    public CompletableFuture<?> run(CachedOutput output, final HolderLookup.Provider provider) {
        Set<ResourceLocation> generatedAdditions = Sets.newHashSet();
        List<CompletableFuture<?>> list = new ArrayList<>();

        buildAdditions((id, addition) -> {
            if (!generatedAdditions.add(id)) {
                throw new IllegalStateException("Duplicate Addition " + id);
            }

            JsonElement json = DrinkAddition.CODEC.encodeStart(provider.createSerializationContext(JsonOps.INSTANCE), addition).getOrThrow();
            @Nullable ResourceCondition[] conditions = FabricDataGenHelper.consumeConditions(addition);
            FabricDataGenHelper.addConditions(json, conditions);

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
    protected BiConsumer<ResourceLocation, DrinkAddition> withConditions(BiConsumer<ResourceLocation, DrinkAddition> exporter, ResourceCondition... conditions) {
        Preconditions.checkArgument(conditions.length > 0, "Must add at least one condition.");
        return (id, addition) -> {
            FabricDataGenHelper.addConditions(addition, conditions);
            exporter.accept(id, addition);
        };
    }

}
