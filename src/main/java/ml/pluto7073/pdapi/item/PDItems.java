package ml.pluto7073.pdapi.item;

import ml.pluto7073.pdapi.PDAPI;
import ml.pluto7073.pdapi.block.PDBlocks;
import ml.pluto7073.pdapi.util.DrinkUtil;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public final class PDItems {

    public static final Item MILK_BOTTLE = new MilkBottleItem();
    public static final Item ICON = new Item(new Item.Properties().stacksTo(1));
    public static final Item SPECIALTY_DRINK = new SpecialtyDrinkItem(new Item.Properties().stacksTo(1));
    public static final Item MUG = new BlockItem(PDBlocks.MUG, new Item.Properties());

    public static final Item DRINK_WORKSTATION = new BlockItem(PDBlocks.DRINK_WORKSTATION, new Item.Properties());

    public static void init() {
        Registry.register(BuiltInRegistries.ITEM, PDAPI.asId("milk_bottle"), MILK_BOTTLE);
        Registry.register(BuiltInRegistries.ITEM, PDAPI.asId("drink_workstation"), DRINK_WORKSTATION);
        Registry.register(BuiltInRegistries.ITEM, PDAPI.asId("specialty_drink"), SPECIALTY_DRINK);
        Registry.register(BuiltInRegistries.ITEM, PDAPI.asId("icon"), ICON);
        Registry.register(BuiltInRegistries.ITEM, PDAPI.asId("mug"), MUG);

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS).register(entries -> {
            entries.addAfter(Items.SMITHING_TABLE, DRINK_WORKSTATION);
            entries.addAfter(Items.FLOWER_POT, MUG);
        });

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.FOOD_AND_DRINKS).register(entries -> entries.addAfter(Items.MILK_BUCKET, MILK_BOTTLE));

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.INGREDIENTS).register(entries -> entries.addAfter(Items.GLASS_BOTTLE, MUG));
    }

}
