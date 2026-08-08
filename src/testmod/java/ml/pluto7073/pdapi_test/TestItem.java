package ml.pluto7073.pdapi_test;

import ml.pluto7073.pdapi.PDAPI;
import ml.pluto7073.pdapi.addition.chemicals.CaffeineHandler;
import ml.pluto7073.pdapi.item.AbstractCustomizableDrinkItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class TestItem extends AbstractCustomizableDrinkItem {

    public TestItem(Properties settings) {
        super(Items.GLASS_BOTTLE, 12, settings);
    }

    @Override
    public float getChemicalContent(ResourceLocation name, ItemStack stack, Level level) {
        return name.equals(CaffeineHandler.INSTANCE.getId()) ? 1000 : 0;
    }
}
