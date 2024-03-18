package ml.pluto7073.pdapi.entity.effect;

import ml.pluto7073.pdapi.PDAPI;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class PDStatusEffects {

    public static final StatusEffect CAFFEINE_OVERDOSE = new CaffeineOverdoseEffect(StatusEffectCategory.HARMFUL, 0x0b1428);

    public static void init() {
        Registry.register(Registries.STATUS_EFFECT, PDAPI.asId("caffeine_overdose"), CAFFEINE_OVERDOSE);
    }

}
