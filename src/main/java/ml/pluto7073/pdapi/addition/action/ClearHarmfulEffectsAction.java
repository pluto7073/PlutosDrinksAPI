package ml.pluto7073.pdapi.addition.action;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
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
            if (effect.getEffect().isBeneficial()) continue;
            limit--;
            user.removeEffect(effect.getEffect());
        }
    }

    @Override
    public OnDrinkSerializer<?> serializer() {
        return OnDrinkSerializers.CLEAR_HARMFUL_EFFECTS;
    }

    public static class Serializer implements OnDrinkSerializer<ClearHarmfulEffectsAction> {

        public static final Codec<ClearHarmfulEffectsAction> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(Codec.INT.fieldOf("limit").orElse(-1).forGetter(action -> action.limit))
                        .apply(instance, ClearHarmfulEffectsAction::new));

        @Override
        public Codec<ClearHarmfulEffectsAction> codec() {
            return CODEC;
        }

        @Override
        public ClearHarmfulEffectsAction fromNetwork(FriendlyByteBuf buf) {
            return new ClearHarmfulEffectsAction(buf.readInt());
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, ClearHarmfulEffectsAction action) {
            buf.writeInt(action.limit);
        }

    }

}
