package ml.pluto7073.pdapi.recipes;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

@MethodsReturnNonnullByDefault
public record DrinkWorkstationRecipeInput(ItemStack base, ItemStack addition) implements RecipeInput {

    public DrinkWorkstationRecipeInput(Container container) {
        this(container.getItem(0), container.getItem(1));
    }

    @Override
    public ItemStack getItem(int i) {
        return switch (i) {
            case 0 -> base;
            case 1 -> addition;
            default -> throw new IllegalArgumentException("Recipe does not contain slot " + i);
        };
    }

    @Override
    public int size() {
        return 2;
    }

    @Override
    public boolean isEmpty() {
        return base.isEmpty() && addition.isEmpty();
    }
}
