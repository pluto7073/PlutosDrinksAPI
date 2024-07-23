package ml.pluto7073.pdapi.entity.damage;

import ml.pluto7073.pdapi.PDAPI;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.level.Level;

public interface PDDamageTypes {

    ResourceKey<DamageType> CAFFEINE_OVERDOSE = ResourceKey.create(Registries.DAMAGE_TYPE, PDAPI.asId("caffeine_overdose"));

    static DamageSource of(Level world, ResourceKey<DamageType> key) {
        return new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(key));
    }

}
