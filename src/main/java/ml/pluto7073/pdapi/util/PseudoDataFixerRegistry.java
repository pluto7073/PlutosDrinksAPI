package ml.pluto7073.pdapi.util;

import ml.pluto7073.pdapi.PDAPI;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class PseudoDataFixerRegistry {

    private static final Map<ResourceLocation, ResourceLocation> REGISTRY = new HashMap<>();

    public static boolean shouldFix(ResourceLocation id) {
        return REGISTRY.containsKey(id);
    }

    public static void register(ResourceLocation oldId, ResourceLocation newId) {
        REGISTRY.put(oldId, newId);
    }

    public static ResourceLocation getReplacement(ResourceLocation id) {
        return REGISTRY.getOrDefault(id, new ResourceLocation("empty"));
    }

    static {
        REGISTRY.put(new ResourceLocation("plutoscoffee:milk_bottle"), PDAPI.asId("milk_bottle"));
        REGISTRY.put(new ResourceLocation("plutoscoffee:coffee_workstation"), PDAPI.asId("drink_workstation"));
    }

}
