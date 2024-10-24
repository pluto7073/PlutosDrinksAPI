package ml.pluto7073.pdapi.datagen.builder;

import ml.pluto7073.pdapi.recipes.DrinkWorkstationRecipe;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

@MethodsReturnNonnullByDefault
public class WorkstationRecipeBuilder implements RecipeBuilder {

    private final Ingredient base;
    private final Ingredient addition;
    private final ResourceLocation result;
    private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();

    public WorkstationRecipeBuilder(Ingredient base, Ingredient addition, ResourceLocation result) {
        this.base = base;
        this.addition = addition;
        this.result = result;
    }

    @Override
    public RecipeBuilder unlockedBy(String criterionName, Criterion<?> criterionTrigger) {
        criteria.put(criterionName, criterionTrigger);
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
    public void save(RecipeOutput exporter, ResourceLocation id) {
        Advancement.Builder builder = exporter.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
                .rewards(AdvancementRewards.Builder.recipe(id)).requirements(AdvancementRequirements.Strategy.OR);
        criteria.forEach(builder::addCriterion);
        exporter.accept(id, new DrinkWorkstationRecipe(base, addition, result), builder.build(id.withPrefix("recipe/workstation/")));
    }

}
