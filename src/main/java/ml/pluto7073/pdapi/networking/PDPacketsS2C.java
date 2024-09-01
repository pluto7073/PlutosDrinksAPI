package ml.pluto7073.pdapi.networking;

import ml.pluto7073.pdapi.PDAPI;
import ml.pluto7073.pdapi.addition.DrinkAddition;
import ml.pluto7073.pdapi.addition.DrinkAdditions;
import ml.pluto7073.pdapi.listeners.DrinkAdditionRegisterer;
import ml.pluto7073.pdapi.networking.packet.s2c.SyncAdditionRegistryS2CPacket;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.player.LocalPlayer;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public class PDPacketsS2C {

    @Environment(EnvType.CLIENT)
    public static void register() {

        ClientPlayConnectionEvents.INIT.register((handler, client) ->
                ClientPlayNetworking.registerGlobalReceiver(SyncAdditionRegistryS2CPacket.TYPE, PDPacketsS2C::receiveAdditionsList)
        );

    }

    @Environment(EnvType.CLIENT)
    private static void receiveAdditionsList(SyncAdditionRegistryS2CPacket packet, LocalPlayer player, PacketSender sender) {
        DrinkAdditions.resetRegistry();

        packet.additions().entrySet().stream()
                .filter(Predicate.not(DrinkAdditions::contains))
                .forEach(entry -> DrinkAdditions.register(entry.getKey(), entry.getValue()));

        PDAPI.LOGGER.info("Received server-side Drink Additions list");
    }

}
