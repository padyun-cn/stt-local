package com.padyun.scripttools.module.runtime;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by daiepngfei on 2020-06-05
 */
public class StManifest {
    private String gameId;
    private String userId;
    private String userToken;
    private String deviceId;
    private String deviceIp;
    private String deviceAsIp;
    private String scriptFilePath;
    private int devicePort;
    private String deviceVerify;
    private Map<String, String> commonHeaders = new HashMap<>();

    public StManifest setGameId(String gameId) {
        this.gameId = gameId;
        return this;
    }

    public StManifest setUseId(String userId) {
        this.userId = userId;
        return this;
    }

    public StManifest setUserToken(String userToken) {
        this.userToken = userToken;
        return this;
    }

    public StManifest setDeviceId(String deviceId) {
        this.deviceId = deviceId;
        return this;
    }

    public StManifest setDeviceIp(String deviceIp) {
        this.deviceIp = deviceIp;
        return this;
    }

    public StManifest setDeviceAsIp(String deviceAsIp) {
        this.deviceAsIp = deviceAsIp;
        return this;
    }

    public StManifest setDevicePort(int devicePort) {
        this.devicePort = devicePort;
        return this;
    }

    public StManifest setDeviceVerify(String deviceVerify) {
        this.deviceVerify = deviceVerify;
        return this;
    }

    public StManifest addCommonHeader(String key, String value) {
        if(key != null && value != null) {
            commonHeaders.put(key, value);
        }
        return this;
    }


    public String getUserId() {
        return userId;
    }

    public String getGameId() {
        return gameId;
    }

    public String getUserToken() {
        return userToken;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getDeviceIp() {
        return deviceIp;
    }

    public String getDeviceAsIp() {
        return deviceAsIp;
    }

    public int getDevicePort() {
        return devicePort;
    }

    public String getDeviceVerify() {
        return deviceVerify;
    }

    public Map<String, String> getCommonHeaders() {
        return new HashMap<>(commonHeaders);
    }

    public Map<String, String> getCommonHeadersWithToken() {
        Map<String, String> map = new HashMap<>(commonHeaders);
        map.put("token", userToken);
        return map;
    }

    public String getScriptFilePath() {
        return scriptFilePath;
    }

    public void setScriptFilePath(String scriptFilePath) {
        this.scriptFilePath = scriptFilePath;
    }
}
