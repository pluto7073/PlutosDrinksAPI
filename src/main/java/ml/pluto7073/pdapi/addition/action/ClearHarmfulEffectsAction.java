package ml.pluto7073.pdapi.addition.action;

import com.google.gson.JsonObject;
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

        @Override
        public ClearHarmfulEffectsAction fromJson(JsonObject json) {
            int limit = -1;
            if (json.has("limit")) {
                limit = GsonHelper.getAsInt(json, "limit");
            }
            return new ClearHarmfulEffectsAction(limit);
        }

        @Override
        public void toJson(JsonObject json, ClearHarmfulEffectsAction action) {
            if (action.limit == -1) return;
            json.addProperty("limit", action.limit);
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
