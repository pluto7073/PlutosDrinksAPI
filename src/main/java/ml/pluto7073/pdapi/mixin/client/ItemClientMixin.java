package ml.pluto7073.pdapi.mixin.client;

import ml.pluto7073.pdapi.component.PDComponents;
import ml.pluto7073.pdapi.item.AbstractCustomizableDrinkItem;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class ItemClientMixin {

    @Inject(at = @At("HEAD"), method = "getBarWidth", cancellable = true)
    private void pdapi$InjectGetBarWidth(ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        if (((Item) (Object) this) instanceof AbstractCustomizableDrinkItem item) {
            cir.setReturnValue((int) Math.round(13.0f - stack.getOrDefault(PDComponents.SIPPED, 0.0) * 13.0f / item.getTotalVolume(stack, Minecraft.getInstance().level.registryAccess())));
        }
    }

}
