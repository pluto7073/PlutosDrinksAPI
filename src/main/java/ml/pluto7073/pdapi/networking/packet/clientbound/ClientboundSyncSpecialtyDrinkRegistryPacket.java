package ml.pluto7073.pdapi.networking.packet.clientbound;

import ml.pluto7073.pdapi.PDAPI;
import ml.pluto7073.pdapi.specialty.SpecialtyDrink;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.repository.Pack;

import java.util.Map;

public record ClientboundSyncSpecialtyDrinkRegistryPacket(Map<ResourceLocation, SpecialtyDrink> registry)
        implements FabricPacket {

    public static final PacketType<ClientboundSyncSpecialtyDrinkRegistryPacket> TYPE = PacketType.create(
            PDAPI.asId("clientbound/sync_specialty_drink_registry"),
            ClientboundSyncSpecialtyDrinkRegistryPacket::read
    );

    private static ClientboundSyncSpecialtyDrinkRegistryPacket read(FriendlyByteBuf buf) {
        return new ClientboundSyncSpecialtyDrinkRegistryPacket(buf.readMap(
                FriendlyByteBuf::readResourceLocation,
                SpecialtyDrink::fromNetwork
        ));
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeMap(registry, FriendlyByteBuf::writeResourceLocation, (b, d) -> d.toNetwork(b));
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }
}
