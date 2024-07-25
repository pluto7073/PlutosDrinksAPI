package ml.pluto7073.pdapi.addition;

import com.google.gson.JsonObject;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public interface OnDrink {

    void onDrink(ItemStack stack, Level level, LivingEntity user);

    JsonObject toJson();

}
