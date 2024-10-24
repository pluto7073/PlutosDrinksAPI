package ml.pluto7073.pdapi.datagen.builder;

import ml.pluto7073.pdapi.recipes.DrinkWorkstationRecipe;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
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
    private final Advancement.Builder advancement = Advancement.Builder.recipeAdvancement();

    public WorkstationRecipeBuilder(Ingredient base, Ingredient addition, ResourceLocation result) {
        this.base = base;
        this.addition = addition;
        this.result = result;
    }

    @Override
    public RecipeBuilder unlockedBy(String criterionName, CriterionTriggerInstance criterionTrigger) {
        advancement.addCriterion(criterionName, criterionTrigger);
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
        recipeOutput.accept(id, new DrinkWorkstationRecipe(base, addition, result), null);
    }

}
