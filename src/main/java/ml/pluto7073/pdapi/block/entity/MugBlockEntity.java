package ml.pluto7073.pdapi.block.entity;

import ml.pluto7073.pdapi.addition.DrinkAddition;
import ml.pluto7073.pdapi.addition.DrinkAdditionManager;
import ml.pluto7073.pdapi.item.AbstractCustomizableDrinkItem;
import ml.pluto7073.pdapi.util.DrinkUtil;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
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

@MethodsReturnNonnullByDefault
public abstract class MugBlockEntity extends BlockEntity {

    protected final List<DrinkAddition> additions;

    public MugBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
        additions = new ArrayList<>();
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag);
        return tag;
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        super.saveAdditional(nbt);
        ListTag list = new ListTag();
        if (level == null) return;
        for (DrinkAddition addition : additions) {
            ResourceLocation id = level.getDrinkAdditionManager().getId(addition);
            list.add(StringTag.valueOf(id.toString()));
        }
        nbt.put("Additions", list);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        ListTag list = tag.getList("Additions", Tag.TAG_STRING);
        additions.clear();
        if (level == null) return;
        for (int i = 0; i < list.size(); i++) {
            ResourceLocation id = new ResourceLocation(list.getString(i));
            additions.add(level.getDrinkAdditionManager().get(id));
        }
    }

    public void loadFromItem(ItemStack stack) {
        additions.clear();
        additions.addAll(List.of(DrinkUtil.getAdditionsFromStack(stack, level)));
    }

    /**
     * <strong>Note:</strong> use {@link MugBlockEntity#saveAdditionalToItemTag(CompoundTag, CompoundTag)} to add data to the saved item
     * @return A new instance of the corresponding itemStack
     */
    public final ItemStack saveToItem() {
        Item item = getBlockState().getBlock().asItem();
        if (item == Items.AIR) return ItemStack.EMPTY;
        ItemStack stack = item.getDefaultInstance();
        CompoundTag tag = stack.getOrCreateTag();
        CompoundTag drinkData = new CompoundTag();
        saveAdditionalToItemTag(drinkData, tag);
        tag.put(AbstractCustomizableDrinkItem.DRINK_DATA_NBT_KEY, drinkData);
        return stack;
    }

    /**
     * Adds nbt data to the item version of this Mug Block<br><br>
     * <strong>Note:</strong> Always call <code>super.saveAdditionalToItemTag()</code> or else Drink Additions won't be saved
     * @param drinkDataTag The Drink Tag, stored under "DrinkData"
     * @param itemTag The base tag of the item for any extra info, does not yet include the DrinkData tag yet
     */
    public void saveAdditionalToItemTag(CompoundTag drinkDataTag, @SuppressWarnings("unused") CompoundTag itemTag) {
        if (level == null) return;
        ListTag tag = new ListTag();
        for (DrinkAddition addition : additions) {
            tag.add(StringTag.valueOf(level.getDrinkAdditionManager().getId(addition).toString()));
        }
        drinkDataTag.put(DrinkAdditionManager.ADDITIONS_NBT_KEY, tag);
    }

}
