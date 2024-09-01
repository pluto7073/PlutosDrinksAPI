package ml.pluto7073.pdapi.networking.packet.clientbound;

import ml.pluto7073.pdapi.PDAPI;
import ml.pluto7073.pdapi.addition.DrinkAddition;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public record ClientboundSyncAdditionRegistryPacket(Map<ResourceLocation, DrinkAddition> additions) implements FabricPacket {

    public static final PacketType<ClientboundSyncAdditionRegistryPacket> TYPE = PacketType.create(
            PDAPI.asId("clientbound/sync_addition_registry"), ClientboundSyncAdditionRegistryPacket::read
    );

    private static ClientboundSyncAdditionRegistryPacket read(FriendlyByteBuf buffer) {
        return new ClientboundSyncAdditionRegistryPacket(buffer.readMap(FriendlyByteBuf::readResourceLocation, DrinkAddition::fromNetwork));
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeMap(additions, FriendlyByteBuf::writeResourceLocation, (b, addition) -> addition.toNetwork(b));
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }

}
