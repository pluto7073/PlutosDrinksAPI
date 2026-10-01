package ml.pluto7073.pdapi.mixin.client;

import ml.pluto7073.pdapi.addition.DrinkAdditionManager;
import ml.pluto7073.pdapi.internal.PDAPILevelExtensions;
import ml.pluto7073.pdapi.specialty.SpecialtyDrinkManager;
import net.minecraft.client.multiplayer.ClientPacketListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin implements PDAPILevelExtensions {

    @Unique
    private final SpecialtyDrinkManager pdapi$SpecialtyDrinkManager = new SpecialtyDrinkManager();
    @Unique
    private final DrinkAdditionManager pdapi$DrinkAdditionManager = new DrinkAdditionManager();

    @SuppressWarnings("AddedMixinMembersNamePattern")
    @Override
    public DrinkAdditionManager getDrinkAdditionManager() {
        return pdapi$DrinkAdditionManager;
    }

    @SuppressWarnings("AddedMixinMembersNamePattern")
    @Override
    public SpecialtyDrinkManager getSpecialtyDrinkManager() {
        return pdapi$SpecialtyDrinkManager;
    }

}
