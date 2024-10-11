package ml.pluto7073.pdapi.component;

import ml.pluto7073.pdapi.PDAPI;
import ml.pluto7073.pdapi.specialty.SpecialtyDrink;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;

import java.util.function.UnaryOperator;

public class PDComponents {

    public static final DataComponentType<DrinkAdditions> ADDITIONS = register("additions", builder ->
            builder.persistent(DrinkAdditions.CODEC).networkSynchronized(DrinkAdditions.STREAM_CODEC));
    public static final DataComponentType<SpecialtyDrink> SPECIALTY_DRINK = register("specialty_drink", builder ->
            builder.persistent(SpecialtyDrink.COMPONENT_CODEC).networkSynchronized(SpecialtyDrink.STREAM_COMPONENT_CODEC));

    private static <T> DataComponentType<T> register(String id, UnaryOperator<DataComponentType.Builder<T>> operator) {
        return Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, PDAPI.asId(id), (operator.apply(DataComponentType.builder())).build());
    }

    public static void init() {}

}
