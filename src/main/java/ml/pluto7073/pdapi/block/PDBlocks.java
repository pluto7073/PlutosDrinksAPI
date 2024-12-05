package ml.pluto7073.pdapi.block;

import ml.pluto7073.pdapi.PDAPI;
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
        Registry.register(BuiltInRegistries.BLOCK, PDAPI.asId("drink_workstation"), DRINK_WORKSTATION);
    }

}
