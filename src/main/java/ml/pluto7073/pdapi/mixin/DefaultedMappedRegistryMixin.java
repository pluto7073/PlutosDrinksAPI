package ml.pluto7073.pdapi.mixin;

import ml.pluto7073.pdapi.util.PseudoDataFixerRegistry;
import net.minecraft.core.DefaultedMappedRegistry;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(DefaultedMappedRegistry.class)
public class DefaultedMappedRegistryMixin {

    @ModifyVariable(
            at = @At("HEAD"),
            method = "get",
            ordinal = 0,
            argsOnly = true
    )
    private ResourceLocation pdapi$FixMissingIDs(ResourceLocation id) {
        if (id != null && PseudoDataFixerRegistry.shouldFix(id)) {
            return PseudoDataFixerRegistry.getReplacement(id);
        }
        return id;
    }

}
