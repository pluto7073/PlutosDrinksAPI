package ml.pluto7073.pdapi.client.gui;

import ml.pluto7073.pdapi.specialty.SpecialtyDrinkManager;
import ml.pluto7073.pdapi.util.DrinkUtil;
import ml.pluto7073.pdapi.block.PDBlocks;
import ml.pluto7073.pdapi.item.PDItems;
import ml.pluto7073.pdapi.recipes.DrinkWorkstationRecipe;
import ml.pluto7073.pdapi.recipes.PDRecipeTypes;
import ml.pluto7073.pdapi.specialty.SpecialtyDrink;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.ItemCombinerMenuSlotDefinition;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import java.util.List;
import java.util.Optional;

public class DrinkWorkstationMenu extends ItemCombinerMenu {

    private final Level world;
    private RecipeHolder<DrinkWorkstationRecipe> currentRecipe;
    private final List<RecipeHolder<DrinkWorkstationRecipe>> recipes;

    public DrinkWorkstationMenu(int syncId, Inventory playerInventory) {
        this(syncId, playerInventory, ContainerLevelAccess.NULL);
    }

    public DrinkWorkstationMenu(int syncId, Inventory playerInventory, ContainerLevelAccess context) {
        super(PDScreens.WORKSTATION_MENU_TYPE, syncId, playerInventory, context);
        this.world = playerInventory.player.level();
        this.recipes = this.world.getRecipeManager().getAllRecipesFor(PDRecipeTypes.DRINK_WORKSTATION_RECIPE_TYPE);
    }

    @Override
    protected boolean mayPickup(Player player, boolean present) {
        return currentRecipe != null && currentRecipe.value().matches(inputSlots, world);
    }

    @Override
    protected void onTake(Player player, ItemStack stack) {
        stack.onCraftedBy(player.level(), player, stack.getCount());
        this.resultSlots.awardUsedRecipes(player, List.of(getSlot(0).getItem(), getSlot(1).getItem()));
        decrementStack(0, player);
        decrementStack(1, player);
        access.execute((world, pos) -> {
            world.levelEvent(10000, pos, 0);
        });
    }

    private void decrementStack(int slot, Player player) {
        ItemStack itemStack = inputSlots.getItem(slot);
        if (itemStack.getCount() == 1) {
            if (itemStack.getItem().hasCraftingRemainingItem()) {
                itemStack = new ItemStack(itemStack.getItem().getCraftingRemainingItem(), 1);
            } else {
                itemStack.shrink(1);
            }
        } else if (itemStack.getCount() > 1) {
            itemStack.shrink(1);
            if (itemStack.getItem().hasCraftingRemainingItem()) {
                player.addItem(new ItemStack(itemStack.getItem().getCraftingRemainingItem(), 1));
            }
        }
        inputSlots.setItem(slot, itemStack);
    }

    @Override
    protected boolean isValidBlock(BlockState state) {
        return state.is(PDBlocks.DRINK_WORKSTATION);
    }

    @Override
    public void createResult() {
        Container testInput = DrinkUtil.copyContainerContents(inputSlots);

        if (inputSlots.getItem(0).is(PDItems.SPECIALTY_DRINK)) {
            testInput.setItem(0, DrinkUtil.getSpecialDrink(inputSlots.getItem(0)).getAsOriginalItemWithAdditions(inputSlots.getItem(0)));
        }

        List<RecipeHolder<DrinkWorkstationRecipe>> list = world.getRecipeManager().getRecipesFor(PDRecipeTypes.DRINK_WORKSTATION_RECIPE_TYPE, testInput, world);
        if (list.isEmpty()) {
            resultSlots.setItem(0, ItemStack.EMPTY);
        } else {
            currentRecipe = list.get(0);
            ItemStack stack = currentRecipe.value().craft(inputSlots);
            resultSlots.setRecipeUsed(currentRecipe);
            resultSlots.setItem(0, stack);

            // Specialty Drink testing
            Container testResults = DrinkUtil.copyContainerContents(resultSlots);
            if (resultSlots.getItem(0).is(PDItems.SPECIALTY_DRINK)) {
                testResults.setItem(0, DrinkUtil.getSpecialDrink(resultSlots.getItem(0)).getAsOriginalItemWithAdditions(resultSlots.getItem(0)));
            }
            List<SpecialtyDrink> matchingDrinks = SpecialtyDrinkManager.values().stream()
                    .filter(drink -> drink.matches(testResults)).toList();
            if (matchingDrinks.isEmpty()) return;
            SpecialtyDrink drink = matchingDrinks.get(0);
            stack = drink.getAsItem();
            resultSlots.setItem(0, stack);
        }
    }

    @Override
    protected ItemCombinerMenuSlotDefinition createInputSlotDefinitions() {
        return ItemCombinerMenuSlotDefinition.create().withSlot(0, 27, 47, stack -> {
            return this.recipes.stream().anyMatch(recipe -> {
                return recipe.value().testBase(stack);
            });
        }).withSlot(1, 76, 47, stack -> {
            return this.recipes.stream().anyMatch(recipe -> {
                return recipe.value().testAddition(stack);
            });
        }).withResultSlot(2, 134, 47).build();
    }

    private static Optional<Integer> getQuickMoveSlot(DrinkWorkstationRecipe recipe, ItemStack stack) {
        if (recipe.testBase(stack)) {
            return Optional.of(0);
        } else {
            return recipe.testAddition(stack) ? Optional.of(1) : Optional.empty();
        }
    }

    @Override
    protected boolean canMoveIntoInputSlots(ItemStack stack) {
        return this.recipes.stream().map((recipe) -> {
            return getQuickMoveSlot(recipe.value(), stack);
        }).anyMatch(Optional::isPresent);
    }

    @Override
    public boolean canTakeItemForPickAll(ItemStack stack, Slot slot) {
        return slot.container != resultSlots && super.canTakeItemForPickAll(stack, slot);
    }

}
