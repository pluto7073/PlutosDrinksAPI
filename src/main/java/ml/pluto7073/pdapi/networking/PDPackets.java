package ml.pluto7073.pdapi.networking;

import ml.pluto7073.pdapi.networking.packet.s2c.SyncAdditionRegistryS2CPacket;
import net.minecraft.resources.ResourceLocation;

public class PDPackets {

    public static final ResourceLocation ADDITION_LIST = SyncAdditionRegistryS2CPacket.TYPE.getId();

}
