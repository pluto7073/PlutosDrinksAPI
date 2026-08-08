package ml.pluto7073.pdapi.addition.action;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public interface OnDrinkSerializer<T extends OnDrinkAction> {

    MapCodec<T> codec();
    StreamCodec<RegistryFriendlyByteBuf, T> streamCodec();

    /**
     * Symbolizes a constant action that is always the same and doesn't take any parameters
     */
    class EmptySerializer<T extends OnDrinkAction> implements OnDrinkSerializer<T> {

        private final T value;
        private final MapCodec<T> codec;
        private final StreamCodec<RegistryFriendlyByteBuf, T> streamCodec;

        public EmptySerializer(T value) {
            this.value = value;
            this.codec = MapCodec.unit(value);
            this.streamCodec = ByteBufCodecs.fromCodecWithRegistries(codec.codec());
        }

        @Override
        public MapCodec<T> codec() {
            return codec;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, T> streamCodec() {
            return streamCodec;
        }
    }


}
