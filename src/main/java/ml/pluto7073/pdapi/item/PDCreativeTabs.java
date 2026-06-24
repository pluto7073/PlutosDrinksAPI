package ml.pluto7073.pdapi.item;

import ml.pluto7073.pdapi.PDAPI;
import ml.pluto7073.pdapi.specialty.SpecialtyDrink;
import ml.pluto7073.pdapi.specialty.SpecialtyDrinkManager;
import ml.pluto7073.pdapi.util.DrinkUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class PDCreativeTabs {

    public static final ResourceKey<CreativeModeTab> SPECIALTY_DRINKS_TAB = ResourceKey.create(Registries.CREATIVE_MODE_TAB, PDAPI.asId("specialty_drinks"));

    public static void init() {
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, SPECIALTY_DRINKS_TAB, FabricItemGroup.builder().icon(() -> new ItemStack(PDItems.ICON))
                .title(Component.translatable("creative_tab.pdapi.specialty_drinks")).build());
    }

    public static void initEntries() {
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS).register(entries -> {
            entries.addAfter(Items.SMITHING_TABLE, PDItems.DRINK_WORKSTATION);
            entries.addAfter(Items.FLOWER_POT, PDItems.MUG);
        });
        ItemGroupEvents.modifyEntriesEvent(SPECIALTY_DRINKS_TAB).register(stacks -> {
            SpecialtyDrinkManager manager = DrinkUtil.supplyIf(() -> FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT, () -> Minecraft.getInstance().level.getSpecialtyDrinkManager(), () -> PDAPI.SERVER_SPECIALITY_DRINK_MANAGER);
            for (SpecialtyDrink d : manager.values()
                    .stream().sorted(DrinkUtil.alphabetizer(drink -> drink.languageKey(Minecraft.getInstance().level))).toList()) {
                stacks.accept(d.getAsItem(Minecraft.getInstance().level));
            }
        });

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.FOOD_AND_DRINKS).register(entries -> entries.addAfter(Items.MILK_BUCKET, PDItems.MILK_BOTTLE));

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.INGREDIENTS).register(entries -> entries.addAfter(Items.GLASS_BOTTLE, PDItems.MUG));
    }

}
