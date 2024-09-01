package ml.pluto7073.pdapi;

import ml.pluto7073.pdapi.addition.action.OnDrinkSerializer;
import ml.pluto7073.pdapi.addition.action.OnDrinkSerializers;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;

public final class PDRegistries {

    public static final ResourceKey<Registry<OnDrinkSerializer<?>>> KEY =
            ResourceKey.createRegistryKey(PDAPI.asId("on_drink_serializer"));
    public static final Registry<OnDrinkSerializer<?>> ON_DRINK_SERIALIZER =
            BuiltInRegistries.registerSimple(KEY, registry -> OnDrinkSerializers.APPLY_STATUS_EFFECT);

}
