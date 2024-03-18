package ml.pluto7073.pdapi.mixin;

import ml.pluto7073.pdapi.DrinkUtil;
import ml.pluto7073.pdapi.entity.PDTrackedData;
import ml.pluto7073.pdapi.entity.effect.PDStatusEffects;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntityMixin {

    @Inject(at = @At("TAIL"), method = "readCustomDataFromNbt")
    public void pdapi$readPlayerCaffeineData(NbtCompound nbt, CallbackInfo ci) {
        NbtCompound caffeineData;
        if (nbt.contains("CoffeeData")) {
            caffeineData = nbt.getCompound("CoffeeData");
        } else if (nbt.contains("CaffeineData")) {
            caffeineData = nbt.getCompound("CaffeineData");
        } else return;

        // Caffeine Content
        if (caffeineData.contains("CaffeineContent")) {
            float caffeine = caffeineData.getFloat("CaffeineContent");
            this.dataTracker.set(PDTrackedData.PLAYER_CAFFEINE_AMOUNT, caffeine);
        }

        // Original Caffeine Content (for math purposes)
        if (caffeineData.contains("OriginalCaffeineContent")) {
            float originalCaffeine = caffeineData.getFloat("OriginalCaffeineContent");
            this.dataTracker.set(PDTrackedData.PLAYER_ORIGINAL_CAFFEINE_AMOUNT, originalCaffeine);
        }

        // Ticks Since Last Caffeine Change
        if (caffeineData.contains("TicksSinceLastCaffeine")) {
            int ticks = caffeineData.getInt("TicksSinceLastCaffeine");
            this.dataTracker.set(PDTrackedData.PLAYER_TICKS_SINCE_LAST_CAFFEINE, ticks);
        }
    }

    @Inject(at = @At("TAIL"), method = "writeCustomDataToNbt")
    public void pdapi$writePlayerCaffeineData(NbtCompound nbt, CallbackInfo ci) {
        NbtCompound caffeineData = new NbtCompound();

        // Caffeine Content
        float caffeine = this.dataTracker.get(PDTrackedData.PLAYER_CAFFEINE_AMOUNT);
        caffeineData.putFloat("CaffeineContent", caffeine);

        // Original Caffeine Content (For math purposes)
        float originalCaffeine = this.dataTracker.get(PDTrackedData.PLAYER_ORIGINAL_CAFFEINE_AMOUNT);
        caffeineData.putFloat("OriginalCaffeineContent", originalCaffeine);

        // Ticks Since Last Caffeine Change
        int ticks = this.dataTracker.get(PDTrackedData.PLAYER_TICKS_SINCE_LAST_CAFFEINE);
        caffeineData.putInt("TicksSinceLastCaffeine", ticks);

        nbt.put("CaffeineData", caffeineData);
    }

    @Inject(at = @At("TAIL"), method = "tick")
    public void pdapi$updateCaffeineContent(CallbackInfo ci) {
        int ticks = this.dataTracker.get(PDTrackedData.PLAYER_TICKS_SINCE_LAST_CAFFEINE);
        ticks++;
        float originalCaffeine = this.dataTracker.get(PDTrackedData.PLAYER_ORIGINAL_CAFFEINE_AMOUNT);
        float newCaffeine = DrinkUtil.calculateCaffeineDecay(ticks, originalCaffeine);
        this.dataTracker.set(PDTrackedData.PLAYER_CAFFEINE_AMOUNT, newCaffeine);
        this.dataTracker.set(PDTrackedData.PLAYER_TICKS_SINCE_LAST_CAFFEINE, ticks);
    }

    @Inject(at = @At("TAIL"), method = "tick")
    public void pdapi$caffeineLevelEffects(CallbackInfo ci) {
        float caffeine = this.dataTracker.get(PDTrackedData.PLAYER_CAFFEINE_AMOUNT);
        if (caffeine >= 3000.0F && !getAbilities().creativeMode && !((ServerPlayerEntity) (Object) this).hasStatusEffect(PDStatusEffects.CAFFEINE_OVERDOSE)) {
            this.addStatusEffect(new StatusEffectInstance(PDStatusEffects.CAFFEINE_OVERDOSE, 20 * 60));
        }
    }

}
