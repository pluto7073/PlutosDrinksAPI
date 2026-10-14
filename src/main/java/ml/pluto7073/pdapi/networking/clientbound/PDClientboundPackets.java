package ml.pluto7073.pdapi.networking.clientbound;

import ml.pluto7073.pdapi.addition.DrinkAdditionManager;
import ml.pluto7073.pdapi.networking.PDPayloads;
import ml.pluto7073.pdapi.specialty.SpecialtyDrinkManager;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.client.Minecraft;

public class PDClientboundPackets {

    public static void registerReceivers() {
        ClientPlayNetworking.registerGlobalReceiver(PDPayloads.SYNC_ADDITIONS_REGISTRY, (payload, ctx) -> {
            if (Minecraft.getInstance().level == null) return;
            DrinkAdditionManager manager = Minecraft.getInstance().level.getDrinkAdditionManager();
            manager.resetRegistry();
            payload.registry().forEach(manager::register);
        });

        ClientPlayNetworking.registerGlobalReceiver(PDPayloads.SYNC_SPECIALTY_DRINKS_REGISTRY, (payload, ctx) -> {
            if (Minecraft.getInstance().level == null) return;
            SpecialtyDrinkManager manager = Minecraft.getInstance().level.getSpecialtyDrinkManager();
            manager.reset();
            payload.registry().forEach(manager::register);
        });
    }

}
