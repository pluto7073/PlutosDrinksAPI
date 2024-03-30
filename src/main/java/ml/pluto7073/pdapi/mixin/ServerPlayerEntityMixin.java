package ml.pluto7073.pdapi.mixin;

import ml.pluto7073.pdapi.DrinkUtil;
import ml.pluto7073.pdapi.crossmodfeatures.CrossModFeatures;
import ml.pluto7073.pdapi.entity.PDTrackedData;
import ml.pluto7073.pdapi.entity.effect.PDStatusEffects;
import ml.pluto7073.pdapi.gamerule.PDGameRules;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
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

        if (caffeine >= 100) {
            this.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 600));
        }
        if (caffeine >= 150) {
            this.addStatusEffect(new StatusEffectInstance(StatusEffects.HASTE, 600));
        }
        if (caffeine >= 300) {
            this.addStatusEffect(new StatusEffectInstance(StatusEffects.HUNGER, 600));
        }
        if (caffeine >= 400) {
            CrossModFeatures.dehydration$AddThirstStatusEffect((ServerPlayerEntity) (Object) this, 600, 0, false);
        }
        if (caffeine >= 450) {
            this.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 600, 1));
        }
        if (caffeine >= 500) {
            this.addStatusEffect(new StatusEffectInstance(StatusEffects.JUMP_BOOST, 600));
        }
        if (caffeine >= 600) {
            this.addStatusEffect(new StatusEffectInstance(StatusEffects.HASTE, 600, 1));
        }
        if (caffeine >= 700) {
            this.addStatusEffect(new StatusEffectInstance(StatusEffects.JUMP_BOOST, 600, 1));
        }
        int lethalCaffeineDose = getWorld().getGameRules().getInt(PDGameRules.LETHAL_CAFFEINE_DOSE);
        boolean overdose = getWorld().getGameRules().getBoolean(PDGameRules.DO_CAFFEINE_OVERDOSE);
        if (overdose && caffeine >= lethalCaffeineDose) {
            this.addStatusEffect(new StatusEffectInstance(PDStatusEffects.CAFFEINE_OVERDOSE, 20 * 60));
        }
    }

}
