package ml.pluto7073.pdapi.networking.packet.clientbound;

import ml.pluto7073.pdapi.PDAPI;
import ml.pluto7073.pdapi.addition.AdditionHolder;
import ml.pluto7073.pdapi.addition.DrinkAddition;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public record ClientboundSyncAdditionRegistryPacket(List<AdditionHolder> additions) implements CustomPacketPayload {

    public static final Type<ClientboundSyncAdditionRegistryPacket> TYPE =
            new Type<>(PDAPI.asId("clientbound/sync_addition_registry"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundSyncAdditionRegistryPacket> STREAM_CODEC =
            StreamCodec.composite(AdditionHolder.STREAM_CODEC.apply(ByteBufCodecs.list()), ClientboundSyncAdditionRegistryPacket::additions,
                    ClientboundSyncAdditionRegistryPacket::new);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
