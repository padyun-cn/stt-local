package com.padyun.scripttools.content.data;

import com.padyun.scripttoolscore.compatible.data.model.IDGen;

/**
 * Created by daiepngfei on 11/17/19
 */
public class CoIDGen {

    /**
     *
     * @return
     */
    public static String genScriptID(){
        return IDGen.genTmsStrRandom("script_");
    }
}
