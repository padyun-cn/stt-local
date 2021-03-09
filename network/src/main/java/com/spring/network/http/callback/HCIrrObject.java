package com.spring.network.http.callback;

import com.google.gson.Gson;

/**
 * Created by daiepngfei on 2020-08-05
 */
public class HCIrrObject<Type> {
    private Class<Type> cls;
    private Parser<Type> parser;
    private Type obj;

    @SuppressWarnings("unused")
    private HCIrrObject() {
    }

    public HCIrrObject(Class<Type> cls) {
        this(cls, null);
    }


    public HCIrrObject(Class<Type> cls, Parser<Type> parser) {
        this.cls = cls;
        this.parser = parser;
    }

    void onIrrParse(String objString) {
        if (cls != null && objString != null) {
            if(cls == String.class){
                //noinspection unchecked
                obj = (Type) objString;
            } else if (parser != null) {
                obj = parser.parse(objString, cls);
            } else {
                obj = new Gson().fromJson(objString, cls);
            }
        }
    }

    public Type getObj(Class<?> cls) {
        return cls == this.cls ? obj : null;
    }

    public interface Parser<TypeOf> {
        TypeOf parse(String source, Class<TypeOf> typeOfClass);
    }

}
