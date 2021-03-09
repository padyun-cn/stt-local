package com.padyun.scripttools.module.runtime;

import com.padyun.scripttools.biz.ui.content.Constants;
import com.padyun.scripttools.compat.data.CoScript;
import com.uls.utilites.content.CoreWorkers;
import com.padyun.scripttools.content.data.CustomScriptIOManager;
import com.padyun.scripttoolscore.compatible.data.model.SEImage;
import com.padyun.scripttoolscore.compatible.data.model.SEScript;
import com.uls.utilites.io.Files;
import com.uls.utilites.un.Useless;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import androidx.core.util.Consumer;

/**
 * Created by daiepngfei on 2020-05-20
 */
public class ScriptManager {

    private static final String DATA = Constants.Folder.SCRIPT;
    public static String stFileExt(){
        return ".ai";
    }

    public static boolean isScriptFileExtension(File f){
        return f != null && Files.ext(f.getAbsolutePath()).equals(stFileExt());
    }

    public static boolean isScriptFileExtension(String f){
        return f != null && Files.ext(f).equals(stFileExt());
    }

    @NotNull
    public static SFileDesc SFileDesc(@NotNull String dPath, long publishingCode) {
        return new SFileDesc(dPath, publishingCode);
    }

    public static class SFileDesc {
        private String path;
        private long verCode;

        SFileDesc(String path, long ver) {
            this.path = path;
            this.verCode = ver;
        }
    }

    private final Map<String, SFileDesc> mScriptDescs = new HashMap<>();

    private StContext context;
    ScriptManager(StContext context) {
        this.context = context;
    }

    File getScriptDataFileByName(String name) {
        return Useless.noEmptyStr(name) ? new File(currentScriptDir(), name) : null;
    }

    @NotNull
    public String currentScriptDir() {
        return context.getCurrentWorkingDir() + File.separator + DATA;
    }

    File[] getAllScriptFiles() {
        return new File(currentScriptDir()).listFiles();
    }

    /**
     *
     * @param f
     * @return
     */
    public void load(File f, Consumer<CoScript> coScriptConsumer) {
        CustomScriptIOManager.getCoScriptFromFileAsync(f.getAbsolutePath(), coScriptConsumer);
    }

    /**
     *
     * @param script
     * @param accepter
     */
    public void foreachAllImages(CoScript script, Consumer<SEImage> accepter) {
        if(script != null && accepter != null) {
            final SEScript seScript = script.buildToSEScript();
            if (seScript != null) {
                seScript.forEachAllSEImgs(accepter);
            }
        }
    }

    /**
     *
     * @param script
     */
    public void save(CoScript script) {
        save(script, null);
    }

    /**
     *
     * @param script
     * @param iResult
     */
    public void save(CoScript script, CoreWorkers.IResult<Boolean> iResult) {
        CustomScriptIOManager.saveWithFilePath(context.getCurrentWorkingDir(), script, iResult);
    }

    /**
     *
     * @param id
     * @param desc
     */
    public void registerOrUpdateScriptDesc(String id, SFileDesc desc){
        if(Useless.noEmptyStr(id) && desc != null) {
            synchronized (mScriptDescs) {
                mScriptDescs.put(id, desc);
            }
        }
    }

    /**
     *
     * @param id
     */
    public boolean hasLocalTask(@NotNull String id) {
        synchronized (mScriptDescs) {
            return mScriptDescs.containsKey(id);
        }
    }

}
