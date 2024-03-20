package ml.pluto7073.pdapi.item;

import ml.pluto7073.pdapi.DrinkUtil;
import ml.pluto7073.pdapi.addition.DrinkAddition;
import ml.pluto7073.pdapi.addition.DrinkAdditions;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;

public abstract class AbstractCustomizableDrinkItem extends Item {

    public static final String DRINK_DATA_NBT_KEY = "DrinkData";

    private static final int MAX_USE_TIME = 32;

    protected final Temperature baseTemperature;
    protected final Item baseItem;

    protected AbstractCustomizableDrinkItem(Item baseItem, Temperature baseTemperature, Settings settings) {
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
    public int getMaxUseTime(ItemStack stack) {
        return MAX_USE_TIME;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.DRINK;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        return ItemUsage.consumeHeldItem(world, user, hand);
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        PlayerEntity playerEntity = user instanceof PlayerEntity ? (PlayerEntity) user : null;
        if (playerEntity instanceof ServerPlayerEntity) {
            Criteria.CONSUME_ITEM.trigger((ServerPlayerEntity)playerEntity, stack);
        }

        if (!world.isClient) {
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
            playerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
            if (!playerEntity.getAbilities().creativeMode) {
                stack.decrement(1);
            }
        }

        if (playerEntity == null || !playerEntity.getAbilities().creativeMode) {
            if (stack.isEmpty()) {
                return new ItemStack(baseItem);
            }

            if (playerEntity != null) {
                playerEntity.getInventory().insertStack(new ItemStack(baseItem));
            }
        }
        user.emitGameEvent(GameEvent.DRINK);
        return stack;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        float caffeine = getCaffeineContent(stack);
        DrinkAddition[] addIns = DrinkUtil.getAdditionsFromStack(stack);
        HashMap<Identifier, Integer> additionCounts = new HashMap<>();
        for (DrinkAddition addIn : addIns) {
            if (addIn == DrinkAdditions.EMPTY) continue;
            Identifier id = DrinkAdditions.getId(addIn);
            if (additionCounts.containsKey(id)) {
                int count = additionCounts.get(id);
                additionCounts.put(id, ++count);
            } else additionCounts.put(id, 1);
        }
        additionCounts.forEach((id, count) -> tooltip.add(Text.translatable(DrinkAdditions.get(id).getTranslationKey(), count).formatted(Formatting.GRAY)));
        if (caffeine > 0) tooltip.add(Text.translatable("tooltip.pdapi.caffeine_content", caffeine).formatted(Formatting.AQUA));
    }

    public enum Temperature {
        BURNT, HOT, NORMAL, COLD, FROZEN
    }

}
