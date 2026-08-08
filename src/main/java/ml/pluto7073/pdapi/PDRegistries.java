package ml.pluto7073.pdapi;

import ml.pluto7073.pdapi.addition.DrinkAddition;
import ml.pluto7073.pdapi.addition.action.OnDrinkSerializer;
import ml.pluto7073.pdapi.addition.action.OnDrinkSerializers;
import ml.pluto7073.pdapi.specialty.SpecialtyDrink;
import ml.pluto7073.pdapi.specialty.SpecialtyDrinkBaseSerializer;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;

public final class PDRegistries {

    public static final ResourceKey<Registry<OnDrinkSerializer<?>>> ON_DRINK_SERIALIZER_KEY =
            ResourceKey.createRegistryKey(PDAPI.asId("on_drink_serializer"));
    public static final ResourceKey<Registry<SpecialtyDrinkBaseSerializer>> SPECIALTY_DRINK_BASE_KEY =
            ResourceKey.createRegistryKey(PDAPI.asId("specialty_drink_base"));
    public static final ResourceKey<Registry<DrinkAddition>> DRINK_ADDITION_KEY =
            ResourceKey.createRegistryKey(PDAPI.asId("drink_addition"));
    public static final ResourceKey<Registry<SpecialtyDrink>> SPECIALITY_DRINK_KEY =
            ResourceKey.createRegistryKey(PDAPI.asId("speciality_drink"));

    public static final Registry<OnDrinkSerializer<?>> ON_DRINK_SERIALIZER =
            BuiltInRegistries.registerSimple(ON_DRINK_SERIALIZER_KEY, registry -> OnDrinkSerializers.APPLY_STATUS_EFFECT);
    public static final Registry<SpecialtyDrinkBaseSerializer> SPECIALTY_DRINK_BASE =
            BuiltInRegistries.registerSimple(SPECIALTY_DRINK_BASE_KEY, registry -> SpecialtyDrinkBaseSerializer.ITEM_BASE);

    public static void initSynced() {
        DynamicRegistries.registerSynced(DRINK_ADDITION_KEY, DrinkAddition.CODEC);
        DynamicRegistries.registerSynced(SPECIALITY_DRINK_KEY, SpecialtyDrink.CODEC);
    }

}
