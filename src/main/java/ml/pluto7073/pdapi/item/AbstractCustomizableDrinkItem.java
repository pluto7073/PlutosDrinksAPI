package ml.pluto7073.pdapi.item;

import ml.pluto7073.pdapi.DrinkUtil;
import ml.pluto7073.pdapi.addition.DrinkAddition;
import ml.pluto7073.pdapi.addition.DrinkAdditions;
import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.util.*;
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

    public int getCaffeineContent(ItemStack stack) {
        int caffeine = 0;
        for (DrinkAddition a : DrinkUtil.getAdditionsFromStack(stack)) {
            caffeine += a.getCaffeine();
        }
        return caffeine;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
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
        Player playerEntity = user instanceof Player ? (Player) user : null;
        if (playerEntity instanceof ServerPlayer) {
            CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayer)playerEntity, stack);
        }

        if (!world.isClientSide) {
            DrinkAddition[] additions = DrinkUtil.getAdditionsFromStack(stack);
            for (DrinkAddition addition : additions) {
                addition.onDrink(stack, world, user);
            }
            int caffeine = this.getCaffeineContent(stack);
            if (playerEntity != null) {
                float currentCaffeine = DrinkUtil.getPlayerCaffeine(playerEntity);
                currentCaffeine += caffeine;
                DrinkUtil.setPlayerCaffeine(playerEntity, currentCaffeine);
            }
        }

        if (playerEntity != null) {
            playerEntity.awardStat(Stats.ITEM_USED.get(this));
            if (!playerEntity.getAbilities().instabuild) {
                stack.shrink(1);
            }
        }

        if (playerEntity == null || !playerEntity.getAbilities().instabuild) {
            if (stack.isEmpty()) {
                return new ItemStack(baseItem);
            }

            if (playerEntity != null) {
                playerEntity.getInventory().add(new ItemStack(baseItem));
            }
        }
        user.gameEvent(GameEvent.DRINK);
        return stack;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag context) {
        float caffeine = getCaffeineContent(stack);
        DrinkAddition[] addIns = DrinkUtil.getAdditionsFromStack(stack);
        HashMap<ResourceLocation, Integer> additionCounts = new HashMap<>();
        for (DrinkAddition addIn : addIns) {
            if (addIn == DrinkAdditions.EMPTY) continue;
            ResourceLocation id = DrinkAdditions.getId(addIn);
            if (additionCounts.containsKey(id)) {
                int count = additionCounts.get(id);
                additionCounts.put(id, ++count);
            } else additionCounts.put(id, 1);
        }
        additionCounts.forEach((id, count) -> tooltip.add(Component.translatable(DrinkAdditions.get(id).getTranslationKey(), count).withStyle(ChatFormatting.GRAY)));
        if (caffeine > 0) tooltip.add(Component.translatable("tooltip.pdapi.caffeine_content", caffeine).withStyle(ChatFormatting.AQUA));
    }

    public enum Temperature {
        BURNT, HOT, NORMAL, COLD, FROZEN
    }

}
