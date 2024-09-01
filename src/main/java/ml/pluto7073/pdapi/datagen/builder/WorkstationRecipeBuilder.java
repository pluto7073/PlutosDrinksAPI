package ml.pluto7073.pdapi.datagen.builder;

import com.google.gson.JsonObject;
import ml.pluto7073.pdapi.recipes.PDRecipeTypes;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
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
    public RecipeBuilder unlockedBy(String criterionName, CriterionTriggerInstance criterionTrigger) {
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
    public void save(Consumer<FinishedRecipe> finishedRecipeConsumer, ResourceLocation recipeId) {
        finishedRecipeConsumer.accept(new Result(recipeId, base, addition, result));
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
            json.add("base", base.toJson());
            json.add("addition", addition.toJson());
            json.addProperty("result", result.toString());
        }

        @Override
        public ResourceLocation getId() {
            return id;
        }

        @Override
        public RecipeSerializer<?> getType() {
            return PDRecipeTypes.DRINK_WORKSTATION_RECIPE_SERIALIZER;
        }

        @Override
        public @Nullable JsonObject serializeAdvancement() {
            return null;
        }

        @Override
        public @Nullable ResourceLocation getAdvancementId() {
            return null;
        }
    }

}
