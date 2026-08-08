package ml.pluto7073.pdapi.component;

import com.mojang.serialization.Codec;
import ml.pluto7073.pdapi.PDAPI;
import ml.pluto7073.pdapi.specialty.SpecialtyDrink;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.item.Item;

import java.util.function.UnaryOperator;

public class PDComponents {

    public static final DataComponentType<DrinkAdditions> ADDITIONS = register("additions", builder ->
            builder.persistent(DrinkAdditions.CODEC).networkSynchronized(DrinkAdditions.STREAM_CODEC));
    public static final DataComponentType<Holder<SpecialtyDrink>> SPECIALTY_DRINK = register("specialty_drink", builder ->
            builder.persistent(SpecialtyDrink.COMPONENT_CODEC).networkSynchronized(SpecialtyDrink.STREAM_COMPONENT_CODEC));
    public static final DataComponentType<Double> SIPPED = register("sipped", builder ->
            builder.persistent(Codec.DOUBLE).networkSynchronized(ByteBufCodecs.DOUBLE));
    public static final DataComponentType<Item> FROM_ITEM = register("from_item", builder ->
            builder.persistent(BuiltInRegistries.ITEM.byNameCodec()).networkSynchronized(ByteBufCodecs.fromCodecWithRegistries(BuiltInRegistries.ITEM.byNameCodec())));

    private static <T> DataComponentType<T> register(String id, UnaryOperator<DataComponentType.Builder<T>> operator) {
        return Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, PDAPI.asId(id), (operator.apply(DataComponentType.builder())).build());
    }

    public static void init() {}

}
