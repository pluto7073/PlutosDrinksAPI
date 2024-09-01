package ml.pluto7073.pdapi.addition.action;

import com.google.gson.JsonObject;
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

        @Override
        public RestoreHungerAction fromJson(JsonObject json) {
            int food = GsonHelper.getAsInt(json, "food");
            int saturation = GsonHelper.getAsInt(json, "saturation");
            return new RestoreHungerAction(food, saturation);
        }

        @Override
        public void toJson(JsonObject json, RestoreHungerAction action) {
            json.addProperty("food", action.food);
            json.addProperty("saturation", action.saturation);
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
