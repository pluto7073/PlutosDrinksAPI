package ml.pluto7073.pdapi.mixin.client;

import ml.pluto7073.pdapi.addition.DrinkAdditionManager;
import ml.pluto7073.pdapi.internal.PDAPILevelExtensions;
import ml.pluto7073.pdapi.specialty.SpecialtyDrinkManager;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ClientLevel.class)
public class ClientLevelMixin implements PDAPILevelExtensions {

    @Shadow
    @Final
    private ClientPacketListener connection;

    @SuppressWarnings("AddedMixinMembersNamePattern")
    @Override
    public DrinkAdditionManager getDrinkAdditionManager() {
        return connection.getDrinkAdditionManager();
    }

    @SuppressWarnings("AddedMixinMembersNamePattern")
    @Override
    public SpecialtyDrinkManager getSpecialtyDrinkManager() {
        return connection.getSpecialtyDrinkManager();
    }
}
