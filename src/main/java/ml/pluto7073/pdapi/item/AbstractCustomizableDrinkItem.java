package ml.pluto7073.pdapi.item;

import ml.pluto7073.pdapi.component.DrinkAdditions;
import ml.pluto7073.pdapi.component.PDComponents;
import ml.pluto7073.pdapi.util.DrinkUtil;
import ml.pluto7073.pdapi.addition.DrinkAddition;
import ml.pluto7073.pdapi.addition.DrinkAdditionManager;
import ml.pluto7073.pdapi.addition.chemicals.ConsumableChemicalRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;

@MethodsReturnNonnullByDefault
public abstract class AbstractCustomizableDrinkItem extends Item {

    public static final String DRINK_DATA_NBT_KEY = "DrinkData";

    private static final int MAX_USE_TIME = 32;

    protected final Temperature baseTemperature;
    protected final Item baseItem;

    protected AbstractCustomizableDrinkItem(Item baseItem, Temperature baseTemperature, Properties settings) {
        super(settings);
        this.baseTemperature = baseTemperature;
        this.baseItem = baseItem;
    }

    public Temperature getDrinkTemperature(ItemStack stack) {
        return baseTemperature;
    }

    public int getChemicalContent(String name, ItemStack stack) {
        int amount = 0;
        for (DrinkAddition a : DrinkUtil.getAdditionsFromStack(stack)) {
            amount += a.chemicals().get(name);
        }
        return amount;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return MAX_USE_TIME;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
        return ItemUtils.startUsingInstantly(world, user, hand);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity user) {
        Player player = user instanceof Player ? (Player) user : null;
        if (player instanceof ServerPlayer) {
            CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayer)player, stack);
        }

        if (!world.isClientSide) {
            DrinkAddition[] additions = DrinkUtil.getAdditionsFromStack(stack);
            for (DrinkAddition addition : additions) {
                addition.onDrink(stack, world, user);
            }
            if (player != null) {
                ConsumableChemicalRegistry.forEach(handler -> {
                    float amount = getChemicalContent(handler.getName(), stack);
                    if (amount > 0) handler.add(player, amount);
                });
            }
        }

        if (player != null) {
            player.awardStat(Stats.ITEM_USED.get(this));
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
        }

        if (player == null || !player.getAbilities().instabuild) {
            if (stack.isEmpty()) {
                return new ItemStack(baseItem);
            }

            if (player != null) {
                player.getInventory().add(new ItemStack(baseItem));
            }
        }
        user.gameEvent(GameEvent.DRINK);
        return stack;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag config) {
        super.appendHoverText(stack, context, tooltip, config);

        if (config.isAdvanced() || config.isCreative()) ConsumableChemicalRegistry.forEach(handler ->
                handler.appendTooltip(tooltip, getChemicalContent(handler.getName(), stack), stack));

        stack.getOrDefault(PDComponents.ADDITIONS, DrinkAdditions.EMPTY).addToTooltip(context, tooltip::add, config);
    }

    public enum Temperature {
        BURNT, HOT, NORMAL, COLD, FROZEN
    }

}
