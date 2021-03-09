package com.padyun.scripttools.content.data

import android.graphics.Rect
import androidx.core.util.Consumer
import com.google.gson.*
import com.padyun.scripttools.compat.data.*
import com.padyun.scripttoolscore.compatible.data.model.SEImage
import com.padyun.scripttoolscore.compatible.data.model.brain.SEBrainAlarm
import com.padyun.scripttoolscore.compatible.data.model.coord.SECoord
import com.padyun.scripttoolscore.compatible.data.model.coord.SECoordFixed
import com.padyun.scripttoolscore.compatible.data.model.item.SEItem
import com.padyun.scripttoolscore.compatible.data.model.item.SEItemImage
import com.padyun.scripttoolscore.compatible.data.model.range.SERange
import com.padyun.scripttoolscore.compatible.data.model.range.SERangeSize
import com.uls.utilites.content.CoreWorkers
import com.uls.utilites.un.Useless
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.lang.reflect.Type

/**
 * Created by daiepngfei on 9/20/19
 */
object CustomScriptIOManager {

    @JvmStatic
    fun getCustomScriptFilesWithGameId(userId: String?, gameId: String?): Array<File>? {
        if (!Useless.hasEmptyIn(userId, gameId)) {
            FPathScript.initWithUserAndGameIds(userId, gameId)
            return CustomScriptStorage.getAllScripts()
        }
        return null
    }

    @JvmStatic
    fun getCoScriptFromFileAsync(path: String, resultHandler: Consumer<CoScript>) {
        CustomScriptStorage.openAsync(path, CoreWorkers.IResult {
            val coScript: CoScript? = parseToCoScript(it)
            resultHandler.accept(coScript ?: CoScript.newScript())
        })
    }

    @JvmStatic
    fun getCoScriptFromFileSync(path: File): CoScript? {
        return getCoScriptFromFileSync(path.absolutePath)
    }

    @JvmStatic
    fun getCoScriptFromFileSync(path: String): CoScript? {
        return parseToCoScript(CustomScriptStorage.open(path))
    }

