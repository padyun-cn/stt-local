package com.padyun.scripttools.module.runtime;

import com.uls.utilites.un.Useless;
import com.padyun.scripttools.content.data.FPathScript;

import java.io.File;

/**
 * Created by daiepngfei on 2020-05-20
 */
class PathManager {

    private String baseDir;
    private StContext context;
    PathManager(StContext context) {
        this.baseDir = FPathScript.getScriptAppCompatSDCardRootDirectory() + "/plugins/script";
        this.context = context;
    }

    String getWorkingDir() {
        final String commonId = context.getManifest().getUserId();
        final String gameId = context.getManifest().getGameId();
        Useless.assertNoEmptyStr(commonId, gameId);
        return baseDir + File.separator + commonId + File.separator + gameId;
    }





}
