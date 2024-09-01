package ml.pluto7073.pdapi.datagen.provider;

import com.google.common.collect.Sets;
import com.google.gson.JsonObject;
import ml.pluto7073.pdapi.addition.DrinkAddition;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@MethodsReturnNonnullByDefault
public abstract class DrinkAdditionProvider implements DataProvider {

    private final PackOutput.PathProvider additionPathProvider;

    public DrinkAdditionProvider(FabricDataOutput out) {
        this.additionPathProvider = out.createPathProvider(PackOutput.Target.DATA_PACK, "drink_additions");
    }

    public abstract void buildAdditions(Consumer<Builder> consumer);

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        Set<ResourceLocation> generatedAdditions = Sets.newHashSet();
        List<CompletableFuture<?>> list = new ArrayList<>();

        buildAdditions(builder -> {
            DrinkAddition addition = builder.build();

            ResourceLocation id = builder.id;
            if (!generatedAdditions.add(id)) {
                throw new IllegalStateException("Duplicate Addition " + id);
            }

            JsonObject json = addition.toJson();

            list.add(DataProvider.saveStable(output, json, additionPathProvider.json(id)));
        });
        return CompletableFuture.allOf(list.toArray(CompletableFuture[]::new));
    }

    @Override
    public String getName() {
        return "Drink Additions";
    }

    protected static Builder builder(ResourceLocation id) {
        return new Builder(id);
    }

    public static class Builder extends DrinkAddition.Builder {

        private final ResourceLocation id;

        private Builder(ResourceLocation id) {
            this.id = id;
        }

    }

}
