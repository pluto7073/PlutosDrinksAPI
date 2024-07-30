package ml.pluto7073.pdapi.item;

import ml.pluto7073.pdapi.DrinkUtil;
import ml.pluto7073.pdapi.PDAPI;
import ml.pluto7073.pdapi.specialty.SpecialtyDrink;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

import java.util.Arrays;

@MethodsReturnNonnullByDefault
public class SpecialtyDrinkItem extends AbstractCustomizableDrinkItem {

    protected SpecialtyDrinkItem(Properties settings) {
        super(Items.GLASS_BOTTLE, Temperature.NORMAL, settings);
    }

    @Override
    public int getChemicalContent(String name, ItemStack stack) {
        int amount;
        try {
            amount = DrinkUtil.getSpecialDrink(stack).chemicals().get(name);
        } catch (Exception e) {
            amount = 0;
        }
        return super.getChemicalContent(name, stack) + amount;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity user) {
        SpecialtyDrink drink = DrinkUtil.getSpecialDrink(stack);

        Arrays.stream(drink.actions()).forEach(action -> action.onDrink(stack, level, user));

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
