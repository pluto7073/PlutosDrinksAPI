package ml.pluto7073.pdapi.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public final class JsonBuilder {

    private JsonBuilder() {}

    public static ObjectBuilder object() {
        return new ObjectBuilder();
    }

    public static ArrayBuilder array() { return new ArrayBuilder(); }

    public static JsonArray createFabricModLoadedConditions(String... requiredMods) {
        JsonArray mods = new JsonArray();
        for (String mod : requiredMods) {
            mods.add(mod);
        }
        return JsonBuilder.array()
                .add(JsonBuilder.object()
                        .put("condition", "fabric:all_mods_loaded")
                        .put("values", mods))
                .build();
    }

    public static class ArrayBuilder {

        private final JsonArray array = new JsonArray();

        private ArrayBuilder() {}

        public ArrayBuilder add(JsonElement e) {
            array.add(e);
            return this;
        }

        public ArrayBuilder add(String s) {
            array.add(s);
            return this;
        }

        public ArrayBuilder add(Number n) {
            array.add(n);
            return this;
        }

        public ArrayBuilder add(boolean b) {
            array.add(b);
            return this;
        }

        public ArrayBuilder add(char c) {
            array.add(c);
            return this;
        }

        public ArrayBuilder add(ObjectBuilder b) {
            return add(b.build());
        }

        public ArrayBuilder add(ArrayBuilder b) {
            return add(b.build());
        }

        public JsonArray build() { return array; }

    }

    public static class ObjectBuilder {

        private final JsonObject object = new JsonObject();

        private ObjectBuilder() {}

        public ObjectBuilder put(String key, JsonElement element) {
            object.add(key, element);
            return this;
        }

        public ObjectBuilder put(String key, ObjectBuilder builder) {
            return put(key, builder.build());
        }

        public ObjectBuilder put(String key, ArrayBuilder builder) {
            return put(key, builder.build());
        }

        public ObjectBuilder put(String key, String element) {
            object.addProperty(key, element);
            return this;
        }

        public ObjectBuilder put(String key, Number element) {
            object.addProperty(key, element);
            return this;
        }

        public ObjectBuilder put(String key, boolean element) {
            object.addProperty(key, element);
            return this;
        }

        public ObjectBuilder put(String key, char element) {
            object.addProperty(key, element);
            return this;
        }

        public JsonObject build() { return object; }

    }

}

