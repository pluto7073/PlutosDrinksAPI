package ml.pluto7073.pdapi.mixin;

import net.minecraft.world.entity.AgeableMob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AgeableMob.class)
public abstract class PassiveEntityMixin extends LivingEntityMixin {
    @Shadow public abstract boolean isBaby();
}
