package ml.pluto7073.pdapi.specialty;

import com.mojang.serialization.Codec;
import ml.pluto7073.pdapi.PDRegistries;
import net.minecraft.world.item.ItemStack;

public interface SpecialtyDrinkBase {

    Codec<SpecialtyDrinkBase> CODEC = PDRegistries.SPECIALTY_DRINK_BASE.byNameCodec().dispatch(SpecialtyDrinkBase::serializer, SpecialtyDrinkBaseSerializer::codec);

    ItemStack buildItemStack();
    boolean matches(ItemStack stack);
    SpecialtyDrinkBaseSerializer serializer();

}
