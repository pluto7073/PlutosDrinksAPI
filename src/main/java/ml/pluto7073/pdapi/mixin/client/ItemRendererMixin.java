package ml.pluto7073.pdapi.mixin.client;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.blaze3d.vertex.PoseStack;
import ml.pluto7073.pdapi.item.PDItems;
import ml.pluto7073.pdapi.specialty.SpecialtyDrink;
import ml.pluto7073.pdapi.specialty.SpecialtyDrinkManager;
import ml.pluto7073.pdapi.util.DrinkUtil;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {

    @Shadow public abstract BakedModel getModel(ItemStack stack, @Nullable Level world, @Nullable LivingEntity entity, int seed);

    @WrapMethod(method = "render")
    private void pdapi$ReplaceModelAndStack(ItemStack stack, ItemDisplayContext modelTransformationMode, boolean leftHanded, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay, BakedModel model, Operation<Void> original) {
        SpecialtyDrink drink = DrinkUtil.getSpecialDrink(stack);
        if (!stack.is(PDItems.SPECIALTY_DRINK) || drink == SpecialtyDrinkManager.EMPTY) {
            original.call(stack, modelTransformationMode, leftHanded, matrices, vertexConsumers, light, overlay, model);
        } else {
            ItemStack newStack = drink.getBaseItem(stack);
            BakedModel baseDrinkModel = getModel(newStack, null, null, 0);
            original.call(drink.color() == -1 ? newStack : stack, modelTransformationMode, leftHanded, matrices, vertexConsumers, light, overlay, baseDrinkModel);
        }
    }

}
