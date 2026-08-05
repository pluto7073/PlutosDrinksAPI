package ml.pluto7073.pdapi.addition.action;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * An onDrinkAction that is used to tell the mod not to sip a certain drink and drink it all at once.  Can only be used in specialty drinks
 */
public class NoSipAction implements OnDrinkAction {

    NoSipAction() {}

    @Override
    public void onDrink(ItemStack stack, Level level, LivingEntity user) {

    }

    @Override
    public OnDrinkSerializer<?> serializer() {
        return OnDrinkSerializers.NO_SIP;
    }

}
