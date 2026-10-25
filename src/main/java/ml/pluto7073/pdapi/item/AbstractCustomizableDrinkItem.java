package ml.pluto7073.pdapi.item;

import ml.pluto7073.pdapi.PDAPI;
import ml.pluto7073.pdapi.component.DrinkAdditions;
import ml.pluto7073.pdapi.component.PDComponents;
import ml.pluto7073.chemicals.item.ChemicalContaining;
import ml.pluto7073.pdapi.util.DrinkUtil;
import ml.pluto7073.pdapi.addition.DrinkAddition;
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
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

import java.util.List;

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

    public double getTotalVolume(ItemStack stack, HolderLookup.Provider provider) {
        double vol = baseVolume;
        for (Holder<DrinkAddition> a : stack.getOrDefault(PDComponents.ADDITIONS, DrinkAdditions.EMPTY).additions()) {
            vol += a.value().volume();
        }
        return vol;
    }

    @Override
    public float getChemicalContent(ResourceLocation name, ItemStack stack, Level level) {
        int amount = 0;
        for (Holder<DrinkAddition> a : stack.getOrDefault(PDComponents.ADDITIONS, DrinkAdditions.EMPTY).additions()) {
            if (a.value().chemicals().containsKey(name))
                amount += a.value().chemicals().get(name);
        }
        return amount;
    }

    @Override
    public float getConsumedChemicalContent(ResourceLocation id, Player player, ItemStack stack, Level level) {
        float amount = getChemicalContent(id, stack, level);
        if (!shouldSip(stack) || player.isShiftKeyDown()) return amount;
        return (float) (getSipAmount(stack, level.registryAccess()) / getTotalVolume(stack, level.registryAccess())) * amount;
    }

    protected Item baseItem(ItemStack stack, HolderLookup.Provider provider) {
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
    protected double getSipAmount(ItemStack stack, HolderLookup.Provider provider) {
        return getTotalVolume(stack, provider) > 10 ? 1 : 0.5;
    }

    protected boolean shouldSip(ItemStack stack) {
        return true;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity user) {
        Player player = user instanceof Player ? (Player) user : null;

        List<Holder<DrinkAddition>> additions = stack.getOrDefault(PDComponents.ADDITIONS, DrinkAdditions.EMPTY).additions();

        if (player != null) {
            if (player.isShiftKeyDown() || !shouldSip(stack)) {
                stack.remove(PDComponents.SIPPED);
            } else {
                double sipped = stack.getOrDefault(PDComponents.SIPPED, 0.0);
                sipped += getSipAmount(stack, world.registryAccess());
                if (sipped >= getTotalVolume(stack, world.registryAccess())) {
                    stack.shrink(1);
                    stack.remove(PDComponents.SIPPED);
                } else  {
                    stack.set(PDComponents.SIPPED, sipped);
                }
            }
        }

        if (stack.get(PDComponents.SIPPED) != null) return stack;

        if (player instanceof ServerPlayer) {
            CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayer)player, stack);
        }

        if (!world.isClientSide) {
            for (Holder<DrinkAddition> addition : additions) {
                addition.value().onDrink(stack, world, user);
            }
        }

        Item baseItem = baseItem(stack, world.registryAccess());

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
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        DrinkAdditions additions = stack.getOrDefault(PDComponents.ADDITIONS, DrinkAdditions.EMPTY);
        additions.addToTooltip(context, tooltipComponents::add, tooltipFlag);
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
