package ml.pluto7073.pdapi.entity.effect;

import ml.pluto7073.pdapi.entity.damage.PDDamageTypes;
import ml.pluto7073.pdapi.gamerule.PDGameRules;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class CaffeineOverdoseEffect extends MobEffect {

    protected CaffeineOverdoseEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        super.applyEffectTick(entity, amplifier);
        entity.hurt(PDDamageTypes.of(entity.level(), PDDamageTypes.CAFFEINE_OVERDOSE), (float) entity.level().getGameRules().getRule(PDGameRules.CAFFEINE_DAMAGE_MODIFIER).get());
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }
}
