package ml.pluto7073.pdapi.block;

import ml.pluto7073.pdapi.client.gui.DrinkWorkstationScreenHandler;
import ml.pluto7073.pdapi.item.PDItems;
import net.minecraft.block.BlockState;
import net.minecraft.block.CraftingTableBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class DrinkWorkstationBlock extends CraftingTableBlock {

    public static final Text TITLE = Text.translatable("container.drink_workstation");

    public DrinkWorkstationBlock(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) {
            return ActionResult.SUCCESS;
        } else {
            player.openHandledScreen(state.createScreenHandlerFactory(world, pos));
            //player.incrementStat();
            return ActionResult.CONSUME;
        }
    }

    @Override
    public NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos) {
        return new SimpleNamedScreenHandlerFactory((syncId, playerInventory, playerEntity) -> new DrinkWorkstationScreenHandler(syncId, playerInventory, ScreenHandlerContext.create(world, pos)), TITLE);
    }

    @Override
    public Item asItem() {
        return PDItems.DRINK_WORKSTATION;
    }

    @Override
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
        return new ItemStack(PDItems.DRINK_WORKSTATION, 1);
    }

}
