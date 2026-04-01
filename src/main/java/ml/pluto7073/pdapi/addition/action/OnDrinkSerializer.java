package ml.pluto7073.pdapi.addition.action;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import ml.pluto7073.pdapi.PDAPI;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Collection;

public interface OnDrinkSerializer<T extends OnDrinkAction> {

    Codec<T> codec();
    T fromNetwork(FriendlyByteBuf buf);
    void toNetwork(FriendlyByteBuf buf, T action);

    /**
     * Symbolizes a constant action that is always the same and doesn't take any parameters
     */
    class EmptySerializer<T extends OnDrinkAction> implements OnDrinkSerializer<T> {

        private final T value;
        private final Codec<T> codec;

        public EmptySerializer(T value) {
            this.value = value;
            this.codec = Codec.unit(value);
        }

        @Override
        public Codec<T> codec() {
            return codec;
        }

        @Override
        public T fromNetwork(FriendlyByteBuf buf) {
            return value;
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, T action) {}
    }


}
