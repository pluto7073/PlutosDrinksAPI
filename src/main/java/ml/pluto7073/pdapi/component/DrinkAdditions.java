package ml.pluto7073.pdapi.component;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import ml.pluto7073.pdapi.addition.DrinkAddition;
import ml.pluto7073.pdapi.addition.DrinkAdditionManager;
import ml.pluto7073.pdapi.util.DrinkUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

public record DrinkAdditions(List<DrinkAddition> additions) implements TooltipProvider {

    public static final DrinkAdditions EMPTY = new DrinkAdditions(new ArrayList<>());
    public static final Codec<DrinkAdditions> CODEC = DrinkAddition.COMPONENT_CODEC.listOf().xmap(DrinkAdditions::new, DrinkAdditions::additions);
    public static final StreamCodec<RegistryFriendlyByteBuf, DrinkAdditions> STREAM_CODEC =
            StreamCodec.of(ByteBufCodecs.fromCodecWithRegistries(CODEC), ByteBufCodecs.fromCodecWithRegistries(CODEC));

    public static DrinkAdditions or(DrinkAdditions first, DrinkAdditions second) {
        List<DrinkAddition> additions = Lists.newArrayList(first.additions);
        additions.addAll(second.additions);
        return new DrinkAdditions(additions);
    }

    public static DrinkAdditions of(List<ResourceLocation> additions) {
        return new DrinkAdditions(additions.stream().map(DrinkAdditionManager::get).toList());
    }

    public static DrinkAdditions of(ResourceLocation addition) {
        return new DrinkAdditions(List.of(DrinkAdditionManager.get(addition)));
    }

    @Override
    public void addToTooltip(Item.TooltipContext context, Consumer<Component> tooltip, TooltipFlag config) {
        HashMap<ResourceLocation, Integer> additionCounts = new HashMap<>();
        List<ResourceLocation> order = new ArrayList<>();
        for (DrinkAddition addIn : additions) {
            if (addIn == DrinkAdditionManager.EMPTY) continue;
            ResourceLocation id = DrinkAdditionManager.getId(addIn);
            if (additionCounts.containsKey(id)) {
                int count = additionCounts.get(id);
                additionCounts.put(id, ++count);
            } else {
                additionCounts.put(id, 1);
                order.add(id);
            }
        }
        order.forEach(id -> tooltip.accept(Component.translatable(DrinkAdditionManager.get(id).getTranslationKey(), additionCounts.get(id)).withStyle(ChatFormatting.GRAY)));

    }

    public DrinkAdditions withAddition(DrinkAddition addition) {
        return new DrinkAdditions(Util.copyAndAdd(additions, addition));
    }
}
