package ml.pluto7073.pdapi.crossmodfeatures;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

public class DehydrationFeatures {

    static void addThirstEffect(LivingEntity user, int duration, int amplifier, boolean onlyIfNonExistent) {
        MobEffect thirst = BuiltInRegistries.MOB_EFFECT.get(new ResourceLocation("dehydration:thirst_effect"));
        if (thirst == null) throw new IllegalStateException("How did this happen? (Apparently the mod \"dehydration\" isn't actually installed");
        if (user.hasEffect(thirst)) return;
        user.addEffect(new MobEffectInstance(thirst, duration, amplifier));
    }

}
