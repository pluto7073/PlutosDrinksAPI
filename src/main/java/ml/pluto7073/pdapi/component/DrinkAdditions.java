package ml.pluto7073.pdapi.component;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import ml.pluto7073.pdapi.addition.DrinkAddition;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public record DrinkAdditions(List<Holder<DrinkAddition>> additions) implements TooltipProvider {

    public static final DrinkAdditions EMPTY = new DrinkAdditions(new ArrayList<>());
    public static final Codec<DrinkAdditions> CODEC = DrinkAddition.COMPONENT_CODEC.listOf().xmap(DrinkAdditions::new, DrinkAdditions::additions);
    public static final StreamCodec<RegistryFriendlyByteBuf, DrinkAdditions> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.collection(ArrayList::new, DrinkAddition.STREAM_COMPONENT_CODEC), DrinkAdditions::additions,
            DrinkAdditions::new
    );

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

    @Override
    public void addToTooltip(Item.TooltipContext context, Consumer<Component> tooltipAdder, TooltipFlag tooltipFlag) {
        Map<Holder<DrinkAddition>, Integer> counts = Maps.newHashMap();
        for (Holder<DrinkAddition> addition : additions) {
            if (!counts.containsKey(addition)) {
                counts.put(addition, 1);
            } else {
                counts.put(addition, counts.get(addition) + 1);
            }
        }
        for (Holder<DrinkAddition> addition : counts.keySet()) {
            if (!addition.value().name().isEmpty()) {
                tooltipAdder.accept(Component.literal(addition.value().name() + " x" + counts.get(addition)).withStyle(ChatFormatting.GRAY));
            } else {
                tooltipAdder.accept(Component.translatable(addition.unwrapKey().orElseThrow().location().toLanguageKey("drink_addition"), counts.get(addition)).withStyle(ChatFormatting.GRAY));
            }
        }
    }
}
