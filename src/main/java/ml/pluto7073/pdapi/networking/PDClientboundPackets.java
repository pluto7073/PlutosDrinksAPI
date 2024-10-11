package ml.pluto7073.pdapi.networking;

import ml.pluto7073.pdapi.PDAPI;
import ml.pluto7073.pdapi.addition.DrinkAdditionManager;
import ml.pluto7073.pdapi.networking.packet.clientbound.ClientboundSyncAdditionRegistryPacket;
import ml.pluto7073.pdapi.networking.packet.clientbound.ClientboundSyncSpecialtyDrinkRegistryPacket;
import ml.pluto7073.pdapi.specialty.SpecialtyDrinkManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.client.player.LocalPlayer;

import java.util.function.Predicate;

public class PDClientboundPackets {

    public static void registerPackets() {
        PayloadTypeRegistry.playS2C().register(ClientboundSyncAdditionRegistryPacket.TYPE, ClientboundSyncAdditionRegistryPacket.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(ClientboundSyncSpecialtyDrinkRegistryPacket.TYPE, ClientboundSyncSpecialtyDrinkRegistryPacket.STREAM_CODEC);
    }

    @Environment(EnvType.CLIENT)
    public static void registerReceivers() {

        ClientPlayConnectionEvents.INIT.register((handler, client) -> {
                    ClientPlayNetworking.registerGlobalReceiver(ClientboundSyncAdditionRegistryPacket.TYPE, PDClientboundPackets::receiveAdditionsList);
                    ClientPlayNetworking.registerGlobalReceiver(ClientboundSyncSpecialtyDrinkRegistryPacket.TYPE, PDClientboundPackets::receiveDrinksList);
        });

    }

    @Environment(EnvType.CLIENT)
    private static void receiveAdditionsList(ClientboundSyncAdditionRegistryPacket packet, ClientPlayNetworking.Context context) {
        DrinkAdditionManager.resetRegistry();

        packet.additions().forEach(DrinkAdditionManager::register);

        PDAPI.LOGGER.info("Received server-side Drink Additions list");
    }

    @Environment(EnvType.CLIENT)
    private static void receiveDrinksList(ClientboundSyncSpecialtyDrinkRegistryPacket packet, ClientPlayNetworking.Context context) {
        SpecialtyDrinkManager.reset();

        packet.drinks().forEach(SpecialtyDrinkManager::register);
    }

}
