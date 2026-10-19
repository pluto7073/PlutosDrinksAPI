package ml.pluto7073.pdapi.specialty;

import com.mojang.serialization.MapCodec;
import ml.pluto7073.pdapi.PDAPI;
import ml.pluto7073.pdapi.PDRegistries;
import net.minecraft.core.Registry;

public interface SpecialtyDrinkBaseSerializer {

    SpecialtyDrinkBaseSerializer ITEM_BASE = Registry.register(
            PDRegistries.SPECIALTY_DRINK_BASE,
            PDAPI.asId("item"),
            new SpecialtyDrink.ItemBaseSerializer()
    );

    MapCodec<? extends SpecialtyDrinkBase> codec();

    static void init () {}

}
