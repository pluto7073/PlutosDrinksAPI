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
import net.minecraft.client.player.LocalPlayer;

import java.util.function.Predicate;

public class PDClientboundPackets {

    @Environment(EnvType.CLIENT)
    public static void register() {

        ClientPlayConnectionEvents.INIT.register((handler, client) ->
                ClientPlayNetworking.registerGlobalReceiver(ClientboundSyncAdditionRegistryPacket.TYPE, PDClientboundPackets::receiveAdditionsList)
        );

        ClientPlayConnectionEvents.INIT.register((handler, client) ->
                ClientPlayNetworking.registerGlobalReceiver(ClientboundSyncSpecialtyDrinkRegistryPacket.TYPE, PDClientboundPackets::receiveDrinksList)
        );

    }

    @Environment(EnvType.CLIENT)
    private static void receiveAdditionsList(ClientboundSyncAdditionRegistryPacket packet, LocalPlayer player, PacketSender sender) {
        DrinkAdditionManager.resetRegistry();

        packet.additions().entrySet().stream()
                .filter(Predicate.not(DrinkAdditionManager::contains))
                .forEach(entry -> DrinkAdditionManager.register(entry.getKey(), entry.getValue()));

        PDAPI.LOGGER.info("Received server-side Drink Additions list");
    }

    @Environment(EnvType.CLIENT)
    private static void receiveDrinksList(ClientboundSyncSpecialtyDrinkRegistryPacket packet, LocalPlayer player, PacketSender sender) {
        SpecialtyDrinkManager.reset();

        packet.registry().forEach(SpecialtyDrinkManager::register);
    }

}
