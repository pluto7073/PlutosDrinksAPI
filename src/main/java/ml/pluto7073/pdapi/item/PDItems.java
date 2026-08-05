package ml.pluto7073.pdapi.item;

import ml.pluto7073.pdapi.PDAPI;
import ml.pluto7073.pdapi.block.PDBlocks;
import ml.pluto7073.pdapi.component.DrinkAdditions;
import ml.pluto7073.pdapi.component.PDComponents;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public final class PDItems {

    public static final Item MILK_BOTTLE = new MilkBottleItem(new Item.Properties().stacksTo(16).craftRemainder(Items.GLASS_BOTTLE));
    public static final Item ICON = new Item(new Item.Properties().stacksTo(1));
    public static final Item SPECIALTY_DRINK = new SpecialtyDrinkItem(new Item.Properties().stacksTo(1).component(PDComponents.ADDITIONS, DrinkAdditions.EMPTY));
    public static final Item MUG = new BlockItem(PDBlocks.MUG, new Item.Properties().component(PDComponents.ADDITIONS, DrinkAdditions.EMPTY));

    public static final Item DRINK_WORKSTATION = new BlockItem(PDBlocks.DRINK_WORKSTATION, new Item.Properties());

    public static void init() {
        Registry.register(BuiltInRegistries.ITEM, PDAPI.asId("milk_bottle"), MILK_BOTTLE);
        Registry.register(BuiltInRegistries.ITEM, PDAPI.asId("drink_workstation"), DRINK_WORKSTATION);
        Registry.register(BuiltInRegistries.ITEM, PDAPI.asId("specialty_drink"), SPECIALTY_DRINK);
        Registry.register(BuiltInRegistries.ITEM, PDAPI.asId("icon"), ICON);
        Registry.register(BuiltInRegistries.ITEM, PDAPI.asId("mug"), MUG);
    }

}
