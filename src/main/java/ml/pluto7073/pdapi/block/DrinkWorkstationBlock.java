package ml.pluto7073.pdapi.block;

import ml.pluto7073.pdapi.client.gui.DrinkWorkstationScreenHandler;
import ml.pluto7073.pdapi.item.PDItems;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CraftingTableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

@MethodsReturnNonnullByDefault
public class DrinkWorkstationBlock extends CraftingTableBlock {

    public static final Component TITLE = Component.translatable("container.drink_workstation");

    public DrinkWorkstationBlock(Properties settings) {
        super(settings);
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (world.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            player.openMenu(state.getMenuProvider(world, pos));
            //player.incrementStat();
            return InteractionResult.CONSUME;
        }
    }

    @Override
    public MenuProvider getMenuProvider(BlockState state, Level world, BlockPos pos) {
        return new SimpleMenuProvider((syncId, playerInventory, playerEntity) -> new DrinkWorkstationScreenHandler(syncId, playerInventory, ContainerLevelAccess.create(world, pos)), TITLE);
    }

    @Override
    public Item asItem() {
        return PDItems.DRINK_WORKSTATION;
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter world, BlockPos pos, BlockState state) {
        return new ItemStack(PDItems.DRINK_WORKSTATION, 1);
    }

}
