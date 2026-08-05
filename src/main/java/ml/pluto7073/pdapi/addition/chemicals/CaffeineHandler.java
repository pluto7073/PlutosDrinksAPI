package ml.pluto7073.pdapi.addition.chemicals;

import ml.pluto7073.chemicals.Chemicals;
import ml.pluto7073.chemicals.handlers.HalfLifeChemicalHandler;
import ml.pluto7073.pdapi.PDAPI;
import ml.pluto7073.pdapi.util.PDCommonConfig;
import ml.pluto7073.pdapi.entity.effect.PDMobEffects;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CaffeineHandler extends HalfLifeChemicalHandler {

    public static final CaffeineHandler INSTANCE = new CaffeineHandler(2500, 10000);

    public CaffeineHandler(int halfLifeTicks, float maxRecommendedAmount) {
        super(halfLifeTicks, maxRecommendedAmount);
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
        if (amount >= 450) {
            list.add(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 600, 1));
        }
        if (amount >= 500) {
            list.add(new MobEffectInstance(MobEffects.JUMP, 600));
        }
        if (amount >= 550) {
            list.add(new MobEffectInstance(MobEffects.HUNGER, 600));
        }
        if (amount >= 600) {
            if (FabricLoader.getInstance().isModLoaded("dehydration")) {
                //noinspection DataFlowIssue
                list.add(new MobEffectInstance(BuiltInRegistries.MOB_EFFECT.get(new ResourceLocation("dehydration:thirst_effect")),
                        600, 0));
            } else if (FabricLoader.getInstance().isModLoaded("toughasnails"))
                //noinspection DataFlowIssue
                list.add(new MobEffectInstance(BuiltInRegistries.MOB_EFFECT.get(new ResourceLocation("toughasnails:thirst")),
                        600, 0));
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
        Registry.register(Chemicals.CHEMICAL_HANDLER, PDAPI.asId("caffeine"), INSTANCE);
    }

    @Override
    public String formatAmount(float amount) {
        return amount + "mg";
    }
}
