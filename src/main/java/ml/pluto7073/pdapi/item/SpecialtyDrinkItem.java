package ml.pluto7073.pdapi.item;

import ml.pluto7073.pdapi.PDAPI;
import ml.pluto7073.pdapi.util.DrinkUtil;
import ml.pluto7073.pdapi.specialty.SpecialtyDrink;
import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
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

@MethodsReturnNonnullByDefault
public class SpecialtyDrinkItem extends AbstractCustomizableDrinkItem {

    protected SpecialtyDrinkItem(Properties settings) {
        super(Items.GLASS_BOTTLE, 0, settings);
    }

    @Override
    protected Item baseItem(ItemStack stack, HolderLookup.Provider provider) {
        Holder<SpecialtyDrink> drink = DrinkUtil.getSpecialDrink(stack);
        AbstractCustomizableDrinkItem base =
                (AbstractCustomizableDrinkItem) drink.value().getBaseItem(stack, provider).getItem();
        return base.baseItem(stack, provider);
    }

    @Override
    public double getTotalVolume(ItemStack stack, HolderLookup.Provider provider) {
        try {
            Holder<SpecialtyDrink> specialty = DrinkUtil.getSpecialDrink(stack);
            if (specialty.value().volume() != 0) {
                return specialty.value().volume();
            }
            ItemStack baseItem = specialty.value().getBaseItem(stack, provider);
            if (baseItem.getItem() instanceof AbstractCustomizableDrinkItem drink) {
                return drink.getTotalVolume(baseItem, provider);
            } else {
                return super.getTotalVolume(stack, provider);
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
            Holder<SpecialtyDrink> specialty = DrinkUtil.getSpecialDrink(stack);
            amount = specialty.value().chemicals().getOrDefault(name, 0f);
            ItemStack baseItem = specialty.value().getBaseItem(stack, level.registryAccess());
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
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity user) {
        Holder<SpecialtyDrink> drink = DrinkUtil.getSpecialDrink(stack);

        if (!level.isClientSide) drink.value().actions(level.registryAccess()).forEach(action -> action.onDrink(stack, level, user));

        return super.finishUsingItem(stack, level, user);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        Holder<SpecialtyDrink> drink = DrinkUtil.getSpecialDrink(stack);
        if (drink == SpecialtyDrink.EMPTY) return;
        String key = drink.value().name().orElse(drink.unwrapKey().orElseThrow().location().toLanguageKey("drink"));
        tooltip.add(Component.translatable(key).withStyle(ChatFormatting.AQUA));
        tooltip.add(Component.empty());

        super.appendHoverText(stack, context, tooltip, flag);
    }

}
