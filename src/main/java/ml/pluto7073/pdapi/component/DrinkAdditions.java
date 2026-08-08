package ml.pluto7073.pdapi.component;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import ml.pluto7073.pdapi.addition.DrinkAddition;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.ArrayList;
import java.util.List;

public record DrinkAdditions(List<Holder<DrinkAddition>> additions) {

    public static final DrinkAdditions EMPTY = new DrinkAdditions(new ArrayList<>());
    public static final Codec<DrinkAdditions> CODEC = DrinkAddition.COMPONENT_CODEC.listOf().xmap(DrinkAdditions::new, DrinkAdditions::additions);
    public static final StreamCodec<RegistryFriendlyByteBuf, DrinkAdditions> STREAM_CODEC =
            StreamCodec.of(ByteBufCodecs.fromCodecWithRegistries(CODEC), ByteBufCodecs.fromCodecWithRegistries(CODEC));

    public static DrinkAdditions or(DrinkAdditions first, DrinkAdditions second) {
        List<Holder<DrinkAddition>> additions = Lists.newArrayList(first.additions);
        additions.addAll(second.additions);
        return new DrinkAdditions(additions);
    }

    public static DrinkAdditions of(List<Holder<DrinkAddition>> additions) {
        return new DrinkAdditions(additions);
    }

    public DrinkAdditions withAddition(Holder<DrinkAddition> addition) {
        return new DrinkAdditions(Util.copyAndAdd(additions, addition));
    }
}
