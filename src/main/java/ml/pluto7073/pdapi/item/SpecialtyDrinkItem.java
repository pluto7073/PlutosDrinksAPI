package ml.pluto7073.pdapi.item;

import com.google.common.collect.Streams;
import ml.pluto7073.pdapi.PDAPI;
import ml.pluto7073.pdapi.addition.action.OnDrinkAction;
import ml.pluto7073.pdapi.util.DrinkUtil;
import ml.pluto7073.pdapi.specialty.SpecialtyDrink;
import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Stream;

@MethodsReturnNonnullByDefault
public class SpecialtyDrinkItem extends AbstractCustomizableDrinkItem {

    protected SpecialtyDrinkItem(Properties settings) {
        super(Items.GLASS_BOTTLE, 0, settings);
    }

    @Override
    protected Item baseItem(ItemStack stack, Level level) {
        SpecialtyDrink drink = DrinkUtil.getSpecialDrink(stack, level);
        AbstractCustomizableDrinkItem base =
                (AbstractCustomizableDrinkItem) drink.getBaseItem(stack).getItem();
        return base.baseItem(stack, level);
    }

    @Override
    public double getTotalVolume(ItemStack stack, Level level) {
        try {
            SpecialtyDrink specialty = DrinkUtil.getSpecialDrink(stack, level);
            if (specialty.volume() != 0) {
                return specialty.volume();
            }
            ItemStack baseItem = specialty.getBaseItem(stack);
            if (baseItem.getItem() instanceof AbstractCustomizableDrinkItem drink) {
                return drink.getTotalVolume(baseItem, level);
            } else {
                return super.getTotalVolume(stack, level);
            }
        } catch (Exception e) {
            PDAPI.LOGGER.warn("Error getting total volume of {}", stack, e);
            return 0;
        }
    }

    @Override
    public float getChemicalContent(ResourceLocation name, ItemStack stack, Level level) {
        float amount;
        try {
            SpecialtyDrink specialty = DrinkUtil.getSpecialDrink(stack, level);
            amount = specialty.chemicals().getOrDefault(name, 0f);
            ItemStack baseItem = specialty.getBaseItem(stack);
            if (baseItem.getItem() instanceof AbstractCustomizableDrinkItem drink) {
                return amount + drink.getChemicalContent(name, baseItem, level);
            } else {
                return super.getChemicalContent(name, stack, level) + amount;
            }
        } catch (Exception e) {
            PDAPI.LOGGER.warn("Error getting amount of {} in {}", name, stack, e);
            return 0;
        }
    }

    @Override
    protected boolean shouldSip(ItemStack stack, Level level) {
        SpecialtyDrink drink = DrinkUtil.getSpecialDrink(stack, level);
        return !drink.noSip();
    }

    @Override
    protected Stream<OnDrinkAction> getActions(ItemStack stack, Level level) {
        SpecialtyDrink drink = DrinkUtil.getSpecialDrink(stack, level);
        return Streams.concat(drink.actions(level).stream(),
                super.getActions(stack, level));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag context) {
        if (world == null) return;
        tooltip.add(Component.translatable(DrinkUtil.getSpecialDrink(stack, world).name(world)).withStyle(ChatFormatting.AQUA));
        tooltip.add(Component.empty());

        super.appendHoverText(stack, world, tooltip, context);
    }

}
