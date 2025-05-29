package ml.pluto7073.pdapi.mixin;

import ml.pluto7073.pdapi.item.AbstractCustomizableDrinkItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public class ItemStackMixin {

    @Unique
    private ItemStack pdapi$this() {
        return (ItemStack) (Object) this;
    }

    @Inject(at = @At("HEAD"), method = "getMaxDamage", cancellable = true)
    private void pdapi$ModifyMaxDamage(CallbackInfoReturnable<Integer> cir) {
        if (!(pdapi$this().getItem() instanceof AbstractCustomizableDrinkItem drinkItem)) return;
        double ounces = drinkItem.getTotalVolume(pdapi$this());
        cir.setReturnValue((int) Math.round(ounces * 2.0));
    }

    @Inject(at = @At("HEAD"), method = "isDamageableItem", cancellable = true)
    private void pdapi$IsDamageableItem(CallbackInfoReturnable<Boolean> cir) {
        if (!(pdapi$this().getItem() instanceof AbstractCustomizableDrinkItem)) return;
        cir.setReturnValue(true);
    }

}
