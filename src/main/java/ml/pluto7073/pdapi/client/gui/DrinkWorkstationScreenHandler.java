package ml.pluto7073.pdapi.client.gui;

import ml.pluto7073.pdapi.block.PDBlocks;
import ml.pluto7073.pdapi.recipes.DrinkWorkstationRecipe;
import ml.pluto7073.pdapi.recipes.PDRecipeTypes;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ForgingScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.ForgingSlotsManager;
import net.minecraft.screen.slot.Slot;
import net.minecraft.world.World;

import java.util.List;
import java.util.Optional;

public class DrinkWorkstationScreenHandler extends ForgingScreenHandler {

    private final World world;
    private DrinkWorkstationRecipe currentRecipe;
    private final List<DrinkWorkstationRecipe> recipes;

    public DrinkWorkstationScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, ScreenHandlerContext.EMPTY);
    }

    public DrinkWorkstationScreenHandler(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(PDScreens.WORKSTATION_HANDLER_TYPE, syncId, playerInventory, context);
        this.world = playerInventory.player.getWorld();
        this.recipes = this.world.getRecipeManager().listAllOfType(PDRecipeTypes.DRINK_WORKSTATION_RECIPE_TYPE);
    }

    @Override
    protected boolean canTakeOutput(PlayerEntity player, boolean present) {
        return currentRecipe != null && currentRecipe.matches(input, world);
    }

    @Override
    protected void onTakeOutput(PlayerEntity player, ItemStack stack) {
        stack.onCraft(player.getWorld(), player, stack.getCount());
        this.output.unlockLastRecipe(player, List.of(getSlot(0).getStack(), getSlot(1).getStack()));
        decrementStack(0, player);
        decrementStack(1, player);
        context.run((world, pos) -> {
            world.syncWorldEvent(10000, pos, 0);
        });
    }

    private void decrementStack(int slot, PlayerEntity player) {
        ItemStack itemStack = input.getStack(slot);
        if (itemStack.getCount() == 1) {
            if (itemStack.getItem().hasRecipeRemainder()) {
                itemStack = new ItemStack(itemStack.getItem().getRecipeRemainder(), 1);
            } else {
                itemStack.decrement(1);
            }
        } else if (itemStack.getCount() > 1) {
            itemStack.decrement(1);
            if (itemStack.getItem().hasRecipeRemainder()) {
                player.giveItemStack(new ItemStack(itemStack.getItem().getRecipeRemainder(), 1));
            }
        }
        input.setStack(slot, itemStack);
    }

    @Override
    protected boolean canUse(BlockState state) {
        return state.isOf(PDBlocks.DRINK_WORKSTATION);
    }

    @Override
    public void updateResult() {
        List<DrinkWorkstationRecipe> list = world.getRecipeManager().getAllMatches(PDRecipeTypes.DRINK_WORKSTATION_RECIPE_TYPE, input, world);
        if (list.isEmpty()) {
            output.setStack(0, ItemStack.EMPTY);
        } else {
            currentRecipe = list.get(0);
            ItemStack stack = currentRecipe.craft(input);
            output.setLastRecipe(currentRecipe);
            output.setStack(0, stack);
        }
    }

    @Override
    protected ForgingSlotsManager getForgingSlotsManager() {
        return ForgingSlotsManager.create().input(0, 27, 47, stack -> {
            return this.recipes.stream().anyMatch(recipe -> {
                return recipe.testBase(stack);
            });
        }).input(1, 76, 47, stack -> {
            return this.recipes.stream().anyMatch(recipe -> {
                return recipe.testAddition(stack);
            });
        }).output(2, 134, 47).build();
    }

    private static Optional<Integer> getQuickMoveSlot(DrinkWorkstationRecipe recipe, ItemStack stack) {
        if (recipe.testBase(stack)) {
            return Optional.of(0);
        } else {
            return recipe.testAddition(stack) ? Optional.of(1) : Optional.empty();
        }
    }

    @Override
    protected boolean isValidIngredient(ItemStack stack) {
        return this.recipes.stream().map((recipe) -> {
            return getQuickMoveSlot(recipe, stack);
        }).anyMatch(Optional::isPresent);
    }

    @Override
    public boolean canInsertIntoSlot(ItemStack stack, Slot slot) {
        return slot.inventory != output && super.canInsertIntoSlot(stack, slot);
    }

}
