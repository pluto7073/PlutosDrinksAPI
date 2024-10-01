package ml.pluto7073.pdapi;

import ml.pluto7073.pdapi.addition.action.OnDrinkSerializer;
import ml.pluto7073.pdapi.addition.action.OnDrinkSerializers;
import ml.pluto7073.pdapi.specialty.SpecialtyDrinkSerializer;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;

public final class PDRegistries {

    public static final ResourceKey<Registry<OnDrinkSerializer<?>>> ON_DRINK_SERIALIZER_KEY =
            ResourceKey.createRegistryKey(PDAPI.asId("on_drink_serializer"));
    public static final ResourceKey<Registry<SpecialtyDrinkSerializer>> SPECIALITY_DRINK_SERIALIZER_KEY =
            ResourceKey.createRegistryKey(PDAPI.asId("speciality_drink_serializer"));

    public static final Registry<OnDrinkSerializer<?>> ON_DRINK_SERIALIZER =
            BuiltInRegistries.registerSimple(ON_DRINK_SERIALIZER_KEY, registry -> OnDrinkSerializers.APPLY_STATUS_EFFECT);
    public static final Registry<SpecialtyDrinkSerializer> SPECIALTY_DRINK_SERIALIZER =
            BuiltInRegistries.registerSimple(SPECIALITY_DRINK_SERIALIZER_KEY, registry -> SpecialtyDrinkSerializer.DEFAULT_SERIALIZER);

}
