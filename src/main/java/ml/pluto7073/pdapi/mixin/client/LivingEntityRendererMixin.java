package ml.pluto7073.pdapi.mixin.client;

import ml.pluto7073.pdapi.addition.chemicals.CaffeineHandler;
import ml.pluto7073.pdapi.config.PDClientConfig;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin<
        T extends LivingEntity,
        M extends EntityModel<T>> {

    @Inject(at = @At("RETURN"), method = "isShaking", cancellable = true)
    public void pdapi$caffeineShakes(T entity, CallbackInfoReturnable<Boolean> cir) {
        if (!(entity instanceof Player playerEntity)
            || !PDClientConfig.INSTANCE.doCaffeineShake) return;
        float caffeine = CaffeineHandler.INSTANCE.get(playerEntity);
        if (caffeine >= PDClientConfig.INSTANCE.caffeineShakeThreshold) {
            cir.setReturnValue(true);
        }
    }

}
