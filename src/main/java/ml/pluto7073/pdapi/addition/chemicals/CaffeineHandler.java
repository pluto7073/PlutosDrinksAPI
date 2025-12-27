package ml.pluto7073.pdapi.addition.chemicals;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import ml.pluto7073.chemicals.Chemicals;
import ml.pluto7073.chemicals.handlers.HalfLifeChemicalHandler;
import ml.pluto7073.pdapi.PDAPI;
import ml.pluto7073.pdapi.config.PDCommonConfig;
import ml.pluto7073.pdapi.entity.effect.PDMobEffects;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CaffeineHandler extends HalfLifeChemicalHandler {

    public static final CaffeineHandler INSTANCE = new CaffeineHandler(2500);

    public CaffeineHandler(int halfLifeTicks) {
        super(halfLifeTicks);
    }

    @Override
    public Collection<MobEffectInstance> getEffectsForAmount(float amount, Level level) {
        ArrayList<MobEffectInstance> list = new ArrayList<>();
        if (amount >= 100) {
            list.add(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 600));
        }
        if (amount >= 150) {
            list.add(new MobEffectInstance(MobEffects.DIG_SPEED, 600));
        }
        if (amount >= 300) {
            list.add(new MobEffectInstance(MobEffects.HUNGER, 600));
        }
        if (amount >= 400 && FabricLoader.getInstance().isModLoaded("dehydration")) {
            //noinspection DataFlowIssue
            list.add(new MobEffectInstance(BuiltInRegistries.MOB_EFFECT.get(new ResourceLocation("dehydration:thirst_effect")),
                    600, 0));
        }
        if (amount >= 450) {
            list.add(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 600, 1));
        }
        if (amount >= 500) {
            list.add(new MobEffectInstance(MobEffects.JUMP, 600));
        }
        if (amount >= 600) {
            list.add(new MobEffectInstance(MobEffects.DIG_SPEED, 600, 1));
        }
        if (amount >= 700) {
            list.add(new MobEffectInstance(MobEffects.JUMP, 600, 1));
        }
        int lethalCaffeineDose = PDCommonConfig.INSTANCE.lethalCaffeineDose;
        boolean overdose = PDCommonConfig.INSTANCE.doCaffeineOverdose;
        if (overdose && amount >= lethalCaffeineDose) {
            list.add(new MobEffectInstance(PDMobEffects.CAFFEINE_OVERDOSE, 20 * 60));
        }
        return list;
    }

    @Override
    public void appendTooltip(List<Component> tooltip, float caffeine, ItemStack stack) {
        if (caffeine > 0) tooltip.add(Component.translatable("tooltip.pdapi.caffeine_content", caffeine).withStyle(ChatFormatting.AQUA));
    }

    public static void init() {
        Registry.register(Chemicals.REGISTRY, PDAPI.asId("caffeine"), INSTANCE);
    }

    @Override
    public String formatAmount(float amount) {
        return amount + "mg";
    }
}
