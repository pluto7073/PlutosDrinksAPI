package ml.pluto7073.pdapi.specialty;

import com.mojang.serialization.MapCodec;
import ml.pluto7073.pdapi.PDAPI;
import ml.pluto7073.pdapi.PDRegistries;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public interface SpecialtyDrinkSerializer {

    SpecialtyDrinkSerializer DEFAULT_SERIALIZER = registerDefault(new SpecialtyDrink.BaseSerializer());

    MapCodec<SpecialtyDrink> codec();
    StreamCodec<RegistryFriendlyByteBuf, SpecialtyDrink> streamCodec();

    private static SpecialtyDrinkSerializer registerDefault(SpecialtyDrinkSerializer serializer) {
        return Registry.register(PDRegistries.SPECIALTY_DRINK_SERIALIZER, PDAPI.asId("specialty_drink"), serializer);
    }

    static void init() {}

}
