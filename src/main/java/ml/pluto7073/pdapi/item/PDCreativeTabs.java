package ml.pluto7073.pdapi.item;

import ml.pluto7073.pdapi.PDAPI;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class PDCreativeTabs {

    public static final ResourceKey<CreativeModeTab> SPECIALTY_DRINKS_TAB = ResourceKey.create(Registries.CREATIVE_MODE_TAB, PDAPI.asId("specialty_drinks"));

    public static void init() {
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, SPECIALTY_DRINKS_TAB, FabricItemGroup.builder().icon(() -> new ItemStack(PDItems.ICON))
                .title(Component.translatable("creative_tab.pdapi.specialty_drinks")).build());
    }

}
