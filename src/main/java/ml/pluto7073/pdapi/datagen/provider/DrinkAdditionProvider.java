package ml.pluto7073.pdapi.datagen.provider;

import com.google.common.collect.Sets;
import com.google.gson.JsonObject;
import ml.pluto7073.pdapi.addition.DrinkAddition;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

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
        return this.registries.thenCompose((provider) -> {
            return this.run(writer, provider);
        });
    }

    public CompletableFuture<?> run(CachedOutput output, final HolderLookup.Provider provider) {
        Set<ResourceLocation> generatedAdditions = Sets.newHashSet();
        List<CompletableFuture<?>> list = new ArrayList<>();

        buildAdditions((id, addition) -> {
            if (!generatedAdditions.add(id)) {
                throw new IllegalStateException("Duplicate Addition " + id);
            }

            list.add(DataProvider.saveStable(output, provider, DrinkAddition.CODEC, addition, additionPathProvider.json(id)));
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

}