    @JvmStatic
    fun parseToCoScript(it: String?): CoScript? {
        if (it == null || it.isEmpty()) {
            return null
        }
        var coScript: CoScript? = null
        try {
            val coObject = JSONObject(it)
            if (Useless.noEmptyStr(it, coObject.optString("co_version"))) {
                val id = coObject.optString("id")
                coScript = if (Useless.isEmpty(id)) CoScript.newScript() else CoScript(id)
                coScript!!.publishedID = coObject.optString("publishedID")
                coScript.publishingCode = coObject.optLong("publishingCode", 0L)
                val sceneObj = coObject.getJSONObject("scene")
                val coScene = CoScene()
                coScene.setId(sceneObj.optInt("scene_id"))
                coScene.setName(sceneObj.optString("name"))
                foreachJSONArray(sceneObj.getJSONArray("condition_list"), Consumer {
                    val cond = parseCondition(it)
                    if (cond != null) coScene.addCondtion(cond)
                })
                coScript.setScene(coScene)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return coScript
    }

    @JvmStatic
    fun saveWithFilePath(path: String?, script: CoScript?, resultHandler: CoreWorkers.IResult<Boolean>?) {
        if (path != null && script != null) {
            CustomScriptStorage.saveAsync(path, script, resultHandler)
        }
    }

    private fun parseCondition(co: JSONObject): AbsCoConditon? {
        var condition: AbsCoConditon? = null
        val cotype = co.optString("co_type")
        if (Useless.equals("color", cotype)) {
            val color = co.optInt("co_color")
            val rect = rectOf(co, "co_rect")
            val path = co.optString("co_origin_path")
            condition = CoColor(color, path, rect)
        } else {
            val imgObj = co.optJSONObject("v3Image")
            if (imgObj != null) {
                val image = Gson().fromJson(imgObj.toString(), SEImage::class.java)
                when (cotype) {
                    "click" -> condition = CoClick(image)
                    "slide" -> {
                        val coStart = rectOf(co, "co_start")
                        val coEnd = rectOf(co, "co_end")
                        condition = CoSlide(image, coStart, coEnd)
                    }
                    "offset" -> {
                        val offsetX = co.optInt("co_offset_x")
                        val offsetY = co.optInt("co_offset_y")
                        condition = CoOffset(image, offsetX, offsetY)
                    }
                    "tap" -> {
                        val re = rectOf(co, "co_tap")
                        if (re != null) {
                            condition = CoTap(image, re)
                        }
                    }
                    "finish" -> {
                        condition = CoFinish(image)
                    }
                }
            }

            if (condition is AbsCoImage<*>) {

                // for NON-exist
                val itemListJA = co.optJSONArray("item_list")
                if (itemListJA != null && itemListJA.length() == 2) {
                    val builder = GsonBuilder()
                    builder.registerTypeAdapter(SECoord::class.java, object : JsonDeserializer<SECoord> {
                        override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): SECoord {
                            return Gson().fromJson(json, SECoordFixed::class.java)
                        }
                    })
                    builder.registerTypeAdapter(SERange::class.java, object : JsonDeserializer<SERange> {
                        override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): SERange {
                            return Gson().fromJson(json, SERangeSize::class.java)
                        }
                    })
                    val itemImage = builder.create().fromJson(itemListJA[1].toString(), SEItemImage::class.java)
                    if (itemImage != null && itemImage.state == SEItem.STATE_WITHOUT) {
                        condition.nonExistExtraItemImage = itemImage
                    }
                }


                condition.co_range = rectOf(co, "co_range")
                val arr: JSONArray? = co.optJSONArray("item_list")
                if (arr != null && arr.length() > 0) {
                    val item = arr.optJSONObject(0)
                    if (item != null) {
                        condition.setItemState(item.optInt("state"))
                        condition.setItemTimeout(item.optInt("timeout"))
                        condition.setItemRelation(item.optInt("relation"))
                    }
                }
            }

            if(condition != null){
                val brainAlarm = co.optString("co_brainAlarm")
                if(brainAlarm != null){
                    condition.co_brainAlarm = Gson().fromJson(brainAlarm, SEBrainAlarm::class.java)
                }
            }

        }

        /*var cls: Class<* : AbsCoConditon>? =
        when (cotype) {
            "click" ->  CoClick::class.java
            "slide" -> {
                val coStart = rectOf(co, "co_start")
                val coEnd = rectOf(co, "co_end")
                condition = CoSlide(image, coStart, coEnd)
            }
            "offset" -> {
                val offsetX = co.optInt("co_offset_x")
                val offsetY = co.optInt("co_offset_y")
                condition = CoOffset(image, offsetX, offsetY)
            }
            "tap" -> {
                val re = rectOf(co, "co_tap")
                if (re != null) {
                    condition = CoTap(image, re)
                }
            }
            "finish" -> {
                condition = CoFinish(image)
            }
        }
        condition = Gson().fromJson(co.toString(), CoClick::class.java)
        */



        if (condition != null) {
            condition.isDisabled = co.optBoolean("disabled", false)
            condition.isCo_isDefaultTimeout = co.optBoolean("co_isDefaultTimeout", true)
            condition.co_timeout = co.optInt("co_timeout", 1000)
            condition.co_brain_count = co.optInt("co_brain_count", 1)
        }
        return condition
    }

    private fun rectOf(`object`: JSONObject, key: String): Rect? {
        val rectString = `object`.optString(key)
        return if (rectString == null) null else Gson().fromJson(rectString, Rect::class.java)
    }

    private fun foreachJSONArray(arr: JSONArray?, objectConsumer: Consumer<JSONObject>?) {
        if (arr == null) return
        for (i in 0 until arr.length()) {
            val `object` = arr.optJSONObject(i)
            if (`object` != null && objectConsumer != null) {
                objectConsumer.accept(`object`)
            }
        }
    }

}