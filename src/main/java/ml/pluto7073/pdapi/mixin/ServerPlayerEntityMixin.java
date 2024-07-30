package ml.pluto7073.pdapi.mixin;

import ml.pluto7073.pdapi.addition.chemicals.ConsumableChemicalRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntityMixin {

    @Inject(at = @At("TAIL"), method = "readAdditionalSaveData")
    public void pdapi$readPlayerChemicalData(CompoundTag nbt, CallbackInfo ci) {
        ConsumableChemicalRegistry.forEach(handler -> handler.loadFromTag(this.entityData, nbt));
    }

    @Inject(at = @At("TAIL"), method = "addAdditionalSaveData")
    public void pdapi$writePlayerChemicalData(CompoundTag nbt, CallbackInfo ci) {
        ConsumableChemicalRegistry.forEach(handler -> handler.saveToTag(this.entityData, nbt));
    }

    @Inject(at = @At("TAIL"), method = "tick")
    public void pdapi$tickChemicals(CallbackInfo ci) {
        ConsumableChemicalRegistry.forEach(handler -> handler.tickPlayer((Player) (Object) this));
    }

    @Inject(at = @At("TAIL"), method = "tick")
    public void pdapi$applyChemicalEffects(CallbackInfo ci) {
        ConsumableChemicalRegistry.forEach(handler -> {
            Player player = (ServerPlayer) (Object) this;
            float amount = handler.get(player);
            handler.getEffectsForAmount(amount, player).forEach(this::addEffect);
        });
    }

}
