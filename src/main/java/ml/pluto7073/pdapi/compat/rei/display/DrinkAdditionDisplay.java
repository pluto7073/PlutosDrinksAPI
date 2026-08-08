package ml.pluto7073.pdapi.compat.rei.display;

import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.registry.RecipeManagerContext;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import ml.pluto7073.pdapi.compat.rei.DrinkREI;
import ml.pluto7073.pdapi.recipes.DrinkWorkstationRecipe;
import ml.pluto7073.pdapi.util.DrinkUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class DrinkAdditionDisplay extends BasicDisplay {

    public DrinkAdditionDisplay(RecipeHolder<DrinkWorkstationRecipe> recipe) {
        this(EntryIngredients.ofIngredients(recipe.value().getIngredients()),
                Collections.singletonList(EntryIngredients.of(recipe.value().getResultItem(Minecraft.getInstance().level.registryAccess()))),
                recipe);
    }

    public DrinkAdditionDisplay(List<EntryIngredient> inputs, List<EntryIngredient> outputs, CompoundTag tag) {
        this(inputs, outputs, RecipeManagerContext.getInstance().byId(tag, "location"));
    }

    public DrinkAdditionDisplay(List<EntryIngredient> inputs, List<EntryIngredient> outputs, RecipeHolder<?> recipe) {
        super(inputs, outputs, Optional.ofNullable(recipe).map(RecipeHolder::id));
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return DrinkREI.DRINK_ADDITION;
    }
}
