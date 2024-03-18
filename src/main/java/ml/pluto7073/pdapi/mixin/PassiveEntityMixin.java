package ml.pluto7073.pdapi.mixin;

import net.minecraft.entity.passive.PassiveEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(PassiveEntity.class)
public abstract class PassiveEntityMixin extends LivingEntityMixin {
    @Shadow public abstract boolean isBaby();
}
