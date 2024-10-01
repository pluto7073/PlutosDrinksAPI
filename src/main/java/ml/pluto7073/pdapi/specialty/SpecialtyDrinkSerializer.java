package ml.pluto7073.pdapi.specialty;

import com.google.gson.JsonObject;
import ml.pluto7073.pdapi.PDAPI;
import ml.pluto7073.pdapi.PDRegistries;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public interface SpecialtyDrinkSerializer {

    SpecialtyDrinkSerializer DEFAULT_SERIALIZER = register("specialty_drink", new SpecialtyDrink.BaseSerializer());

    SpecialtyDrink fromJson(ResourceLocation id, JsonObject data);
    SpecialtyDrink fromNetwork(FriendlyByteBuf buf);
    void toNetwork(SpecialtyDrink drink, FriendlyByteBuf buf);

    private static SpecialtyDrinkSerializer register(String id, SpecialtyDrinkSerializer serializer) {
        return Registry.register(PDRegistries.SPECIALTY_DRINK_SERIALIZER, PDAPI.asId(id), serializer);
    }

    static void init() {}

}
