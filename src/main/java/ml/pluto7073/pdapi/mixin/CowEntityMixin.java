package ml.pluto7073.pdapi.mixin;

import ml.pluto7073.pdapi.item.PDItems;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Cow.class)
public abstract class CowEntityMixin extends PassiveEntityMixin {

    @Inject(at = @At("HEAD"), method = "mobInteract", cancellable = true)
    public void pdapi$collectMilkBottle(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.is(Items.GLASS_BOTTLE) && !isBaby()) {
            player.playSound(SoundEvents.COW_MILK, 1.0F, 1.0F);
            ItemStack itemStack2 = ItemUtils.createFilledResult(stack, player, PDItems.MILK_BOTTLE.getDefaultInstance());
            player.setItemInHand(hand, itemStack2);
            cir.setReturnValue(InteractionResult.sidedSuccess(level().isClientSide));
        }
    }


}
