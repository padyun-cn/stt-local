package com.padyun.scripttoolscore.tools;

import com.google.gson.Gson;
import com.padyun.scripttoolscore.compatible.ScriptCons;
import com.padyun.scripttoolscore.compatible.data.model.ImageInfo;
import com.padyun.scripttoolscore.compatible.data.model.SECondition;
import com.padyun.scripttoolscore.compatible.data.model.SEImage;
import com.padyun.scripttoolscore.compatible.data.model.SEScene;
import com.padyun.scripttoolscore.compatible.data.model.SEScript;
import com.padyun.scripttoolscore.compatible.data.model.actions.SEAction;
import com.padyun.scripttoolscore.compatible.data.model.actions.SEActionBrain;
import com.padyun.scripttoolscore.compatible.data.model.actions.SEActionImage;
import com.padyun.scripttoolscore.compatible.data.model.brain.SEBrainCounter;
import com.padyun.scripttoolscore.compatible.data.model.item.SEItem;
import com.padyun.scripttoolscore.compatible.data.model.item.SEItemBrain;
import com.padyun.scripttoolscore.compatible.data.model.item.SEItemImage;
import com.padyun.scripttoolscore.compatible.data.util.SEModelUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


/**
 * Created by daiepngfei on 2020-06-06
 */
public class SceTools {

    /**
     * @param targetZipFile
     * @param unzippedDir
     *
     * @return
     *
     * @throws InterruptedException
     * @throws IOException
     */
    public static String compatUnzip(File targetZipFile, File unzippedDir) throws Exception {

        if (targetZipFile == null || unzippedDir == null || unzippedDir.isFile()) {
            throw new IOException("File not found or directory error ! ");
        }

        if (!unzippedDir.exists()) {
            unzippedDir.mkdirs();
        }

        File aiFile = null;
        try(ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(targetZipFile))) {
            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                // interruption
                if (Thread.currentThread().isInterrupted()) {
                    throw new InterruptedException();
                }

                // do working
                final File destFile = new File(unzippedDir, entry.getName());

                if (!entry.isDirectory()) {
                    try(FileOutputStream fileOutputStream = new FileOutputStream(destFile)) {
                        final byte[] readBuffer = new byte[8192];
                        int nread;
                        while ((nread = zipInputStream.read(readBuffer)) != -1) {
                            fileOutputStream.write(readBuffer, 0, nread);
                        }
                        //
                        if (destFile.getName().endsWith(".ai")) {
                            aiFile = destFile;
                        }
                    }
                }
            }
        }
        return compatibleParseAiFileToSEScriptData(aiFile, unzippedDir);
    }

    /**
     * @param aiFile
     *
     * @return
     *
     * @throws IOException
     */
    public static String compatibleParseAiFileToSEScriptData(File aiFile, File dir) throws Exception {
        final String scriptJson = SceCryptor.decrypt(readFile(aiFile));
        try {
            final JSONObject scriptJSONObject = new JSONObject(scriptJson);
            final JSONArray conditionArray = scriptJSONObject.getJSONObject("scene").getJSONArray("condition_list");

            if (conditionArray != null && conditionArray.length() > 0) {
                final SEScript script = new SEScript(scriptJSONObject.getString("id"));
                final SEScene seScene = new SEScene();
                script.addScene(seScene);
                int brainCounter = 0;
                for (int i = 0; i < conditionArray.length(); i++) {

                    final JSONObject condJObj = conditionArray.getJSONObject(i);
                    if (condJObj == null || condJObj.optBoolean("disabled")) {
                        continue;
                    }

                    // toCreate se-condition
                    SECondition condition = SEModelUtil.fromJson(condJObj.toString(), SECondition.class);
                    if (condition == null) {
                        throw new JSONException("bad condition");
                    }

                    final int count = condJObj.getInt("co_brain_count");
                    if (count > 0) {
                        // toCreate new brain counter
                        SEBrainCounter counter = new SEBrainCounter();
                        counter.setId(++brainCounter);
                        counter.setThreshold(count);

                        // add new counter into script
                        script.addBrain(counter);

                        // -------item-------
                        // toCreate new brain item
                        SEItemBrain itemBrain = new SEItemBrain();
                        itemBrain.setRelation(ScriptCons.JRelation.FLAG_AND_NOT);
                        itemBrain.setId(counter.getId());

                        // add item into condition
                        condition.addItem(itemBrain);

                        // -------action-------
                        SEActionBrain actionBrain = new SEActionBrain();
                        actionBrain.setAction(SEActionBrain.ACTION_PLUS);
                        actionBrain.setId(counter.getId());

                        // add action into condition
                        condition.addAction(actionBrain);
                    }

                    // write info data
                    if (condition.getItem_list() != null) {
                        for (SEItem item : condition.getItem_list()) {
                            if (item instanceof SEItemImage) {
                                writeImageInfo(((SEItemImage) item).getSEImage(), dir);
                            }
                        }
                    }

                    // write info data
                    if (condition.getAction_list() != null) {
                        for (SEAction item : condition.getAction_list()) {
                            if (item instanceof SEActionImage) {
                                writeImageInfo(((SEActionImage) item).getSEImage(), dir);
                            }
                        }
                    }
                    seScene.addCondition(condition);
                }


                return new Gson().toJson(script);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    static void writeImageInfo(SEImage image, File dir) throws IOException {
        final File f = new File(dir, image.getCropFileName() + ".info");
        final ImageInfo ii = image.getImageInfo();
        if (!f.exists()) {
            FileWriter fileWriter = new FileWriter(f);
            fileWriter.write("x=" + ii.getX() + "\n");
            fileWriter.write("y=" + ii.getY() + "\n");
            fileWriter.write("width=" + ii.getW() + "\n");
            fileWriter.write("height=" + ii.getH() + "\n");
            fileWriter.write("flag=" + ii.getFlag() + "\n");
            fileWriter.write("thresh=" + ii.getThreshold() + "\n");
            fileWriter.write("maxval=" + ii.getMaxval() + "\n");
            fileWriter.write("type=" + ii.getType() + "\n");
            fileWriter.write("sim=" + (ii.getSim() / 100F));
            fileWriter.close();
        }

    }

    private static byte[] readFile(File f) throws IOException {
        FileInputStream fis = new FileInputStream(f);
        if (f.length() > 100 * 1024 * 1024) {
            throw new IOException();
        }
        byte[] data = new byte[(int) f.length()];
        final int i = fis.read(data, 0, data.length);
        fis.close();
        return data;
    }
}
