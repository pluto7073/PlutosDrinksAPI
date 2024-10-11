package ml.pluto7073.pdapi.addition.action;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public interface OnDrinkSerializer<T extends OnDrinkAction> {

    MapCodec<T> codec();
    StreamCodec<RegistryFriendlyByteBuf, T> streamCodec();

}
