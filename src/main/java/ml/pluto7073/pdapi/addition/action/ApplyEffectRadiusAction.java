package ml.pluto7073.pdapi.addition.action;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
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
    private final Holder<MobEffect> effect;
    private final int duration;
    private final int amplifier;

    public ApplyEffectRadiusAction(int radius, boolean includeDrinker, Holder<MobEffect> effect, int duration, int amplifier) {
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

        public static MapCodec<ApplyEffectRadiusAction> CODEC = RecordCodecBuilder.mapCodec(instance ->
                instance.group(Codec.INT.fieldOf("radius").forGetter(action -> action.radius),
                                Codec.BOOL.fieldOf("includeDrinker").forGetter(action -> action.includeDrinker),
                                BuiltInRegistries.MOB_EFFECT.holderByNameCodec().fieldOf("effect").forGetter(action -> action.effect),
                                Codec.INT.fieldOf("duration").forGetter(action -> action.duration),
                                Codec.INT.fieldOf("amplifier").forGetter(action -> action.amplifier))
                        .apply(instance, ApplyEffectRadiusAction::new));
        public static StreamCodec<RegistryFriendlyByteBuf, ApplyEffectRadiusAction> STREAM_CODEC =
                StreamCodec.of(Serializer::toNetwork, Serializer::fromNetwork);

        @Override
        public MapCodec<ApplyEffectRadiusAction> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ApplyEffectRadiusAction> streamCodec() {
            return STREAM_CODEC;
        }

        public static ApplyEffectRadiusAction fromNetwork(FriendlyByteBuf buf) {
            Holder<MobEffect> effect = BuiltInRegistries.MOB_EFFECT.getHolder(buf.readResourceKey(Registries.MOB_EFFECT)).orElseThrow();
            int duration = buf.readInt();
            int amplifier = buf.readInt();
            boolean includeDrinker = buf.readBoolean();
            int radius = buf.readInt();
            return new ApplyEffectRadiusAction(radius, includeDrinker, effect, duration, amplifier);
        }

        public static void toNetwork(FriendlyByteBuf buf, ApplyEffectRadiusAction action) {
            buf.writeResourceKey(BuiltInRegistries.MOB_EFFECT.getResourceKey(action.effect.value())
                    .orElseThrow(IllegalStateException::new));
            buf.writeInt(action.duration);
            buf.writeInt(action.amplifier);
            buf.writeBoolean(action.includeDrinker);
            buf.writeInt(action.radius);
        }
    }

}
