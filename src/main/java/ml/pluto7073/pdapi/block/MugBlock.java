package ml.pluto7073.pdapi.block;

import ml.pluto7073.pdapi.block.entity.MugBlockEntity;
import ml.pluto7073.pdapi.item.PDItems;
import ml.pluto7073.pdapi.recipes.DrinkWorkstationRecipe;
import ml.pluto7073.pdapi.recipes.PDRecipeTypes;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@SuppressWarnings("deprecation")
@MethodsReturnNonnullByDefault
public class MugBlock extends BaseEntityBlock {

    public static final VoxelShape SHAPE = Shapes.or(Block.box(6, 0, 6, 10, 5, 10), Block.box(4, 1, 7.5, 6, 4, 8.5));

    private final Supplier<BlockEntityType<? extends MugBlockEntity>> blockEntity;

    public MugBlock(Supplier<BlockEntityType<? extends MugBlockEntity>> blockEntity, Properties properties) {
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
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (this == PDBlocks.MUG) return InteractionResult.PASS;
        if (player.getItemInHand(hand).isEmpty()) return InteractionResult.PASS;
        Optional<? extends MugBlockEntity> opt = level.getBlockEntity(pos, blockEntity.get());
        MugBlockEntity entity;
        if (opt.isEmpty()) {
            return InteractionResult.PASS;
        } else {
            entity = opt.get();
        }
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        Container container = new SimpleContainer(2);
        container.setItem(0, entity.saveToItem());
        container.setItem(1, player.getItemInHand(hand));
        List<DrinkWorkstationRecipe> recipes = level.getRecipeManager().getRecipesFor(PDRecipeTypes.DRINK_WORKSTATION_RECIPE_TYPE, container, level);
        ItemStack result = recipes.get(0).craft(container, level);
        entity.loadFromItem(result);
        entity.setChanged();
        return InteractionResult.CONSUME;
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
        if (!state.is(newState.getBlock()) && !(newState.getBlock() instanceof MugBlock)) {
            if (level.getBlockEntity(pos) instanceof MugBlockEntity entity && level instanceof ServerLevel) {
                ItemStack stack = entity.saveToItem();
                Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), stack);
            } else if (level instanceof ServerLevel) {
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
