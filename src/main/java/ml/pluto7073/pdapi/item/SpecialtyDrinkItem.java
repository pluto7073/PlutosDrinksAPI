package ml.pluto7073.pdapi.item;

import ml.pluto7073.pdapi.PDAPI;
import ml.pluto7073.pdapi.util.DrinkUtil;
import ml.pluto7073.pdapi.specialty.SpecialtyDrink;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

@MethodsReturnNonnullByDefault
public class SpecialtyDrinkItem extends AbstractCustomizableDrinkItem {

    protected SpecialtyDrinkItem(Properties settings) {
        super(Items.GLASS_BOTTLE, Temperature.NORMAL, settings);
    }

    @Override
    protected Item baseItem(ItemStack stack) {
        SpecialtyDrink drink = DrinkUtil.getSpecialDrink(stack);
        AbstractCustomizableDrinkItem base =
                (AbstractCustomizableDrinkItem) drink.getBaseItem(stack).getItem();
        return base.baseItem(stack);
    }

    @Override
    public float getChemicalContent(ResourceLocation name, ItemStack stack) {
        float amount;
        try {
            SpecialtyDrink specialty = DrinkUtil.getSpecialDrink(stack);
            amount = specialty.chemicals().getOrDefault(name, 0f);
            ItemStack baseItem = specialty.getBaseItem(stack);
            if (baseItem.getItem() instanceof AbstractCustomizableDrinkItem drink) {
                return amount + drink.getChemicalContent(name, baseItem);
            } else {
                return super.getChemicalContent(name, stack) + amount;
            }
        } catch (Exception e) {
            PDAPI.LOGGER.warn("Error getting amount of {} in {}", name, stack, e);
            return 0;
        }
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity user) {
        SpecialtyDrink drink = DrinkUtil.getSpecialDrink(stack);

        if (!level.isClientSide) drink.actions().forEach(action -> action.onDrink(stack, level, user));

        return super.finishUsingItem(stack, level, user);
    }

    @Override
    public String getDescriptionId(ItemStack stack) {
        try {
            SpecialtyDrink drink = DrinkUtil.getSpecialDrink(stack);
            return drink.name();
        } catch (Exception e) {
            return super.getDescriptionId(stack);
        }
    }

}
