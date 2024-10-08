package ml.pluto7073.pdapi.datagen.builder;

import com.google.gson.JsonObject;
import ml.pluto7073.pdapi.recipes.PDRecipeTypes;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

@MethodsReturnNonnullByDefault
public class WorkstationRecipeBuilder implements RecipeBuilder {

    private final Ingredient base;
    private final Ingredient addition;
    private final ResourceLocation result;

    public WorkstationRecipeBuilder(Ingredient base, Ingredient addition, ResourceLocation result) {
        this.base = base;
        this.addition = addition;
        this.result = result;
    }

    @Override
    public RecipeBuilder unlockedBy(String name, Criterion<?> criterion) {
        return this;
    }

    @Override
    public RecipeBuilder group(@Nullable String groupName) {
        return this;
    }

    @Override
    public Item getResult() {
        return addition.getItems()[0].getItem();
    }

    @Override
    public void save(RecipeOutput recipeOutput, ResourceLocation id) {
        recipeOutput.accept(new Result(id, base, addition, result));
    }

    public static class Result implements FinishedRecipe {

        private final ResourceLocation id;
        private final Ingredient base;
        private final Ingredient addition;
        private final ResourceLocation result;

        public Result(ResourceLocation id, Ingredient base, Ingredient addition, ResourceLocation result) {
            this.id = id;
            this.base = base;
            this.addition = addition;
            this.result = result;
        }

        @Override
        public void serializeRecipeData(JsonObject json) {
            json.add("base", base.toJson(true));
            json.add("addition", addition.toJson(true));
            json.addProperty("result", result.toString());
        }

        @Override
        public ResourceLocation id() {
            return id;
        }

        @Override
        public RecipeSerializer<?> type() {
            return PDRecipeTypes.DRINK_WORKSTATION_RECIPE_SERIALIZER;
        }

        @Override
        public @Nullable AdvancementHolder advancement() {
            return null;
        }

    }

}
