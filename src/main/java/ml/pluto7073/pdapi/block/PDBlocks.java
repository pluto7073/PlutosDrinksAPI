package ml.pluto7073.pdapi.block;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.block.enums.Instrument;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;

public class PDBlocks {

    public static final Block DRINK_WORKSTATION = new DrinkWorkstationBlock(FabricBlockSettings.create().mapColor(MapColor.OAK_TAN).instrument(Instrument.BASS).strength(2.5F).sounds(BlockSoundGroup.WOOD).burnable().sounds(BlockSoundGroup.WOOD));

    public static void init() {
        Registry.register(Registries.BLOCK, "plutoscoffee:coffee_workstation", DRINK_WORKSTATION); // Using the OG PlutosCoffee ids until I find a way to safely rename them
    }

}
