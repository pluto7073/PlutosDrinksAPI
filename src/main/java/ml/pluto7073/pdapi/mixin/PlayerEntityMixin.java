package ml.pluto7073.pdapi.mixin;

import ml.pluto7073.pdapi.entity.PDTrackedData;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntityMixin {

    @Shadow public abstract PlayerAbilities getAbilities();

    @Inject(at = @At("TAIL"), method = "initDataTracker")
    public void pdapi$initCustomDataTrackers(CallbackInfo ci) {
        this.dataTracker.startTracking(PDTrackedData.PLAYER_CAFFEINE_AMOUNT, 0F);
        this.dataTracker.startTracking(PDTrackedData.PLAYER_ORIGINAL_CAFFEINE_AMOUNT, 0F);
        this.dataTracker.startTracking(PDTrackedData.PLAYER_TICKS_SINCE_LAST_CAFFEINE, 0);
    }

}
