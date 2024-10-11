package ml.pluto7073.pdapi.item;

import ml.pluto7073.pdapi.PDAPI;
import ml.pluto7073.pdapi.block.PDBlocks;
import ml.pluto7073.pdapi.component.DrinkAdditions;
import ml.pluto7073.pdapi.component.PDComponents;
import ml.pluto7073.pdapi.specialty.SpecialtyDrink;
import ml.pluto7073.pdapi.util.DrinkUtil;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public final class PDItems {

    public static final Item MILK_BOTTLE = new MilkBottleItem();
    public static final Item SPECIALTY_DRINK = new SpecialtyDrinkItem(new Item.Properties().stacksTo(1)
            .component(PDComponents.ADDITIONS, DrinkAdditions.EMPTY).component(PDComponents.SPECIALTY_DRINK, SpecialtyDrink.EMPTY));
    public static final Item TEST_DRINK_ITEM = DrinkUtil.dev() ? new TestDrinkItem(new Item.Properties().stacksTo(1)
            .component(PDComponents.ADDITIONS, DrinkAdditions.EMPTY)) : null;

    public static final Item DRINK_WORKSTATION = new BlockItem(PDBlocks.DRINK_WORKSTATION, new Item.Properties());

    public static void init() {
        Registry.register(BuiltInRegistries.ITEM, "plutoscoffee:milk_bottle", MILK_BOTTLE); // Using the OG PlutosCoffee ids until I find a way to safely rename them
        Registry.register(BuiltInRegistries.ITEM, "plutoscoffee:coffee_workstation", DRINK_WORKSTATION);
        Registry.register(BuiltInRegistries.ITEM, PDAPI.asId("specialty_drink"), SPECIALTY_DRINK);

        if (DrinkUtil.dev()) Registry.register(BuiltInRegistries.ITEM, PDAPI.asId("test_drink"), TEST_DRINK_ITEM);

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS).register(entries -> entries.addAfter(Items.SMITHING_TABLE, DRINK_WORKSTATION));

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.FOOD_AND_DRINKS).register(entries -> entries.addAfter(Items.MILK_BUCKET, MILK_BOTTLE));
    }

}
