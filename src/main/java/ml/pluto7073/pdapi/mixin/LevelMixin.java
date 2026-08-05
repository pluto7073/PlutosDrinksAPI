package ml.pluto7073.pdapi.mixin;

import ml.pluto7073.pdapi.addition.DrinkAdditionManager;
import ml.pluto7073.pdapi.internal.PDAPILevelExtensions;
import ml.pluto7073.pdapi.specialty.SpecialtyDrinkManager;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

@SuppressWarnings({"RedundantMethodOverride", "AddedMixinMembersNamePattern"})
@Mixin(Level.class)
public class LevelMixin implements PDAPILevelExtensions {

    @Override
    public DrinkAdditionManager getDrinkAdditionManager() {
        return null;
    }

    @Override
    public SpecialtyDrinkManager getSpecialtyDrinkManager() {
        return null;
    }
}
