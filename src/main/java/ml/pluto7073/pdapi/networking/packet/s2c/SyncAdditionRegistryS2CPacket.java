package ml.pluto7073.pdapi.networking.packet.s2c;

import ml.pluto7073.pdapi.PDAPI;
import ml.pluto7073.pdapi.addition.DrinkAddition;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public record SyncAdditionRegistryS2CPacket(Map<ResourceLocation, DrinkAddition> additions) implements FabricPacket {

    public static final PacketType<SyncAdditionRegistryS2CPacket> TYPE = PacketType.create(
            PDAPI.asId("s2c/sync_addition_registry"), SyncAdditionRegistryS2CPacket::read
    );

    private static SyncAdditionRegistryS2CPacket read(FriendlyByteBuf buffer) {
        return new SyncAdditionRegistryS2CPacket(buffer.readMap(FriendlyByteBuf::readResourceLocation, DrinkAddition::fromNetwork));
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
