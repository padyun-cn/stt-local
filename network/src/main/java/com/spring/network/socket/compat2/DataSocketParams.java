package com.spring.network.socket.compat2;

import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 * Created by daiepngfei on 11/11/19
 */
@SuppressWarnings("WeakerAccess")
public class DataSocketParams {
    private ArrayList<byte[]> params = new ArrayList<>();

    /**
     * Constructor
     */
    public DataSocketParams(){}

    /**
     * Constructor
     * @param params
     */
    public DataSocketParams(DataSocketParams params){
        addParam(params);
    }

    public DataSocketParams(ByteBuffer buffer){
        if(buffer != null){
            addParam(buffer.array());
        }
    }

    /**
     * Constructor
     * @param data
     */
    public DataSocketParams(byte[]... data){
        addParam(data);
    }

    /**
     *
     * @param params
     */
    public void addParam(DataSocketParams params) {
        if(params != null && params.getParams() != null){
            for(byte[] data : params.getParams()){
                if(data != null && data.length > 0) {
                    params.addParam(data);
                }
            }
        }
    }


    public void addParam(byte[]... data){
        if(data != null){
            for (byte[] d : data) {
                if (d == null) {
                    continue;
                }
                params.add(d);
            }
        }
    }

    /**
     *
     * @return
     */
    public ArrayList<byte[]> getParams() {
        return params;
    }

    /**
     *
     * @return
     */
    public boolean isEmpty() {
        return params == null || params.size() == 0;
    }
}
