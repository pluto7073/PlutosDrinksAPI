package ml.pluto7073.pdapi.block;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;

public class PDBlocks {

    public static final Block DRINK_WORKSTATION = new DrinkWorkstationBlock(FabricBlockSettings.create().mapColor(MapColor.WOOD).instrument(NoteBlockInstrument.BASS).strength(2.5F).sounds(SoundType.WOOD).burnable());

    public static void init() {
        Registry.register(BuiltInRegistries.BLOCK, "plutoscoffee:coffee_workstation", DRINK_WORKSTATION); // Using the OG PlutosCoffee ids until I find a way to safely rename them
    }

}
