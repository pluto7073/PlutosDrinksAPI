package ml.pluto7073.pdapi.item;

import ml.pluto7073.pdapi.addition.DrinkAdditionManager;
import ml.pluto7073.pdapi.specialty.SpecialtyDrinkManager;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * <bold>Temporary</bold> solution to not being able to access Level in <code>Item.appendHoverText()</code>
 * <br/>(End goal is to use Dynamic registries to house Drink Additions and Specialty drinks so that the
 * item components have direct access to the full Specialty Drink and Drink Addition data on demand but
 * that isn't working right now hence why {@link DrinkAdditionManager} and {@link SpecialtyDrinkManager}
 * still exist even in 1.21.1
 */
public interface LevelAccessTooltipProvider {

    /**
     * See {@link LevelAccessTooltipProvider}
     */
    void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag);

}
