package ml.pluto7073.pdapi;

import ml.pluto7073.pdapi.addition.action.OnDrinkSerializer;
import ml.pluto7073.pdapi.addition.action.OnDrinkSerializers;
import ml.pluto7073.pdapi.specialty.SpecialtyDrink;
import ml.pluto7073.pdapi.specialty.SpecialtyDrinkBaseSerializer;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;

public final class PDRegistries {

    public static final ResourceKey<Registry<OnDrinkSerializer<?>>> ON_DRINK_SERIALIZER_KEY =
            ResourceKey.createRegistryKey(PDAPI.asId("on_drink_serializer"));
    public static final ResourceKey<Registry<SpecialtyDrinkBaseSerializer>> SPECIALTY_DRINK_BASE_KEY =
            ResourceKey.createRegistryKey(PDAPI.asId("specialty_drink_base"));

    public static final Registry<OnDrinkSerializer<?>> ON_DRINK_SERIALIZER =
            BuiltInRegistries.registerSimple(ON_DRINK_SERIALIZER_KEY, registry -> OnDrinkSerializers.APPLY_STATUS_EFFECT);
    public static final Registry<SpecialtyDrinkBaseSerializer> SPECIALTY_DRINK_BASE =
            BuiltInRegistries.registerSimple(SPECIALTY_DRINK_BASE_KEY, registry -> SpecialtyDrinkBaseSerializer.ITEM_BASE);

}
