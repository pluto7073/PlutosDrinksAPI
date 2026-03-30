package ml.pluto7073.pdapi.block;

import ml.pluto7073.pdapi.block.entity.MugBlockEntity;
import ml.pluto7073.pdapi.item.PDItems;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

@SuppressWarnings("deprecation")
@MethodsReturnNonnullByDefault
public class MugBlock extends BaseEntityBlock {

    public static final VoxelShape SHAPE = Shapes.or(Block.box(6, 0, 6, 10, 5, 10), Block.box(4, 1, 7.5, 6, 4, 8.5));

    private final Supplier<BlockEntityType<?>> blockEntity;

    public MugBlock(Supplier<BlockEntityType<?>> blockEntity, Properties properties) {
        super(properties);
        this.blockEntity = blockEntity;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        if (level.getBlockEntity(pos) instanceof MugBlockEntity entity) {
            entity.loadFromItem(stack);
        }
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public Item asItem() {
        if (this == PDBlocks.MUG) return PDItems.MUG;
        ResourceLocation id = BuiltInRegistries.BLOCK.getKey(this);
        return BuiltInRegistries.ITEM.get(id);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock())) {
            if (level.getBlockEntity(pos) instanceof MugBlockEntity entity && level instanceof ServerLevel) {
                ItemStack stack = entity.saveToItem();
                Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), stack);
            } else if (!(newState.getBlock() instanceof MugBlock) && level instanceof ServerLevel) {
                ItemStack stack = new ItemStack(PDItems.MUG);
                Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), stack);
            }
            level.updateNeighbourForOutputSignal(pos, this);
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        BlockEntityType<?> type = blockEntity.get();
        return type == null ? null : type.create(pos, state);
    }
}
