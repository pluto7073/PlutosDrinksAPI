package ml.pluto7073.pdapi.networking;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import ml.pluto7073.pdapi.PDAPI;
import ml.pluto7073.pdapi.PDRegistries;
import ml.pluto7073.pdapi.addition.DrinkAddition;
import ml.pluto7073.pdapi.addition.action.OnDrinkAction;
import ml.pluto7073.pdapi.addition.action.OnDrinkSerializer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamDecoder;
import net.minecraft.network.codec.StreamEncoder;
import net.minecraft.resources.ResourceLocation;

import java.util.*;
import java.util.function.Function;

public final class NetworkingUtils {

    public static <T> void arrayToNetwork(FriendlyByteBuf buf, T[] array, StreamEncoder<? super FriendlyByteBuf, T> itemWriter) {
        HashMap<Integer, T> intMap = new HashMap<>();
        for (int i = 0; i < array.length; i++) {
            intMap.put(i, array[i]);
        }
        buf.writeMap(intMap, FriendlyByteBuf::writeInt, itemWriter);
    }

    public static <T> List<T> listFromNetwork(FriendlyByteBuf buf, StreamDecoder<? super FriendlyByteBuf, T> itemReader) {
        Map<Integer, T> intMap = buf.readMap(FriendlyByteBuf::readInt, itemReader);
        List<T> list = new ArrayList<>();
        for (int i = 0; i < intMap.size(); i++) {
            list.add(intMap.get(i));
        }
        return list;
    }

}
