package ml.pluto7073.pdapi.addition.action;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

public class ChorusTeleportAction implements OnDrinkAction {

    private final int radius;

    public ChorusTeleportAction(int radius) {
        this.radius = radius;
    }

    @Override
    public void onDrink(ItemStack stack, Level level, LivingEntity user) {
        double d = user.getX();
        double e = user.getY();
        double f = user.getZ();

        for(int i = 0; i < 16; ++i) {
            double g = user.getX() + (user.getRandom().nextDouble() - 0.5) * radius * 2;
            double h = Mth.clamp(user.getY() + (double)(user.getRandom().nextInt(radius * 2) - radius), level.getMinBuildHeight(), level.getMinBuildHeight() + ((ServerLevel)level).getLogicalHeight() - 1);
            double j = user.getZ() + (user.getRandom().nextDouble() - 0.5) * radius * 2;
            if (user.isPassenger()) {
                user.stopRiding();
            }

            Vec3 vec3d = user.position();
            if (user.randomTeleport(g, h, j, true)) {
                level.gameEvent(GameEvent.TELEPORT, vec3d, GameEvent.Context.of(user));
                SoundEvent soundEvent = user instanceof Fox ? SoundEvents.FOX_TELEPORT : SoundEvents.CHORUS_FRUIT_TELEPORT;
                level.playSound(null, d, e, f, soundEvent, SoundSource.PLAYERS, 1.0F, 1.0F);
                user.playSound(soundEvent, 1.0F, 1.0F);
                user.resetFallDistance();
                break;
            }
        }
    }

    @Override
    public OnDrinkSerializer<?> serializer() {
        return OnDrinkSerializers.CHORUS_TELEPORT;
    }

    public static class Serializer implements OnDrinkSerializer<ChorusTeleportAction> {

        public static final MapCodec<ChorusTeleportAction> CODEC = RecordCodecBuilder.mapCodec(instance ->
                instance.group(Codec.INT.fieldOf("maxRadius").forGetter(action -> action.radius))
                        .apply(instance, ChorusTeleportAction::new));
        public static final StreamCodec<RegistryFriendlyByteBuf, ChorusTeleportAction> STREAM_CODEC =
                StreamCodec.of(Serializer::toNetwork, Serializer::fromNetwork);

        @Override
        public MapCodec<ChorusTeleportAction> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ChorusTeleportAction> streamCodec() {
            return STREAM_CODEC;
        }

        public static ChorusTeleportAction fromNetwork(FriendlyByteBuf buf) {
            return new ChorusTeleportAction(buf.readInt());
        }

        public static void toNetwork(FriendlyByteBuf buf, ChorusTeleportAction action) {
            buf.writeInt(action.radius);
        }
    }

}
