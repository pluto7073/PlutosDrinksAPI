package ml.pluto7073.pdapi.item;

import ml.pluto7073.pdapi.block.PDBlocks;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;

public final class PDItems {

    public static final Item MILK_BOTTLE = new MilkBottleItem();
    // public static final Item TEST_DRINK_ITEM = new TestDrinkItem(new Item.Settings().maxCount(1));

    public static final Item DRINK_WORKSTATION = new BlockItem(PDBlocks.DRINK_WORKSTATION, new Item.Properties());

    public static void init() {
        Registry.register(BuiltInRegistries.ITEM, "plutoscoffee:milk_bottle", MILK_BOTTLE); // Using the OG PlutosCoffee ids until I find a way to safely rename them
        Registry.register(BuiltInRegistries.ITEM, "plutoscoffee:coffee_workstation", DRINK_WORKSTATION);

        // Registry.register(Registries.ITEM, PDAPI.asId("test_drink"), TEST_DRINK_ITEM);

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS).register(entries -> entries.accept(DRINK_WORKSTATION));

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.FOOD_AND_DRINKS).register(entries -> entries.accept(MILK_BOTTLE));
    }

}
