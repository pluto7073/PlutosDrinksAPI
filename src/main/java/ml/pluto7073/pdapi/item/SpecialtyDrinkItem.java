package ml.pluto7073.pdapi.item;

import ml.pluto7073.pdapi.PDAPI;
import ml.pluto7073.pdapi.component.PDComponents;
import ml.pluto7073.pdapi.specialty.SpecialtyDrink;
import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

@MethodsReturnNonnullByDefault
public class SpecialtyDrinkItem extends AbstractCustomizableDrinkItem {

    protected SpecialtyDrinkItem(Properties settings) {
        super(Items.GLASS_BOTTLE, 0, settings);
    }

    @Override
    protected Item baseItem(ItemStack stack, HolderLookup.Provider provider) {
        Holder<SpecialtyDrink> drink = stack.getOrDefault(PDComponents.SPECIALTY_DRINK, SpecialtyDrink.EMPTY);
        AbstractCustomizableDrinkItem base =
                (AbstractCustomizableDrinkItem) drink.value().getBaseItem(stack, provider).getItem();
        return base.baseItem(stack, provider);
    }

    @Override
    public double getTotalVolume(ItemStack stack, HolderLookup.Provider provider) {
        try {
            Holder<SpecialtyDrink> specialty = stack.getOrDefault(PDComponents.SPECIALTY_DRINK, SpecialtyDrink.EMPTY);
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
            Holder<SpecialtyDrink> specialty = stack.getOrDefault(PDComponents.SPECIALTY_DRINK, SpecialtyDrink.EMPTY);
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
    protected boolean shouldSip(ItemStack stack) {
        Holder<SpecialtyDrink> drink = stack.getOrDefault(PDComponents.SPECIALTY_DRINK, SpecialtyDrink.EMPTY);
        return !drink.value().noSip();
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity user) {
        Holder<SpecialtyDrink> drink = stack.getOrDefault(PDComponents.SPECIALTY_DRINK, SpecialtyDrink.EMPTY);

        ItemStack newStack = super.finishUsingItem(stack, level, user);

        if (!level.isClientSide && newStack.isEmpty()) drink.value().actions(level.registryAccess()).forEach(action -> action.onDrink(stack, level, user));

        return newStack;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        Holder<SpecialtyDrink> holder = stack.getOrDefault(PDComponents.SPECIALTY_DRINK, SpecialtyDrink.EMPTY);
        if (holder instanceof Holder.Direct<SpecialtyDrink>) {
            super.appendHoverText(stack, context, tooltip, flag);
        }
        ResourceKey<SpecialtyDrink> id = holder.unwrapKey().orElseThrow();
        String key = id.location().toLanguageKey("drink");
        if (holder.value().name().isPresent()) {
            key = holder.value().name().get();
        }
        tooltip.add(Component.translatable(key).withStyle(ChatFormatting.AQUA));
        tooltip.add(Component.empty());
        super.appendHoverText(stack, context, tooltip, flag);
    }

}
