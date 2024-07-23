package ml.pluto7073.pdapi.networking;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.network.FriendlyByteBuf;

import java.util.Map;
import java.util.Stack;

public final class NetworkingUtils {

    private static final int INT_TYPE = 0,
            FLOAT_TYPE = 1,
            BOOLEAN_TYPE = 2,
            STRING_TYPE = 3,
            ARRAY_BEGIN_TYPE = 4,
            ARRAY_END_TYPE = 5,
            OBJECT_BEGIN_TYPE = 6,
            OBJECT_END_TYPE = 7,
            KEY_TYPE = 8;

    public static JsonObject readJsonObject(FriendlyByteBuf buffer) {
        Stack<Integer> stack = new Stack<>();
        stack.push(OBJECT_BEGIN_TYPE);
        int i = buffer.readInt();
        if (i != OBJECT_BEGIN_TYPE) throw new IllegalStateException("Expected object initializer as first signal");
        return readJsonObject(buffer, stack);
    }

    private static JsonObject readJsonObject(FriendlyByteBuf buffer, Stack<Integer> objectLevels) {
        JsonObject object = new JsonObject();
        String currentKey = "";
        loop: while (true) {

            int signal = buffer.readInt();

            switch (signal) {
                case INT_TYPE -> object.add(currentKey, new JsonPrimitive(buffer.readInt()));
                case FLOAT_TYPE -> object.add(currentKey, new JsonPrimitive(buffer.readFloat()));
                case BOOLEAN_TYPE -> object.add(currentKey, new JsonPrimitive(buffer.readBoolean()));
                case STRING_TYPE -> object.add(currentKey, new JsonPrimitive(buffer.readUtf()));
                case ARRAY_BEGIN_TYPE -> {
                    objectLevels.push(ARRAY_BEGIN_TYPE);
                    object.add(currentKey, readJsonArray(buffer, objectLevels));
                    objectLevels.pop();
                }
                case ARRAY_END_TYPE -> throw new IllegalStateException("Unexpected array ending");
                case OBJECT_BEGIN_TYPE -> {
                    objectLevels.push(OBJECT_BEGIN_TYPE);
                    object.add(currentKey, readJsonObject(buffer, objectLevels));
                    objectLevels.pop();
                }
                case OBJECT_END_TYPE -> {
                    if (objectLevels.isEmpty()) throw new IllegalStateException("Unexpected object ending");
                    if (objectLevels.peek() != OBJECT_BEGIN_TYPE) throw new IllegalStateException("Unexpected object ending");
                    break loop;
                }
                case KEY_TYPE -> currentKey = buffer.readUtf();
            }
        }
        return object;
    }

    private static JsonArray readJsonArray(FriendlyByteBuf buffer, Stack<Integer> objectLevels) {
        JsonArray array = new JsonArray();
        loop: while (true) {

            int signal = buffer.readInt();

            switch (signal) {
                case INT_TYPE -> array.add(buffer.readInt());
                case FLOAT_TYPE -> array.add(buffer.readFloat());
                case BOOLEAN_TYPE -> array.add(buffer.readBoolean());
                case STRING_TYPE -> array.add(buffer.readUtf());
                case ARRAY_BEGIN_TYPE -> {
                    objectLevels.push(ARRAY_BEGIN_TYPE);
                    array.add(readJsonArray(buffer, objectLevels));
                    objectLevels.pop();
                }
                case ARRAY_END_TYPE -> {
                    if (objectLevels.isEmpty()) throw new IllegalStateException("Unexpected Array Ending");
                    if (objectLevels.peek() != ARRAY_BEGIN_TYPE) throw new IllegalStateException("Unexpected Array Ending");
                    break loop;
                }
                case OBJECT_BEGIN_TYPE -> {
                    objectLevels.push(OBJECT_BEGIN_TYPE);
                    array.add(readJsonObject(buffer, objectLevels));
                    objectLevels.pop();
                }
                case OBJECT_END_TYPE -> throw new IllegalStateException("Unexpected Object Ending");
                case KEY_TYPE -> throw new IllegalStateException("Unexpected Key Type");
            }
        }
        return array;
    }

    public static void writeJsonObjectStart(FriendlyByteBuf buf, JsonObject object) {
        buf.writeInt(OBJECT_BEGIN_TYPE);
        writeJsonObject(buf, object);
    }

    private static void writeJsonObject(FriendlyByteBuf buf, JsonObject object) {
        for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
            buf.writeInt(KEY_TYPE);
            buf.writeUtf(entry.getKey());

            JsonElement value = entry.getValue();
            writeValue(value, buf);
        }

        buf.writeInt(OBJECT_END_TYPE);
    }

    private static void writeJsonArray(FriendlyByteBuf buf, JsonArray array) {
        for (JsonElement value : array) {
            writeValue(value, buf);
        }
        buf.writeInt(ARRAY_END_TYPE);
    }

    private static void writeValue(JsonElement value, FriendlyByteBuf buf) {
        if (value.isJsonArray()) {
            buf.writeInt(ARRAY_BEGIN_TYPE);
            writeJsonArray(buf, value.getAsJsonArray());
        } else if (value.isJsonObject()) {
            buf.writeInt(OBJECT_BEGIN_TYPE);
            writeJsonObject(buf, value.getAsJsonObject());
        } else if (value.isJsonPrimitive()) {
            JsonPrimitive primVal = value.getAsJsonPrimitive();
            if (primVal.isBoolean()) {
                buf.writeInt(BOOLEAN_TYPE);
                buf.writeBoolean(primVal.getAsBoolean());
            } else if (primVal.isNumber()) {
                if (primVal.getAsNumber().intValue() == primVal.getAsNumber().floatValue()) {
                    buf.writeInt(INT_TYPE);
                    buf.writeInt(primVal.getAsInt());
                } else {
                    buf.writeInt(FLOAT_TYPE);
                    buf.writeFloat(primVal.getAsFloat());
                }
            } else if (primVal.isString()) {
                buf.writeInt(STRING_TYPE);
                buf.writeUtf(primVal.getAsString());
            }
        }
    }

}
