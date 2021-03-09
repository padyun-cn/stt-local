package com.padyun.scripttools.content;

import com.padyun.scripttools.content.data.SceEncryptor;

/**
 * Created by daiepngfei on 9/30/19
 */
public class Test {

    public static class Book {
        public Book(String name, String author){
            this.name = name;
            this.author = author;
        }
        String name;
        String author;
    }
    public static void main(String[] args) {

        byte a = -1;
        final String hi = "hi123567ojn";
        System.out.println( hi);
        byte[] data = SceEncryptor.encrypt(hi);
        System.out.println( hi);
        System.out.println("data.length : " + data.length + ", hi : " + hi);
        final String resultHi = SceEncryptor.decrypt(data);
        System.out.println("data.length : " + data.length + ", resultHi : " + resultHi);


        /*Matrix mat = new Matrix();
        System.out.println(mat.toString());*/

        /*final String json = "{\"id\":\"script_1575425244119_837\",\"scenes\":[{\"condition_list\":[{\"action_list\":[{\"delay\":2000,\"image_detail\":{\"fileName\":\"crop_1576045053050_344.png\",\"id\":\"image1576045053050_718\",\"info\":{\"flag\":1,\"h\":65,\"maxval\":255,\"sim\":100,\"threshold\":150,\"type\":0,\"w\":66,\"x\":1148,\"y\":556},\"name\":\"\",\"original\":\"/storage/emulated/0/YPEditor/cache/plugins/script/383273/101/image_cache/screen/screen_1576045051786_290.png\",\"path\":\"/storage/emulated/0/YPEditor/cache/plugins/script/383273/101/image_cache/crop/crop_1576045053050_344.png\",\"signature\":\"1576045053050\"},\"image_info\":\"crop_1576045053050_344.png\",\"shift_x\":0,\"shift_y\":0,\"type\":\"image\",\"se_type\":\"action_image\"}],\"condition\":0,\"condition_id\":\"condition_id_1576047172724_496\",\"item_list\":[{\"id\":\"item_image1576047172725_342\",\"image_detail\":{\"fileName\":\"crop_1576045053050_344.png\",\"id\":\"image1576045053050_718\",\"info\":{\"flag\":1,\"h\":65,\"maxval\":255,\"sim\":100,\"threshold\":150,\"type\":0,\"w\":66,\"x\":1148,\"y\":556}},\"image_info\":\"crop_1576045053050_344.png\",\"state\":1,\"timeout\":0,\"type\":\"image\",\"relation\":1,\"se_type\":\"item_image\"}],\"name\":\"无名\"},{\"action_list\":[{\"delay\":1000,\"duration\":1000,\"end_coord\":{\"type\":\"fixed\",\"x\":766,\"y\":350,\"se_type\":\"coord_fixed\"},\"end_pointf_x\":0.0,\"end_pointf_y\":0.0,\"end_range\":{\"h\":13,\"type\":\"size\",\"w\":14,\"se_type\":\"range_size\"},\"orignalPath\":\"/storage/emulated/0/YPEditor/cache/plugins/script/383273/101/image_cache/screen/screen_1576045476126_464.png\",\"start_coord\":{\"type\":\"fixed\",\"x\":538,\"y\":354,\"se_type\":\"coord_fixed\"},\"start_pointf_x\":0.0,\"start_pointf_y\":0.0,\"start_range\":{\"h\":13,\"type\":\"size\",\"w\":14,\"se_type\":\"range_size\"},\"type\":\"slide\",\"se_type\":\"action_slide\"}],\"condition\":0,\"condition_id\":\"condition_id_1576047172748_743\",\"item_list\":[{\"id\":\"item_image1576047172748_151\",\"image_detail\":{\"fileName\":\"crop_1576045477360_429.png\",\"id\":\"image1576045477360_26\",\"info\":{\"flag\":52,\"h\":50,\"maxval\":255,\"sim\":80,\"threshold\":150,\"type\":0,\"w\":39,\"x\":526,\"y\":336}},\"image_info\":\"crop_1576045477360_429.png\",\"state\":1,\"timeout\":0,\"type\":\"image\",\"relation\":1,\"se_type\":\"item_image\"}],\"name\":\"无名\"}],\"scene_id\":0}]}";
        final SEScript seScript = SEDataUtils.fromJson(json, SEScript.class);

        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(SEImage.class, new JsonSerializer<SEImage>() {
            @Override
            public JsonElement serialize(SEImage src, Type typeOfSrc, JsonSerializationContext codec_context) {
                JsonObject obj = (JsonObject) new Gson().toJsonTree(src);
                obj.remove("original");
                obj.remove("path");
                obj.remove("signature");
                obj.remove("name");
                return obj;
            }

        });
        builder.registerTypeAdapter(SEActionSlide.class, (JsonSerializer<SEActionSlide>) (src, typeOfSrc, codec_context) -> {
            JsonObject obj = (JsonObject) new Gson().toJsonTree(src);
            obj.remove("orignalPath");
            return obj;
        });*/
//        builder.registerTypeAdapter(Object.class, new JsonSerializer<Object>() {
//            @Override
//            public JsonElement serialize(Object src, Type typeOfSrc, JsonSerializationContext codec_context) {
//                JsonObject obj = (JsonObject) new Gson().toJsonTree(src);
//                if(src instanceof SEImage){
//                    obj.remove("original");
//                    obj.remove("path");
//                    obj.remove("signature");
//                    obj.remove("name");
//                } else if(src instanceof SEAction) {
//                    if (obj.has("orignalPath")) {
//                        obj.remove("orignalPath");
//                    }
//                }
//                return obj;
//            }
//
//        });

//        builder.registerTypeAdapter(Book.class, new TypeAdapter<Book>() {
//
//            @Override
//            public void write(JsonWriter out, Book value) throws IOException {
//                out.beginObject();
//                out.name("BookName").value(value.name);
//                out.endObject();
//            }
//
//            @Override
//            public Book read(JsonReader in) throws IOException {
//                return null;
//            }
//        });

        //System.out.println(builder.create().toJson(new Book("Alan's New Book", "Alan Dai")));
//        System.out.println(builder.create().toJson(seScript));
//        int a = 2;
//        int flag = 0;
//        int b = 4;
//        flag |= a;
//        System.out.println(flag);
//        flag &= ~a;
//        System.out.println(flag);
//        flag |= b;
//        System.out.println(flag);
//        flag &= ~b;
//        System.out.println(flag);
//        flag |= a;
//        flag |= b;
//        System.out.println(flag);
//        flag &= ~b;
//        System.out.println(flag);
//
//        final short i = 12410;
//        byte[] bytes = new byte[2];
//        bytes[0] = (byte) (i >>> 8);
//        bytes[1] = (i & 0x00FF);
//        System.out.println("b0 " + bytes[0]);
//        System.out.println("b1 " + bytes[1]);
//        short r = 0;
//        r = (short) ((((short) bytes[0]) << 8) | ((short) bytes[1]));
//        System.out.println("r is " + r);
//        final Object s = new Object();
//        T t = new T();
//        int count = 0;
//        t.start();
//        synchronized (t.waiter) {
//            try {
//                t.waiter.wait();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            while (count < 6) {
//                System.out.println("88" + (++count));
//                try {
//                    Thread.sleep(500);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//            t.waiter.notify();
//        }


    }


    static class T extends Thread {
        int count;
        private final Object waiter;

        T() {
            this.waiter = new Object();
        }

        public void awake() {
            synchronized (waiter) {
                waiter.notifyAll();
            }
        }

        public void await() {
            try {
                waiter.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            while (true) {
                synchronized (waiter) {
                    if (count > 6) {
                        awake();
                    }
                    System.out.println("haha : " + (++count));
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
