package ml.pluto7073.pdapi.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import ml.pluto7073.pdapi.item.LevelAccessTooltipProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

/**
 * See {@link LevelAccessTooltipProvider}
 */
@Mixin(ItemStack.class)
public class ItemStackMixin {

    @WrapOperation(method = "getTooltipLines", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;appendHoverText(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/Item$TooltipContext;Ljava/util/List;Lnet/minecraft/world/item/TooltipFlag;)V"))
    private void pdapi$GiveLevelAccessToTooltip(Item instance, ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag, Operation<Void> original, @Local(argsOnly = true) @Nullable Player player) {
        if (instance instanceof LevelAccessTooltipProvider provider) {
            provider.appendHoverText(stack, player == null ? null : player.level(), tooltipComponents, tooltipFlag);
        } else {
            original.call(instance, stack, context, tooltipComponents, tooltipFlag);
        }
    }

}
