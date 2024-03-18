package ml.pluto7073.pdapi.mixin;

import ml.pluto7073.pdapi.entity.PDTrackedData;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin<
        T extends net.minecraft.entity.LivingEntity,
        M extends net.minecraft.client.render.entity.model.EntityModel<T>> {

    @Inject(at = @At("RETURN"), method = "isShaking", cancellable = true)
    public void pdapi$caffeineShakes(T entity, CallbackInfoReturnable<Boolean> cir) {
        if (!(entity instanceof PlayerEntity playerEntity)) return;
        float caffeine = playerEntity.getDataTracker().get(PDTrackedData.PLAYER_CAFFEINE_AMOUNT);
        float originalCaffeine = playerEntity.getDataTracker().get(PDTrackedData.PLAYER_ORIGINAL_CAFFEINE_AMOUNT);
        boolean shake = false;
        if (caffeine >= 500.0f) {
            shake = true;
        } else if (caffeine >= 50.0F && originalCaffeine >= 500.0F) {
            shake = true;
        }
        cir.setReturnValue(cir.getReturnValue() || shake);
    }

}
