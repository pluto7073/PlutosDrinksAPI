package ml.pluto7073.pdapi.addition.action;

import com.google.gson.JsonObject;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class DealDamageAction implements OnDrinkAction {

    private final float amount;
    private final ResourceKey<DamageType> source;

    public DealDamageAction(float amount, ResourceKey<DamageType> source) {
        this.amount = amount;
        this.source = source;
    }

    @Override
    public void onDrink(ItemStack stack, Level level, LivingEntity user) {
        user.hurt(user.damageSources().source(source), amount);
    }

    @Override
    public OnDrinkSerializer<?> serializer() {
        return OnDrinkSerializers.DEAL_DAMAGE;
    }

    public static class Serializer implements OnDrinkSerializer<DealDamageAction> {

        @Override
        public DealDamageAction fromJson(JsonObject json) {
            ResourceLocation sourceId = new ResourceLocation(GsonHelper.getAsString(json, "source"));
            float amount = GsonHelper.getAsFloat(json, "amount");
            return new DealDamageAction(amount, ResourceKey.create(Registries.DAMAGE_TYPE, sourceId));
        }

        @Override
        public void toJson(JsonObject json, DealDamageAction action) {
            json.addProperty("source", action.source.location().toString());
            json.addProperty("amount", action.amount);
        }

        @Override
        public DealDamageAction fromNetwork(FriendlyByteBuf buf) {
            float amount = buf.readFloat();
            ResourceKey<DamageType> source = buf.readResourceKey(Registries.DAMAGE_TYPE);
            return new DealDamageAction(amount, source);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, DealDamageAction action) {
            buf.writeFloat(action.amount);
            buf.writeResourceKey(action.source);
        }
    }

}
