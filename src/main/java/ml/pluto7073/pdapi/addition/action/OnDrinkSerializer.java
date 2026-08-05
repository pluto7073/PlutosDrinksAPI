package ml.pluto7073.pdapi.addition.action;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public interface OnDrinkSerializer<T extends OnDrinkAction> {

    MapCodec<T> codec();
    StreamCodec<RegistryFriendlyByteBuf, T> streamCodec();

    /**
     * Symbolizes a constant action that is always the same and doesn't take any parameters
     */
    class EmptySerializer<T extends OnDrinkAction> implements OnDrinkSerializer<T> {

        private final T value;
        private final Codec<T> codec;

        public EmptySerializer(T value) {
            this.value = value;
            this.codec = Codec.unit(value);
        }

        @Override
        public Codec<T> codec() {
            return codec;
        }

        @Override
        public T fromNetwork(FriendlyByteBuf buf) {
            return value;
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, T action) {}
    }


}
