package ml.pluto7073.pdapi.crossmodfeatures;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.entity.LivingEntity;

public final class CrossModFeatures {

    public static void dehydration$AddThirstStatusEffect(LivingEntity entity, int duration, int amplifier, boolean onlyIfNonExistent) {
        if (FabricLoader.getInstance().isModLoaded("dehydration")) {
            DehydrationFeatures.addThirstEffect(entity, duration, amplifier, onlyIfNonExistent);
        }
    }

}
