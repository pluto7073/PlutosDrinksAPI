package ml.pluto7073.pdapi.addition.chemicals;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

public interface ConsumableChemicalHandler {

    void tickPlayer(Player player);
    float get(Player player);
    void add(Player player, float amount);
    void set(Player player, float amount);
    Collection<MobEffectInstance> getEffectsForAmount(float amount, Player player);
    String getName();
    void saveToTag(SynchedEntityData data, CompoundTag tag);
    void loadFromTag(SynchedEntityData data, CompoundTag tag);
    void defineDataForPlayer(SynchedEntityData data);
    void appendTooltip(List<Component> tooltip, float amount);
    @Nullable LiteralArgumentBuilder<CommandSourceStack> getDrinkSubcommand();

}
