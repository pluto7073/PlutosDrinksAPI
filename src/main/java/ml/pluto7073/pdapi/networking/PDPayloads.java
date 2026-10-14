package ml.pluto7073.pdapi.networking;

import ml.pluto7073.pdapi.PDAPI;
import ml.pluto7073.pdapi.networking.clientbound.SyncAdditionsRegistryPayload;
import ml.pluto7073.pdapi.networking.clientbound.SyncSpecialtyDrinksRegistryPayload;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public class PDPayloads {

    public static final CustomPacketPayload.Type<SyncAdditionsRegistryPayload> SYNC_ADDITIONS_REGISTRY =
            new CustomPacketPayload.Type<>(PDAPI.asId("clientbound/sync_additions_registry"));
    public static final CustomPacketPayload.Type<SyncSpecialtyDrinksRegistryPayload> SYNC_SPECIALTY_DRINKS_REGISTRY =
            new CustomPacketPayload.Type<>(PDAPI.asId("clientbound/sync_specialty_drinks_registry"));

    public static void register() {
        PayloadTypeRegistry.playS2C().register(SYNC_ADDITIONS_REGISTRY, SyncAdditionsRegistryPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(SYNC_SPECIALTY_DRINKS_REGISTRY, SyncSpecialtyDrinksRegistryPayload.CODEC);
    }

}
