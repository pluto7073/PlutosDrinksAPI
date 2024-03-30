package ml.pluto7073.pdapi.entity.effect;

import ml.pluto7073.pdapi.entity.damage.PDDamageTypes;
import ml.pluto7073.pdapi.gamerule.PDGameRules;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class CaffeineOverdoseEffect extends StatusEffect {

    protected CaffeineOverdoseEffect(StatusEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        super.applyUpdateEffect(entity, amplifier);
        entity.damage(PDDamageTypes.of(entity.getWorld(), PDDamageTypes.CAFFEINE_OVERDOSE), (float) entity.getWorld().getGameRules().get(PDGameRules.CAFFEINE_DAMAGE_MODIFIER).get());
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }
}
