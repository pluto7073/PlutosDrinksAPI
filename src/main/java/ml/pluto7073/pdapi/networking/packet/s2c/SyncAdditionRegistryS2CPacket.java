package ml.pluto7073.pdapi.networking.packet.s2c;

import com.google.gson.JsonObject;
import ml.pluto7073.pdapi.PDAPI;
import ml.pluto7073.pdapi.networking.NetworkingUtils;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public record SyncAdditionRegistryS2CPacket(Map<ResourceLocation, JsonObject> additions) implements FabricPacket {

    public static final PacketType<SyncAdditionRegistryS2CPacket> TYPE = PacketType.create(
            PDAPI.asId("s2c/sync_addition_registry"), SyncAdditionRegistryS2CPacket::read
    );

    private static SyncAdditionRegistryS2CPacket read(FriendlyByteBuf buffer) {
        return new SyncAdditionRegistryS2CPacket(buffer.readMap(FriendlyByteBuf::readResourceLocation, NetworkingUtils::readJsonObject));
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeMap(additions, FriendlyByteBuf::writeResourceLocation, NetworkingUtils::writeJsonObjectStart);
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }

}
