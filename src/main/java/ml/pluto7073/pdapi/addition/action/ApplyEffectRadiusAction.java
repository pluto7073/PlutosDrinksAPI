package ml.pluto7073.pdapi.addition.action;

import com.google.gson.JsonObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;

public class ApplyEffectRadiusAction implements OnDrinkAction {

    private static final AABB ONE_BLOCK = Block.box(0, 0, 0, 16, 16, 16).bounds();

    private final int radius;
    private final boolean includeDrinker;
    private final MobEffect effect;
    private final int duration;
    private final int amplifier;

    public ApplyEffectRadiusAction(int radius, boolean includeDrinker, MobEffect effect, int duration, int amplifier) {
        this.radius = radius;
        this.includeDrinker = includeDrinker;
        this.effect = effect;
        this.duration = duration;
        this.amplifier = amplifier;
    }

    @Override
    public void onDrink(ItemStack stack, Level level, LivingEntity user) {
        List<LivingEntity> list =
                level.getEntities(user, ONE_BLOCK.inflate(radius), entity -> entity instanceof LivingEntity)
                .stream().map(e -> (LivingEntity) e).toList();

        for (LivingEntity e : list) {
            e.addEffect(new MobEffectInstance(effect, duration, amplifier));
        }

        if (includeDrinker) user.addEffect(new MobEffectInstance(effect, duration, amplifier));
    }

    @Override
    public OnDrinkSerializer<?> serializer() {
        return OnDrinkSerializers.APPLY_EFFECT_RADIUS;
    }

    public static class Serializer implements OnDrinkSerializer<ApplyEffectRadiusAction> {

        @Override
        public ApplyEffectRadiusAction fromJson(JsonObject json) {
            ResourceLocation effectId = new ResourceLocation(GsonHelper.getAsString(json, "effect"));
            MobEffect effect = BuiltInRegistries.MOB_EFFECT.get(effectId);
            if (effect == null) {
                throw new IllegalArgumentException("Effect ID must point to a Mob Effect that actually exists");
            }
            int duration = GsonHelper.getAsInt(json, "duration");
            int amplifier = GsonHelper.getAsInt(json, "amplifier");
            int radius = GsonHelper.getAsInt(json, "radius");
            boolean includeDrinker = GsonHelper.getAsBoolean(json, "includeDrinker");
            return new ApplyEffectRadiusAction(radius, includeDrinker, effect, duration, amplifier);
        }

        @Override
        public void toJson(JsonObject json, ApplyEffectRadiusAction action) {
            json.addProperty("radius", action.radius);
            json.addProperty("includeDrinker", action.includeDrinker);
            ResourceLocation id = BuiltInRegistries.MOB_EFFECT.getResourceKey(action.effect)
                    .orElseThrow(IllegalStateException::new).location();
            json.addProperty("effect", id.toString());
            json.addProperty("duration", action.duration);
            json.addProperty("amplifier", action.amplifier);
        }

        @Override
        public ApplyEffectRadiusAction fromNetwork(FriendlyByteBuf buf) {
            MobEffect effect = BuiltInRegistries.MOB_EFFECT.get(buf.readResourceKey(Registries.MOB_EFFECT));
            int duration = buf.readInt();
            int amplifier = buf.readInt();
            boolean includeDrinker = buf.readBoolean();
            int radius = buf.readInt();
            return new ApplyEffectRadiusAction(radius, includeDrinker, effect, duration, amplifier);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, ApplyEffectRadiusAction action) {
            buf.writeResourceKey(BuiltInRegistries.MOB_EFFECT.getResourceKey(action.effect)
                    .orElseThrow(IllegalStateException::new));
            buf.writeInt(action.duration);
            buf.writeInt(action.amplifier);
            buf.writeBoolean(action.includeDrinker);
            buf.writeInt(action.radius);
        }
    }

}
