package ml.pluto7073.pdapi.specialty;

import ml.pluto7073.pdapi.addition.AdditionHolder;
import ml.pluto7073.pdapi.addition.DrinkAddition;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record SpecialtyDrinkHolder(ResourceLocation id, SpecialtyDrink value) {

    public static final StreamCodec<RegistryFriendlyByteBuf, SpecialtyDrinkHolder> STREAM_CODEC = StreamCodec.composite(ResourceLocation.STREAM_CODEC,
            SpecialtyDrinkHolder::id, SpecialtyDrink.STREAM_CODEC, SpecialtyDrinkHolder::value, SpecialtyDrinkHolder::new);

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;

        if (obj instanceof SpecialtyDrinkHolder holder) {
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
