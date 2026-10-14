package ml.pluto7073.pdapi.networking.clientbound;

import ml.pluto7073.pdapi.networking.PDPayloads;
import ml.pluto7073.pdapi.specialty.SpecialtyDrink;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public record SyncSpecialtyDrinksRegistryPayload(Map<ResourceLocation, SpecialtyDrink> registry) implements CustomPacketPayload {

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncSpecialtyDrinksRegistryPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.map(HashMap::new, ResourceLocation.STREAM_CODEC, SpecialtyDrink.STREAM_CODEC),
            SyncSpecialtyDrinksRegistryPayload::registry, SyncSpecialtyDrinksRegistryPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return PDPayloads.SYNC_SPECIALTY_DRINKS_REGISTRY;
    }
}
