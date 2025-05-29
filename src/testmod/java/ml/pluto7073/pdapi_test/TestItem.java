package ml.pluto7073.pdapi_test;

import ml.pluto7073.pdapi.item.AbstractCustomizableDrinkItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class TestItem extends AbstractCustomizableDrinkItem {

    public TestItem(Properties settings) {
        super(Items.GLASS_BOTTLE, 12, settings);
    }

    @Override
    public float getChemicalContent(ResourceLocation name, ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTagElement("Chemicals");
        float amount = 0;
        if (tag.contains(name.toString())) {
            amount = tag.getFloat(name.toString());
        }
        return super.getChemicalContent(name, stack) + amount;
    }
}
