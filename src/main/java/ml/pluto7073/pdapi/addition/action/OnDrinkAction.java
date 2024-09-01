package ml.pluto7073.pdapi.addition.action;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public interface OnDrinkAction {

    void onDrink(ItemStack stack, Level level, LivingEntity user);
    OnDrinkSerializer<?> serializer();

}
