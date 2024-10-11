package ml.pluto7073.pdapi.entity;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.player.Player;

public class PDTrackedData {

    public static EntityDataAccessor<Float> PLAYER_CAFFEINE_AMOUNT;
    public static EntityDataAccessor<Float> PLAYER_ORIGINAL_CAFFEINE_AMOUNT;
    public static EntityDataAccessor<Integer> PLAYER_TICKS_SINCE_LAST_CAFFEINE;

    public static void init() {
        PLAYER_CAFFEINE_AMOUNT = SynchedEntityData.defineId(Player.class, EntityDataSerializers.FLOAT);
        PLAYER_ORIGINAL_CAFFEINE_AMOUNT = SynchedEntityData.defineId(Player.class, EntityDataSerializers.FLOAT);
        PLAYER_TICKS_SINCE_LAST_CAFFEINE = SynchedEntityData.defineId(Player.class, EntityDataSerializers.INT);
    }

}
