package ml.pluto7073.pdapi.addition.action;

import com.mojang.serialization.Codec;
import ml.pluto7073.pdapi.PDRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public interface OnDrinkAction {

    Codec<OnDrinkAction> CODEC = PDRegistries.ON_DRINK_SERIALIZER.byNameCodec().dispatch(OnDrinkAction::serializer, OnDrinkSerializer::codec);
    StreamCodec<RegistryFriendlyByteBuf, OnDrinkAction> STREAM_CODEC = ByteBufCodecs.registry(PDRegistries.ON_DRINK_SERIALIZER_KEY)
            .dispatch(OnDrinkAction::serializer, OnDrinkSerializer::streamCodec);

    void onDrink(ItemStack stack, Level level, LivingEntity user);
    OnDrinkSerializer<?> serializer();

}
