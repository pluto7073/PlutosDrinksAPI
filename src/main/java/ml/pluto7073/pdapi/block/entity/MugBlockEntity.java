package ml.pluto7073.pdapi.block.entity;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import ml.pluto7073.pdapi.addition.DrinkAddition;
import ml.pluto7073.pdapi.component.DrinkAdditions;
import ml.pluto7073.pdapi.component.PDComponents;
import ml.pluto7073.pdapi.util.DrinkUtil;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.*;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static ml.pluto7073.pdapi.item.AbstractCustomizableDrinkItem.DRINK_DATA_NBT_KEY;

@MethodsReturnNonnullByDefault
public abstract class MugBlockEntity extends BlockEntity {

    protected final List<Holder<DrinkAddition>> additions;
    protected double sips;

    public MugBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
        additions = new ArrayList<>();
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag, provider);
        return tag;
    }

    @Override
    protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider provider) {
        super.saveAdditional(nbt, provider);
        if (level == null) return;
        DataResult<Tag> tagDataResult = DrinkAdditions.CODEC.encodeStart(provider.createSerializationContext(NbtOps.INSTANCE), new DrinkAdditions(additions));
        nbt.put("Additions", tagDataResult.getOrThrow());
        nbt.putDouble("Sipped", sips);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        ListTag list = tag.getList("Additions", Tag.TAG_STRING);
        additions.clear();
        DrinkAdditions additions = DrinkAdditions.CODEC.decode(registries.createSerializationContext(NbtOps.INSTANCE), list).map(Pair::getFirst).getOrThrow();
        this.additions.addAll(additions.additions());
        sips = tag.getDouble("Sipped");
    }

    public void loadFromItem(ItemStack stack) {
        additions.clear();
        additions.addAll(stack.getOrDefault(PDComponents.ADDITIONS, DrinkAdditions.EMPTY).additions());
        sips = stack.getOrDefault(PDComponents.SIPPED, 0.0);
    }

    /**
     * <strong>Note:</strong> override {@link MugBlockEntity#saveAdditionalToItemTag(ItemStack)} to add data to the saved item
     * @return A new instance of the corresponding itemStack
     */
    public final ItemStack saveToItem() {
        Item item = getBlockState().getBlock().asItem();
        if (item == Items.AIR) return ItemStack.EMPTY;
        ItemStack stack = item.getDefaultInstance();
        stack.set(PDComponents.SIPPED, sips);
        stack.set(PDComponents.ADDITIONS, new DrinkAdditions(ImmutableList.copyOf(additions)));
        saveAdditionalToItemTag(stack);
        return stack;
    }

    /**
     * Adds nbt data to the item version of this Mug Block<br><br>
     * @param stack The stack to add extra information to. Contains additions already
     */
    public void saveAdditionalToItemTag(ItemStack stack) {
    }

}
