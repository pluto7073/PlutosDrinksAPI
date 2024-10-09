package ml.pluto7073.pdapi.addition.action;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class RestoreHungerAction implements OnDrinkAction {

    private final int food, saturation;

    public RestoreHungerAction(int food, int saturation) {
        this.food = food;
        this.saturation = saturation;
    }

    @Override
    public void onDrink(ItemStack stack, Level level, LivingEntity user) {
        if (!(user instanceof Player player)) return;
        player.getFoodData().eat(food, saturation);
    }

    @Override
    public OnDrinkSerializer<?> serializer() {
        return OnDrinkSerializers.RESTORE_HUNGER;
    }

    public static class Serializer implements OnDrinkSerializer<RestoreHungerAction> {

        public static final Codec<RestoreHungerAction> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(Codec.INT.fieldOf("food").forGetter(action -> action.food),
                        Codec.INT.fieldOf("saturation").forGetter(action -> action.saturation))
                        .apply(instance, RestoreHungerAction::new));

        @Override
        public Codec<RestoreHungerAction> codec() {
            return CODEC;
        }

        @Override
        public RestoreHungerAction fromNetwork(FriendlyByteBuf buf) {
            int food = buf.readInt();
            int saturation = buf.readInt();
            return new RestoreHungerAction(food, saturation);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, RestoreHungerAction action) {
            buf.writeInt(action.food);
            buf.writeInt(action.saturation);
        }

    }

}
