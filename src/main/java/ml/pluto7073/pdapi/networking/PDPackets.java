package ml.pluto7073.pdapi.networking;

import ml.pluto7073.pdapi.networking.packet.s2c.SyncAdditionRegistryS2CPacket;
import net.minecraft.util.Identifier;

public class PDPackets {

    public static final Identifier ADDITION_LIST = SyncAdditionRegistryS2CPacket.TYPE.getId();

}
