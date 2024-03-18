package ml.pluto7073.pdapi.entity.damage;

import ml.pluto7073.pdapi.PDAPI;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public interface PDDamageTypes {

    RegistryKey<DamageType> CAFFEINE_OVERDOSE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, PDAPI.asId("caffeine_overdose"));

    static DamageSource of(World world, RegistryKey<DamageType> key) {
        return new DamageSource(world.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(key));
    }

}
