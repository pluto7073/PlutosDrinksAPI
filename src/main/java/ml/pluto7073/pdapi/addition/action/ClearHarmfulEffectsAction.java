package ml.pluto7073.pdapi.addition.action;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ClearHarmfulEffectsAction implements OnDrinkAction {

    private final int limit;

    public ClearHarmfulEffectsAction(int limit) {
        this.limit = limit;
    }

    @Override
    public void onDrink(ItemStack stack, Level level, LivingEntity user) {
        int limit = this.limit;

        if (limit == -1) limit = user.getActiveEffects().size();
        for (MobEffectInstance effect : user.getActiveEffects()) {
            if (effect.getEffect().value().isBeneficial()) continue;
            limit--;
            user.removeEffect(effect.getEffect());
        }
    }

    @Override
    public OnDrinkSerializer<?> serializer() {
        return OnDrinkSerializers.CLEAR_HARMFUL_EFFECTS;
    }

    public static class Serializer implements OnDrinkSerializer<ClearHarmfulEffectsAction> {

        public static final MapCodec<ClearHarmfulEffectsAction> CODEC = RecordCodecBuilder.mapCodec(instance ->
                instance.group(Codec.INT.fieldOf("limit").orElse(-1).forGetter(action -> action.limit))
                        .apply(instance, ClearHarmfulEffectsAction::new));
        public static final StreamCodec<RegistryFriendlyByteBuf, ClearHarmfulEffectsAction> STREAM_CODEC =
                StreamCodec.of(Serializer::toNetwork, Serializer::fromNetwork);

        @Override
        public MapCodec<ClearHarmfulEffectsAction> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ClearHarmfulEffectsAction> streamCodec() {
            return STREAM_CODEC;
        }

        public static ClearHarmfulEffectsAction fromNetwork(FriendlyByteBuf buf) {
            return new ClearHarmfulEffectsAction(buf.readInt());
        }

        public static void toNetwork(FriendlyByteBuf buf, ClearHarmfulEffectsAction action) {
            buf.writeInt(action.limit);
        }

    }

}
