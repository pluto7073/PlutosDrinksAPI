package ml.pluto7073.pdapi.addition.action;

import com.google.gson.JsonObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ApplyStatusEffectAction implements OnDrinkAction {

    private final MobEffect effect;
    private final int duration, amplifier;

    public ApplyStatusEffectAction(MobEffect effect, int duration, int amplifier) {
        this.effect = effect;
        this.duration = duration;
        this.amplifier = amplifier;
    }

    @Override
    public void onDrink(ItemStack stack, Level level, LivingEntity user) {
        user.addEffect(new MobEffectInstance(effect, duration, amplifier));
    }

    @Override
    public OnDrinkSerializer<?> serializer() {
        return OnDrinkSerializers.APPLY_STATUS_EFFECT;
    }

    public static class Serializer implements OnDrinkSerializer<ApplyStatusEffectAction> {

        @Override
        public ApplyStatusEffectAction fromJson(JsonObject json) {
            ResourceLocation effectId = new ResourceLocation(GsonHelper.getAsString(json, "effect"));
            int duration = GsonHelper.getAsInt(json, "duration");
            int amplifier = GsonHelper.getAsInt(json, "amplifier");
            MobEffect effect = BuiltInRegistries.MOB_EFFECT.get(effectId);
            if (effect == null) {
                throw new IllegalArgumentException("Effect ID must point to a Mob Effect that actually exists");
            }
            return new ApplyStatusEffectAction(effect, duration, amplifier);
        }

        @Override
        public void toJson(JsonObject json, ApplyStatusEffectAction action) {
            ResourceLocation id = BuiltInRegistries.MOB_EFFECT.getKey(action.effect);
            if (id == null) throw new IllegalStateException();
            json.addProperty("effect", id.toString());
            json.addProperty("duration", action.duration);
            json.addProperty("amplifier", action.amplifier);
        }

        @Override
        public ApplyStatusEffectAction fromNetwork(FriendlyByteBuf buf) {
            ResourceLocation id = buf.readResourceLocation();
            MobEffect effect = BuiltInRegistries.MOB_EFFECT.get(id);
            if (effect == null) throw new IllegalStateException();
            int duration = buf.readInt();
            int amplifier = buf.readInt();
            return new ApplyStatusEffectAction(effect, duration, amplifier);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, ApplyStatusEffectAction action) {
            ResourceLocation id = BuiltInRegistries.MOB_EFFECT.getKey(action.effect);
            if (id == null) throw new IllegalStateException();
            buf.writeResourceLocation(id);
            buf.writeInt(action.duration);
            buf.writeInt(action.amplifier);
        }
    }

}
