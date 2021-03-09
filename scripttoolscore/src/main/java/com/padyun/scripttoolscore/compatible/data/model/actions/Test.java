package com.padyun.scripttoolscore.compatible.data.model.actions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by daiepngfei on 1/16/19
 */
public class Test {

    public static void main(String[] args) {
        final String s = "1855858980";

        System.out.println(Math.hypot(4, 4));
        System.out.println(Math.pow(Math.hypot(4, 4), 2));
//        List<C> list = new ArrayList<>();
//        list.add(new A());
//        list.add(new B());
//        String json = new Gson().toJson(list);
//        System.out.println(json);
//        List<C> list2 = new GsonBuilder().registerTypeAdapter(new TypeToken<List<C>>() {
//        }.getType(), new TypeAdapter<List<C>>() {
//            @Override
//            public void write(JsonWriter out, List<C> value) throws IOException {
//
//            }
//
//            @Override
//            public List<C> read(JsonReader in) throws IOException {
//                List<C> cs = new ArrayList<>();
//                in.beginArray();
//                while (in.hasNext()) {
//                    final String objectString = in.getCropPath();
//                    System.out.println(in.getCropPath());
//                    in.beginObject();
//                    Class<? extends C> cls = null;
//                    while (in.hasNext()) {
//                        final String name = in.nextName();
//                        if ("type".equals(name)) {
//                            final String value = in.nextString();
//                            switch (value) {
//                                case "A":
////                                    cs.add(new Gson().fromJson(objectString, A.class));
//                                    cls = A.class;
//                                    break;
//                                case "B":
//                                    cls = B.class;
////                                    cs.add(new Gson().fromJson(objectString, B.class));
//                                    break;
//                                default:
//                            }
//                        } else in.skipValue();
//                    }
//                    in.endObject();
//                }
//                in.endArray();
//                return cs;
//            }
//        }).toCreate().fromJson(json, new TypeToken<List<C>>() {
//        }.getType());
//        List<C> lis32 = new GsonBuilder().registerTypeAdapter(new TypeToken<List<C>>() {
//        }.getType(), new JsonDeserializer<List<C>>() {
//            @Override
//            public List<C> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext codec_context) throws JsonParseException {
//                List<C> cs = new ArrayList<>();
//                System.out.println(json.toString());
//                if (json.isJsonArray()) {
//                    JsonArray arr = json.getAsJsonArray();
//                    for (int i = 0; i < arr.size(); i++) {
//                        JsonElement element = arr.get(i);
//                        if (element == null) continue;
//                        JsonObject object = element.getAsJsonObject();
//                        if (object.has("type")) {
//                            Class<? extends C> cls = null;
//                            switch (object.get("type").getAsString()) {
//                                case "A":
//                                    cls = A.class;
//                                    break;
//                                case "A2":
//                                    cls = A2.class;
//                                    break;
//                                case "B":
//                                    cls = B.class;
//                                    break;
//                                case "B2":
//                                    cls = B2.class;
//                                    break;
//                                default:
//                            }
//                            if(cls != null) cs.add((C) codec_context.deserialize(element, cls));
//                        }
//                    }
//                }
//                return cs;
//            }
//        }).toCreate().fromJson(json, new TypeToken<List<C>>() {
//        }.getType());
//        System.out.println("haha");
//        for(C c : list2){
//            c.convert();
//        }
    }

    public  abstract class D<D>{
    }


    public static class C<T> {
//[{"type":"A","a":0,"b":0},{"type":"B","aa":"aa"}]
        public T convert(){
            return (T)this;
        }
    }

    public static class A extends C<A> {
        String type = "A";
        int a;
        int b;
        List<C> c2s = new ArrayList<>();

        A() {
            c2s.add(new A2());
            c2s.add(new B2());
        }
    }


    public static class B extends C<B> {
        String type = "B";
        String aa = "aa";
        String bb;
    }

    public static class C2 {
//[{"type":"A","a":0,"b":0},{"type":"B","aa":"aa"}]
    }

    public static class A2 extends C<A2> {
        String type = "A2";
        int a2 = 0;
    }


    public static class B2 extends C<B2> {
        String type = "B2";
        String aa2 = "aa2";
        String bb2;
    }

}
