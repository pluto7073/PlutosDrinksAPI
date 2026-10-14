package ml.pluto7073.pdapi.networking.clientbound;

import ml.pluto7073.pdapi.addition.DrinkAddition;
import ml.pluto7073.pdapi.networking.PDPayloads;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

@MethodsReturnNonnullByDefault
public record SyncAdditionsRegistryPayload(Map<ResourceLocation, DrinkAddition> registry) implements CustomPacketPayload {

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncAdditionsRegistryPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.map(HashMap::new, ResourceLocation.STREAM_CODEC, DrinkAddition.STREAM_CODEC),
            SyncAdditionsRegistryPayload::registry, SyncAdditionsRegistryPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return PDPayloads.SYNC_ADDITIONS_REGISTRY;
    }
}
