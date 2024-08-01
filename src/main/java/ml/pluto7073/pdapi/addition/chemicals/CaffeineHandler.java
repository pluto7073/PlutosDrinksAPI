package ml.pluto7073.pdapi.addition.chemicals;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import ml.pluto7073.pdapi.DrinkUtil;
import ml.pluto7073.pdapi.command.PDCommands;
import ml.pluto7073.pdapi.entity.PDTrackedData;
import ml.pluto7073.pdapi.entity.effect.PDMobEffects;
import ml.pluto7073.pdapi.gamerule.PDGameRules;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CaffeineHandler implements ConsumableChemicalHandler {

    @Override
    public void tickPlayer(Player player) {
        int ticks = player.getEntityData().get(PDTrackedData.PLAYER_TICKS_SINCE_LAST_CAFFEINE);
        ticks++;
        float originalCaffeine = player.getEntityData().get(PDTrackedData.PLAYER_ORIGINAL_CAFFEINE_AMOUNT);
        float newCaffeine = DrinkUtil.calculateCaffeineDecay(ticks, originalCaffeine);
        player.getEntityData().set(PDTrackedData.PLAYER_CAFFEINE_AMOUNT, newCaffeine);
        player.getEntityData().set(PDTrackedData.PLAYER_TICKS_SINCE_LAST_CAFFEINE, ticks);
    }

    @Override
    public float get(Player player) {
        return player.getEntityData().get(PDTrackedData.PLAYER_CAFFEINE_AMOUNT).intValue();
    }

    @Override
    public void add(Player player, float amount) {
        set(player, get(player) + amount);
    }

    @Override
    public void set(Player player, float amount) {
        player.getEntityData().set(PDTrackedData.PLAYER_TICKS_SINCE_LAST_CAFFEINE, 0);
        player.getEntityData().set(PDTrackedData.PLAYER_CAFFEINE_AMOUNT, amount);
        player.getEntityData().set(PDTrackedData.PLAYER_ORIGINAL_CAFFEINE_AMOUNT, amount);
    }

    @Override
    public Collection<MobEffectInstance> getEffectsForAmount(float amount, Player player) {
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
        int lethalCaffeineDose = player.level().getGameRules().getInt(PDGameRules.LETHAL_CAFFEINE_DOSE);
        boolean overdose = player.level().getGameRules().getBoolean(PDGameRules.DO_CAFFEINE_OVERDOSE);
        if (overdose && amount >= lethalCaffeineDose) {
            list.add(new MobEffectInstance(PDMobEffects.CAFFEINE_OVERDOSE, 20 * 60));
        }
        return list;
    }

    @Override
    public String getName() {
        return "caffeine";
    }

    @Override
    public void saveToTag(SynchedEntityData data, CompoundTag tag) {
        CompoundTag caffeineData = new CompoundTag();

        // Caffeine Content
        float caffeine = data.get(PDTrackedData.PLAYER_CAFFEINE_AMOUNT);
        caffeineData.putFloat("CaffeineContent", caffeine);

        // Original Caffeine Content (For math purposes)
        float originalCaffeine = data.get(PDTrackedData.PLAYER_ORIGINAL_CAFFEINE_AMOUNT);
        caffeineData.putFloat("OriginalCaffeineContent", originalCaffeine);

        // Ticks Since Last Caffeine Change
        int ticks = data.get(PDTrackedData.PLAYER_TICKS_SINCE_LAST_CAFFEINE);
        caffeineData.putInt("TicksSinceLastCaffeine", ticks);

        tag.put("CaffeineData", caffeineData);
    }

    @Override
    public void loadFromTag(SynchedEntityData data, CompoundTag tag) {
        CompoundTag caffeineData;
        if (tag.contains("CoffeeData")) {
            caffeineData = tag.getCompound("CoffeeData");
        } else if (tag.contains("CaffeineData")) {
            caffeineData = tag.getCompound("CaffeineData");
        } else return;

        // Caffeine Content
        if (caffeineData.contains("CaffeineContent")) {
            float caffeine = caffeineData.getFloat("CaffeineContent");
            data.set(PDTrackedData.PLAYER_CAFFEINE_AMOUNT, caffeine);
        }

        // Original Caffeine Content (for math purposes)
        if (caffeineData.contains("OriginalCaffeineContent")) {
            float originalCaffeine = caffeineData.getFloat("OriginalCaffeineContent");
            data.set(PDTrackedData.PLAYER_ORIGINAL_CAFFEINE_AMOUNT, originalCaffeine);
        }

        // Ticks Since Last Caffeine Change
        if (caffeineData.contains("TicksSinceLastCaffeine")) {
            int ticks = caffeineData.getInt("TicksSinceLastCaffeine");
            data.set(PDTrackedData.PLAYER_TICKS_SINCE_LAST_CAFFEINE, ticks);
        }
    }

    @Override
    public void defineDataForPlayer(SynchedEntityData data) {
        data.define(PDTrackedData.PLAYER_CAFFEINE_AMOUNT, 0F);
        data.define(PDTrackedData.PLAYER_ORIGINAL_CAFFEINE_AMOUNT, 0F);
        data.define(PDTrackedData.PLAYER_TICKS_SINCE_LAST_CAFFEINE, 0);
    }

    @Override
    public void appendTooltip(List<Component> tooltip, float caffeine, ItemStack stack) {
        if (caffeine > 0) tooltip.add(Component.translatable("tooltip.pdapi.caffeine_content", caffeine).withStyle(ChatFormatting.AQUA));
    }

    @Override
    public @Nullable LiteralArgumentBuilder<CommandSourceStack> getDrinkSubcommand() {
        return PDCommands.caffeine();
    }

}
