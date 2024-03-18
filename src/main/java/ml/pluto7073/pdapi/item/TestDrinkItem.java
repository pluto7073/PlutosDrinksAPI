package ml.pluto7073.pdapi.item;

import net.minecraft.item.Item;
import net.minecraft.item.Items;

public class TestDrinkItem extends AbstractCustomizableDrinkItem {

    public TestDrinkItem(Settings settings) {
        super(Items.GLASS_BOTTLE, Temperature.NORMAL, settings);
    }

}
