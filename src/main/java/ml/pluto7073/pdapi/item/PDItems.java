package ml.pluto7073.pdapi.item;

import ml.pluto7073.pdapi.PDAPI;
import ml.pluto7073.pdapi.block.PDBlocks;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public final class PDItems {

    public static final Item MILK_BOTTLE = new MilkBottleItem();
    // public static final Item TEST_DRINK_ITEM = new TestDrinkItem(new Item.Settings().maxCount(1));

    public static final Item DRINK_WORKSTATION = new BlockItem(PDBlocks.DRINK_WORKSTATION, new Item.Settings());

    public static void init() {
        Registry.register(Registries.ITEM, "plutoscoffee:milk_bottle", MILK_BOTTLE); // Using the OG PlutosCoffee ids until I find a way to safely rename them
        Registry.register(Registries.ITEM, "plutoscoffee:coffee_workstation", DRINK_WORKSTATION);

        // Registry.register(Registries.ITEM, PDAPI.asId("test_drink"), TEST_DRINK_ITEM);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(entries -> entries.add(DRINK_WORKSTATION));

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK).register(entries -> entries.add(MILK_BOTTLE));
    }

}
