package ml.pluto7073.pdapi.networking.packet.clientbound;

import ml.pluto7073.pdapi.PDAPI;
import ml.pluto7073.pdapi.PDRegistries;
import ml.pluto7073.pdapi.specialty.SpecialtyDrink;
import ml.pluto7073.pdapi.specialty.SpecialtyDrinkHolder;
import ml.pluto7073.pdapi.specialty.SpecialtyDrinkManager;
import ml.pluto7073.pdapi.specialty.SpecialtyDrinkSerializer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.repository.Pack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public record ClientboundSyncSpecialtyDrinkRegistryPacket(List<SpecialtyDrinkHolder> drinks) implements CustomPacketPayload {

    public static final Type<ClientboundSyncSpecialtyDrinkRegistryPacket> TYPE = new Type<>(PDAPI.asId("clientbound/sync_specialty_drink_registry"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundSyncSpecialtyDrinkRegistryPacket> STREAM_CODEC =
            StreamCodec.composite(SpecialtyDrinkHolder.STREAM_CODEC.apply(ByteBufCodecs.list()), ClientboundSyncSpecialtyDrinkRegistryPacket::drinks,
                    ClientboundSyncSpecialtyDrinkRegistryPacket::new);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
