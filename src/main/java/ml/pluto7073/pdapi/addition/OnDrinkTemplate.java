package ml.pluto7073.pdapi.addition;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import ml.pluto7073.pdapi.PDAPI;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@FunctionalInterface
public interface OnDrinkTemplate {

    HashMap<ResourceLocation, OnDrinkTemplate> REGISTRY = new HashMap<>();

    OnDrinkTemplate APPLY_STATUS_EFFECT = register("apply_status_effect", (id, onDrinkData) -> {
        ResourceLocation effect = new ResourceLocation(GsonHelper.getAsString(onDrinkData, "effect"));
        int duration = GsonHelper.getAsInt(onDrinkData, "duration");
        int amplifier = GsonHelper.getAsInt(onDrinkData, "amplifier");
        MobEffect statusEffect = BuiltInRegistries.MOB_EFFECT.get(effect);
        assert statusEffect != null;

        return new OnDrink() {
            @Override
            public void onDrink(ItemStack stack, Level level, LivingEntity user) {
                user.addEffect(new MobEffectInstance(statusEffect, duration, amplifier));
            }

            @Override
            public JsonObject toJson() {
                return onDrinkData;
            }
        };
    });

    OnDrinkTemplate DEAL_DAMAGE = register("deal_damage", (id, onDrinkData) -> {
        float amount = GsonHelper.getAsFloat(onDrinkData, "amount");
        ResourceLocation damageSource = new ResourceLocation(GsonHelper.getAsString(onDrinkData, "source"));
        return new OnDrink() {
            @Override
            public void onDrink(ItemStack stack, Level level, LivingEntity user) {
                ResourceKey<DamageType> type = ResourceKey.create(Registries.DAMAGE_TYPE, damageSource);
                user.hurt(user.damageSources().source(type), amount);
            }

            @Override
            public JsonObject toJson() {
                return onDrinkData;
            }
        };
    });

    OnDrinkTemplate RESTORE_HUNGER = register("restore_hunger", (id, onDrinkData) -> {
        int food = GsonHelper.getAsInt(onDrinkData, "food");
        int saturation = GsonHelper.getAsInt(onDrinkData, "saturation");
        return new OnDrink() {
            @Override
            public void onDrink(ItemStack stack, Level level, LivingEntity user) {
                if (!(user instanceof Player player)) return;
                player.getFoodData().eat(food, saturation);
            }

            @Override
            public JsonObject toJson() {
                return onDrinkData;
            }
        };
    });

    OnDrinkTemplate CLEAR_HARMFUL_EFFECTS = register("clear_harmful_effects", (id, onDrinkData) -> new OnDrink() {
        @Override
        public void onDrink(ItemStack stack, Level level, LivingEntity user) {
            Collection<MobEffectInstance> statusEffects = new ArrayList<>(user.getActiveEffects());
            if (statusEffects.isEmpty()) return;
            for (MobEffectInstance instance : statusEffects) {
                if (!instance.getEffect().isBeneficial()) user.removeEffect(instance.getEffect());
            }
        }

        @Override
        public JsonObject toJson() {
            return onDrinkData;
        }
    });

    OnDrinkTemplate CHORUS_TELEPORT = register("chorus_teleport", (id, onDrinkData) -> {
        int radius = GsonHelper.getAsInt(onDrinkData, "maxRadius");
        return new OnDrink() {
            @Override
            public void onDrink(ItemStack stack, Level level, LivingEntity user) {
                double d = user.getX();
                double e = user.getY();
                double f = user.getZ();

                for(int i = 0; i < 16; ++i) {
                    double g = user.getX() + (user.getRandom().nextDouble() - 0.5) * radius * 2;
                    double h = Mth.clamp(user.getY() + (double)(user.getRandom().nextInt(radius * 2) - radius), level.getMinBuildHeight(), level.getMinBuildHeight() + ((ServerLevel)level).getLogicalHeight() - 1);
                    double j = user.getZ() + (user.getRandom().nextDouble() - 0.5) * radius * 2;
                    if (user.isPassenger()) {
                        user.stopRiding();
                    }

                    Vec3 vec3d = user.position();
                    if (user.randomTeleport(g, h, j, true)) {
                        level.gameEvent(GameEvent.TELEPORT, vec3d, GameEvent.Context.of(user));
                        SoundEvent soundEvent = user instanceof Fox ? SoundEvents.FOX_TELEPORT : SoundEvents.CHORUS_FRUIT_TELEPORT;
                        level.playSound(null, d, e, f, soundEvent, SoundSource.PLAYERS, 1.0F, 1.0F);
                        user.playSound(soundEvent, 1.0F, 1.0F);
                        user.resetFallDistance();
                        break;
                    }
                }
            }

            @Override
            public JsonObject toJson() {
                return onDrinkData;
            }
        };
    });

    static OnDrinkTemplate register(ResourceLocation id, OnDrinkTemplate template) {
        REGISTRY.put(id, template);
        return template;
    }

    private static OnDrinkTemplate register(String id, OnDrinkTemplate template) {
        return register(PDAPI.asId(id), template);
    }

    static OnDrinkTemplate get(ResourceLocation id) {
        OnDrinkTemplate template = REGISTRY.get(id);
        if (template == null) throw new IllegalArgumentException("No valid OnDrinkTemplate exists for identifier " + id.toString());
        return template;
    }

    OnDrink parseJson(ResourceLocation id, JsonObject onDrinkData);

}
