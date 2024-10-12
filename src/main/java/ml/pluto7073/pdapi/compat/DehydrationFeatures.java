package ml.pluto7073.pdapi.compat;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

public class DehydrationFeatures {

    static void addThirstEffect(LivingEntity user, int duration, int amplifier, boolean onlyIfNonExistent) {
        Holder<MobEffect> thirst = BuiltInRegistries.MOB_EFFECT.getHolder(ResourceLocation.parse("dehydration:thirst_effect")).orElseThrow();
        if (user.hasEffect(thirst)) return;
        user.addEffect(new MobEffectInstance(thirst, duration, amplifier));
    }

}
