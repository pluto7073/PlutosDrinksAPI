package ml.pluto7073.pdapi.item;

import ml.pluto7073.pdapi.PDAPI;
import ml.pluto7073.pdapi.util.DrinkUtil;
import ml.pluto7073.pdapi.specialty.SpecialtyDrink;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

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
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity user) {
        SpecialtyDrink drink = DrinkUtil.getSpecialDrink(stack, level);

        if (!level.isClientSide) drink.actions(level).forEach(action -> action.onDrink(stack, level, user));

        return super.finishUsingItem(stack, level, user);
    }

    @Override
    public String getDescriptionId(ItemStack stack) {
        try {
            if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT && Minecraft.getInstance().level != null) {
                Level level = Minecraft.getInstance().level;
                return DrinkUtil.getSpecialDrink(stack, level).name(level);
            } else {
                return new ResourceLocation(stack.getOrCreateTag().getString("Drink")).toLanguageKey("drink");
            }
        } catch (Exception e) {
            return super.getDescriptionId(stack);
        }
    }

}
