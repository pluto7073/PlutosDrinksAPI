package ml.pluto7073.pdapi.item;

import ml.pluto7073.pdapi.component.DrinkAdditions;
import ml.pluto7073.pdapi.component.PDComponents;
import ml.pluto7073.chemicals.item.ChemicalContaining;
import ml.pluto7073.pdapi.util.DrinkUtil;
import ml.pluto7073.pdapi.addition.DrinkAddition;
import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

@MethodsReturnNonnullByDefault
public abstract class AbstractCustomizableDrinkItem extends Item implements ChemicalContaining {

    public static final String DRINK_DATA_NBT_KEY = "DrinkData";

    private static final int MAX_USE_TIME = 16;

    protected final Item baseItem;
    protected final double baseVolume;

    protected AbstractCustomizableDrinkItem(Item baseItem, double baseVolume, Properties settings) {
        super(settings);
        this.baseVolume = baseVolume;
        this.baseItem = baseItem;
    }

    public double getTotalVolume(ItemStack stack, Level level) {
        double vol = baseVolume;
        for (DrinkAddition a : DrinkUtil.getAdditionsFromStack(stack, level)) {
            vol += a.volume();
        }
        return vol;
    }

    @Override
    public float getChemicalContent(ResourceLocation name, ItemStack stack, Level level) {
        int amount = 0;
        for (DrinkAddition a : DrinkUtil.getAdditionsFromStack(stack, level)) {
            if (a.getChemicals().containsKey(name))
                amount += a.getChemicals().get(name);
        }
        return amount;
    }

    @Override
    public float getConsumedChemicalContent(ResourceLocation id, ItemStack stack, Level level) {
        float amount = getChemicalContent(id, stack, level);
        return (float) (getSipAmount(stack, level) / getTotalVolume(stack, level)) * amount;
    }

    protected Item baseItem(ItemStack stack, Level level) {
        return baseItem;
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

    /**
     * @return the amount of liquid to be sipped.  1oz for >10oz drinks, 0.5oz for less than 10oz drinks
     */
    protected double getSipAmount(ItemStack stack, Level level) {
        return getTotalVolume(stack, level) > 10 ? 1 : 0.5;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity user) {
        Player player = user instanceof Player ? (Player) user : null;
        Item baseItem = baseItem(stack, world);

        if (player != null) {
            double sipped = stack.getOrDefault(PDComponents.SIPPED, 0.0);
            sipped += getSipAmount(stack, world);
            if (sipped >= getTotalVolume(stack, world)) {
                stack.shrink(1);
                stack.remove(PDComponents.SIPPED);
            } else  {
                stack.set(PDComponents.SIPPED, sipped);
            }
        }

        if (!stack.isEmpty()) return stack;

        if (player instanceof ServerPlayer) {
            CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayer)player, stack);
        }

        if (!world.isClientSide) {
            DrinkAddition[] additions = DrinkUtil.getAdditionsFromStack(stack, world);
            for (DrinkAddition addition : additions) {
                addition.onDrink(stack, world, user);
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
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag config) {
        HashMap<ResourceLocation, Integer> additionCounts = new HashMap<>();
        List<ResourceLocation> order = new ArrayList<>();
        List<ResourceLocation> additions = stack.getOrDefault(PDComponents.ADDITIONS, DrinkAdditions.EMPTY).additions();
        for (ResourceLocation addIn : additions) {
            if (additionCounts.containsKey(addIn)) {
                int count = additionCounts.get(addIn);
                additionCounts.put(addIn, ++count);
            } else {
                additionCounts.put(addIn, 1);
                order.add(addIn);
            }
        }
        order.forEach(holder -> tooltip.add(Component.translatable(holder.toLanguageKey("drink_addition"), additionCounts.get(holder)).withStyle(ChatFormatting.GRAY)));
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return super.getBarWidth(stack);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return 0x25bbf7;
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return stack.getOrDefault(PDComponents.SIPPED, 0.0) > 0;
    }
}
