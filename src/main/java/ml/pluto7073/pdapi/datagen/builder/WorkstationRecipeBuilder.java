package ml.pluto7073.pdapi.datagen.builder;

import ml.pluto7073.pdapi.recipes.DrinkWorkstationRecipe;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.advancements.Criterion;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

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
        recipeOutput.accept(id, new DrinkWorkstationRecipe(base, addition, result), null);
    }

}
