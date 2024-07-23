package ml.pluto7073.pdapi.crossmodfeatures;

import net.dehydration.init.EffectInit;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

public class DehydrationFeatures {

    static void addThirstEffect(LivingEntity user, int duration, int amplifier, boolean onlyIfNonExistent) {
        if (user.hasEffect(EffectInit.THIRST)) return;
        user.addEffect(new MobEffectInstance(EffectInit.THIRST, duration, amplifier));
    }

}
