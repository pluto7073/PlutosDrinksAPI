package ml.pluto7073.pdapi.mixin;

import ml.pluto7073.pdapi.item.PDItems;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CowEntity.class)
public abstract class CowEntityMixin extends PassiveEntityMixin{

    @Inject(at = @At("HEAD"), method = "interactMob", cancellable = true)
    public void pdapi$collectMilkBottle(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        ItemStack stack = player.getStackInHand(hand);
        if (stack.isOf(Items.GLASS_BOTTLE) && !isBaby()) {
            player.playSound(SoundEvents.ENTITY_COW_MILK, 1.0F, 1.0F);
            ItemStack itemStack2 = ItemUsage.exchangeStack(stack, player, PDItems.MILK_BOTTLE.getDefaultStack());
            player.setStackInHand(hand, itemStack2);
            cir.setReturnValue(ActionResult.success(getWorld().isClient));
        }
    }


}
