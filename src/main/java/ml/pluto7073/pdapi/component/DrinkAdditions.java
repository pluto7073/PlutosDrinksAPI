package ml.pluto7073.pdapi.component;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import ml.pluto7073.pdapi.addition.DrinkAddition;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public record DrinkAdditions(List<ResourceLocation> additions) {

    public static final DrinkAdditions EMPTY = new DrinkAdditions(new ArrayList<>());
    public static final Codec<DrinkAdditions> CODEC = ResourceLocation.CODEC.listOf().xmap(DrinkAdditions::new, DrinkAdditions::additions);
    public static final StreamCodec<RegistryFriendlyByteBuf, DrinkAdditions> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC.apply(ByteBufCodecs.list()), DrinkAdditions::additions,
            DrinkAdditions::new
    );

    public static DrinkAdditions or(DrinkAdditions first, DrinkAdditions second) {
        List<ResourceLocation> additions = Lists.newArrayList(first.additions);
        additions.addAll(second.additions);
        return new DrinkAdditions(additions);
    }

    public static DrinkAdditions of(List<ResourceLocation> additions) {
        return new DrinkAdditions(additions);
    }

    public DrinkAdditions withAddition(ResourceLocation addition) {
        return new DrinkAdditions(Util.copyAndAdd(additions, addition));
    }
}
