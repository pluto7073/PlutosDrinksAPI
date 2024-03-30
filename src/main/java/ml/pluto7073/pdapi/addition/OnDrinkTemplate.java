package ml.pluto7073.pdapi.addition;

import com.google.gson.JsonObject;
import ml.pluto7073.pdapi.PDAPI;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.event.GameEvent;

import java.util.Collection;
import java.util.HashMap;

@FunctionalInterface
public interface OnDrinkTemplate {

    HashMap<Identifier, OnDrinkTemplate> REGISTRY = new HashMap<>();

    OnDrinkTemplate EMPTY = register("empty", (id, onDrinkData) -> (stack, world, user) -> {});

    OnDrinkTemplate APPLY_STATUS_EFFECT = register("apply_status_effect", (id, onDrinkData) -> {
        Identifier effect = new Identifier(JsonHelper.getString(onDrinkData, "effect"));
        int duration = JsonHelper.getInt(onDrinkData, "duration");
        int amplifier = JsonHelper.getInt(onDrinkData, "amplifier");
        StatusEffect statusEffect = Registries.STATUS_EFFECT.get(effect);
        assert statusEffect != null;

        return (stack, world, user) -> user.addStatusEffect(new StatusEffectInstance(statusEffect, duration, amplifier));
    });

    OnDrinkTemplate DEAL_DAMAGE = register("deal_damage", (id, onDrinkData) -> {
        float amount = JsonHelper.getFloat(onDrinkData, "amount");
        Identifier damageSource = new Identifier(JsonHelper.getString(onDrinkData, "source"));
        return (stack, world, user) -> {
            RegistryKey<DamageType> type = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, damageSource);
            user.damage(user.getDamageSources().create(type), amount);
        };
    });

    OnDrinkTemplate RESTORE_HUNGER = register("restore_hunger", (id, onDrinkData) -> {
        int food = JsonHelper.getInt(onDrinkData, "food");
        int saturation = JsonHelper.getInt(onDrinkData, "saturation");
        return (stack, world, user) -> {
            if (!(user instanceof PlayerEntity player)) return;
            player.getHungerManager().add(food, saturation);
        };
    });

    OnDrinkTemplate CLEAR_HARMFUL_EFFECTS = register("clear_harmful_effects", (id, onDrinkData) -> (stack, world, user) -> {
        Collection<StatusEffectInstance> statusEffects = user.getStatusEffects();
        for (StatusEffectInstance instance : statusEffects) {
            if (!instance.getEffectType().isBeneficial()) user.removeStatusEffect(instance.getEffectType());
        }
    });

    OnDrinkTemplate CHORUS_TELEPORT = register("chorus_teleport", (id, onDrinkData) -> {
        int radius = JsonHelper.getInt(onDrinkData, "maxRadius");
        return (stack, world, user) -> {
            double d = user.getX();
            double e = user.getY();
            double f = user.getZ();

            for(int i = 0; i < 16; ++i) {
                double g = user.getX() + (user.getRandom().nextDouble() - 0.5) * radius * 2;
                double h = MathHelper.clamp(user.getY() + (double)(user.getRandom().nextInt(radius * 2) - radius), world.getBottomY(), world.getBottomY() + ((ServerWorld)world).getLogicalHeight() - 1);
                double j = user.getZ() + (user.getRandom().nextDouble() - 0.5) * radius * 2;
                if (user.hasVehicle()) {
                    user.stopRiding();
                }

                Vec3d vec3d = user.getPos();
                if (user.teleport(g, h, j, true)) {
                    world.emitGameEvent(GameEvent.TELEPORT, vec3d, GameEvent.Emitter.of(user));
                    SoundEvent soundEvent = user instanceof FoxEntity ? SoundEvents.ENTITY_FOX_TELEPORT : SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT;
                    world.playSound(null, d, e, f, soundEvent, SoundCategory.PLAYERS, 1.0F, 1.0F);
                    user.playSound(soundEvent, 1.0F, 1.0F);
                    user.onLanding();
                    break;
                }
            }
        };
    });

    static OnDrinkTemplate register(Identifier id, OnDrinkTemplate template) {
        REGISTRY.put(id, template);
        return template;
    }

    private static OnDrinkTemplate register(String id, OnDrinkTemplate template) {
        return register(PDAPI.asId(id), template);
    }

    static OnDrinkTemplate get(Identifier id) {
        OnDrinkTemplate template = REGISTRY.get(id);
        if (template == null) throw new IllegalArgumentException("No valid OnDrinkTemplate exists for identifier " + id.toString());
        return template;
    }

    OnDrink parseJson(Identifier id, JsonObject onDrinkData);

}
