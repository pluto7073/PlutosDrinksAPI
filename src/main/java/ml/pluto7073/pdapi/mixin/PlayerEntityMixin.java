package ml.pluto7073.pdapi.mixin;

import ml.pluto7073.pdapi.entity.PDTrackedData;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerEntityMixin extends LivingEntityMixin {

    @Shadow public abstract Abilities getAbilities();

    @Inject(at = @At("TAIL"), method = "defineSynchedData")
    public void pdapi$initCustomDataTrackers(CallbackInfo ci) {
        this.entityData.define(PDTrackedData.PLAYER_CAFFEINE_AMOUNT, 0F);
        this.entityData.define(PDTrackedData.PLAYER_ORIGINAL_CAFFEINE_AMOUNT, 0F);
        this.entityData.define(PDTrackedData.PLAYER_TICKS_SINCE_LAST_CAFFEINE, 0);
    }

}
