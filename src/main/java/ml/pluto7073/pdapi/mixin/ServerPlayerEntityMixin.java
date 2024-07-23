package ml.pluto7073.pdapi.mixin;

import ml.pluto7073.pdapi.DrinkUtil;
import ml.pluto7073.pdapi.crossmodfeatures.CrossModFeatures;
import ml.pluto7073.pdapi.entity.PDTrackedData;
import ml.pluto7073.pdapi.entity.effect.PDMobEffects;
import ml.pluto7073.pdapi.gamerule.PDGameRules;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntityMixin {

    @Inject(at = @At("TAIL"), method = "readAdditionalSaveData")
    public void pdapi$readPlayerCaffeineData(CompoundTag nbt, CallbackInfo ci) {
        CompoundTag caffeineData;
        if (nbt.contains("CoffeeData")) {
            caffeineData = nbt.getCompound("CoffeeData");
        } else if (nbt.contains("CaffeineData")) {
            caffeineData = nbt.getCompound("CaffeineData");
        } else return;

        // Caffeine Content
        if (caffeineData.contains("CaffeineContent")) {
            float caffeine = caffeineData.getFloat("CaffeineContent");
            this.entityData.set(PDTrackedData.PLAYER_CAFFEINE_AMOUNT, caffeine);
        }

        // Original Caffeine Content (for math purposes)
        if (caffeineData.contains("OriginalCaffeineContent")) {
            float originalCaffeine = caffeineData.getFloat("OriginalCaffeineContent");
            this.entityData.set(PDTrackedData.PLAYER_ORIGINAL_CAFFEINE_AMOUNT, originalCaffeine);
        }

        // Ticks Since Last Caffeine Change
        if (caffeineData.contains("TicksSinceLastCaffeine")) {
            int ticks = caffeineData.getInt("TicksSinceLastCaffeine");
            this.entityData.set(PDTrackedData.PLAYER_TICKS_SINCE_LAST_CAFFEINE, ticks);
        }
    }

    @Inject(at = @At("TAIL"), method = "addAdditionalSaveData")
    public void pdapi$writePlayerCaffeineData(CompoundTag nbt, CallbackInfo ci) {
        CompoundTag caffeineData = new CompoundTag();

        // Caffeine Content
        float caffeine = this.entityData.get(PDTrackedData.PLAYER_CAFFEINE_AMOUNT);
        caffeineData.putFloat("CaffeineContent", caffeine);

        // Original Caffeine Content (For math purposes)
        float originalCaffeine = this.entityData.get(PDTrackedData.PLAYER_ORIGINAL_CAFFEINE_AMOUNT);
        caffeineData.putFloat("OriginalCaffeineContent", originalCaffeine);

        // Ticks Since Last Caffeine Change
        int ticks = this.entityData.get(PDTrackedData.PLAYER_TICKS_SINCE_LAST_CAFFEINE);
        caffeineData.putInt("TicksSinceLastCaffeine", ticks);

        nbt.put("CaffeineData", caffeineData);
    }

    @Inject(at = @At("TAIL"), method = "tick")
    public void pdapi$updateCaffeineContent(CallbackInfo ci) {
        int ticks = this.entityData.get(PDTrackedData.PLAYER_TICKS_SINCE_LAST_CAFFEINE);
        ticks++;
        float originalCaffeine = this.entityData.get(PDTrackedData.PLAYER_ORIGINAL_CAFFEINE_AMOUNT);
        float newCaffeine = DrinkUtil.calculateCaffeineDecay(ticks, originalCaffeine);
        this.entityData.set(PDTrackedData.PLAYER_CAFFEINE_AMOUNT, newCaffeine);
        this.entityData.set(PDTrackedData.PLAYER_TICKS_SINCE_LAST_CAFFEINE, ticks);
    }

    @Inject(at = @At("TAIL"), method = "tick")
    public void pdapi$caffeineLevelEffects(CallbackInfo ci) {
        float caffeine = this.entityData.get(PDTrackedData.PLAYER_CAFFEINE_AMOUNT);

        if (caffeine >= 100) {
            this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 600));
        }
        if (caffeine >= 150) {
            this.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 600));
        }
        if (caffeine >= 300) {
            this.addEffect(new MobEffectInstance(MobEffects.HUNGER, 600));
        }
        if (caffeine >= 400) {
            CrossModFeatures.dehydration$AddThirstStatusEffect((ServerPlayer) (Object) this, 600, 0, false);
        }
        if (caffeine >= 450) {
            this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 600, 1));
        }
        if (caffeine >= 500) {
            this.addEffect(new MobEffectInstance(MobEffects.JUMP, 600));
        }
        if (caffeine >= 600) {
            this.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 600, 1));
        }
        if (caffeine >= 700) {
            this.addEffect(new MobEffectInstance(MobEffects.JUMP, 600, 1));
        }
        int lethalCaffeineDose = level().getGameRules().getInt(PDGameRules.LETHAL_CAFFEINE_DOSE);
        boolean overdose = level().getGameRules().getBoolean(PDGameRules.DO_CAFFEINE_OVERDOSE);
        if (overdose && caffeine >= lethalCaffeineDose) {
            this.addEffect(new MobEffectInstance(PDMobEffects.CAFFEINE_OVERDOSE, 20 * 60));
        }
    }

}
