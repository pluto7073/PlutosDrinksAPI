package ml.pluto7073.pdapi.mixin;

import ml.pluto7073.pdapi.internal.PDAPILevelExtensions;
import ml.pluto7073.pdapi.internal.WrappedTooltipContext;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.TooltipContext.class)
public interface TooltipContextMixin extends PDAPILevelExtensions {

    @Inject(method = "of(Lnet/minecraft/world/level/Level;)Lnet/minecraft/world/item/Item$TooltipContext;", at = @At("RETURN"), cancellable = true)
    private static void pdapi$WrapTooltipContext(Level level, CallbackInfoReturnable<Item.TooltipContext> cir) {
        if (level == null) return;
        cir.setReturnValue(new WrappedTooltipContext(level, cir.getReturnValue()));
    }

}
