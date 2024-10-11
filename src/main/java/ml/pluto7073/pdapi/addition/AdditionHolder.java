package ml.pluto7073.pdapi.addition;

import ml.pluto7073.pdapi.networking.packet.clientbound.ClientboundSyncAdditionRegistryPacket;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;

public record AdditionHolder(ResourceLocation id, DrinkAddition value) {

    public static final StreamCodec<RegistryFriendlyByteBuf, AdditionHolder> STREAM_CODEC =
            StreamCodec.composite(ResourceLocation.STREAM_CODEC, AdditionHolder::id, DrinkAddition.STREAM_CODEC, AdditionHolder::value, AdditionHolder::new);

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;

        if (obj instanceof AdditionHolder holder) {
            return holder.id().equals(this.id);
        }

        return false;
    }

    public int hashCode() {
        return this.id.hashCode();
    }

    public String toString() {
        return this.id.toString();
    }

}
