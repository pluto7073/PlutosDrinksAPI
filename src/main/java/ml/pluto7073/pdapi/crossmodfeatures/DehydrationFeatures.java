package ml.pluto7073.pdapi.crossmodfeatures;

import net.dehydration.init.EffectInit;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;

public class DehydrationFeatures {

    static void addThirstEffect(LivingEntity user, int duration, int amplifier, boolean onlyIfNonExistent) {
        if (user.hasStatusEffect(EffectInit.THIRST)) return;
        user.addStatusEffect(new StatusEffectInstance(EffectInit.THIRST, duration, amplifier));
    }

}
