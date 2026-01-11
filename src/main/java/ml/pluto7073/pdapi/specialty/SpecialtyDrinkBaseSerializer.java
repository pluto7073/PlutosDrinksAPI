package ml.pluto7073.pdapi.specialty;

import com.mojang.serialization.Codec;
import ml.pluto7073.pdapi.PDAPI;
import ml.pluto7073.pdapi.PDRegistries;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;

public interface SpecialtyDrinkBaseSerializer {

    SpecialtyDrinkBaseSerializer ITEM_BASE = Registry.register(
            PDRegistries.SPECIALTY_DRINK_BASE,
            PDAPI.asId("item"),
            new SpecialtyDrink.ItemBaseSerializer()
    );

    Codec<? extends SpecialtyDrinkBase> codec();
    void toNetwork(FriendlyByteBuf buf, SpecialtyDrinkBase base);
    SpecialtyDrinkBase fromNetwork(FriendlyByteBuf buf);

    static void init () {}

}
