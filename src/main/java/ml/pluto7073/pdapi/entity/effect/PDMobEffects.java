package ml.pluto7073.pdapi.entity.effect;

import ml.pluto7073.pdapi.PDAPI;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class PDMobEffects {

    public static final MobEffect CAFFEINE_OVERDOSE = new CaffeineOverdoseEffect(MobEffectCategory.HARMFUL, 0x0b1428);

    public static void init() {
        Registry.register(BuiltInRegistries.MOB_EFFECT, PDAPI.asId("caffeine_overdose"), CAFFEINE_OVERDOSE);
    }

}
