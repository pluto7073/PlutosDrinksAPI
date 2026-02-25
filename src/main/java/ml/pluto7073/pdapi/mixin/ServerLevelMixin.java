package ml.pluto7073.pdapi.mixin;

import ml.pluto7073.pdapi.PDAPI;
import ml.pluto7073.pdapi.addition.DrinkAdditionManager;
import ml.pluto7073.pdapi.internal.PDAPILevelExtensions;
import ml.pluto7073.pdapi.specialty.SpecialtyDrinkManager;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ServerLevel.class)
public class ServerLevelMixin implements PDAPILevelExtensions {

    @SuppressWarnings("AddedMixinMembersNamePattern")
    @Override
    public DrinkAdditionManager getDrinkAdditionManager() {
        return PDAPI.SERVER_ADDITION_MANAGER;
    }

    @SuppressWarnings("AddedMixinMembersNamePattern")
    @Override
    public SpecialtyDrinkManager getSpecialtyDrinkManager() {
        return PDAPI.SERVER_SPECIALITY_DRINK_MANAGER;
    }
}
