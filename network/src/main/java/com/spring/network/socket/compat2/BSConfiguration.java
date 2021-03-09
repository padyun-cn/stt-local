package com.spring.network.socket.compat2;

/**
 * Created by daiepngfei on 10/28/19
 */
@SuppressWarnings("FieldCanBeLocal")
public class BSConfiguration {
    private boolean isAutoReconnectting;
    private long connectTimeout = 10000;
    private String ip;
    private int port;
    private boolean asyncSender = true;
    private int soTimeout = 0;
    private boolean isKeepAlive;
    private int retryCount = 3;

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    public static Builder Builder(){
        return new Builder();
    }

    public static class Builder {
        private BSConfiguration configuration = new BSConfiguration();

        public Builder(){}

        public Builder(String ip, int port){
            setIp(ip);
            setPort(port);
        }

        public Builder setIp(String ip){
            configuration.ip = ip;
            return this;
        }

        public Builder setPort(int port){
            configuration.port = port;
            return this;
        }

        public Builder autoConnect(boolean auto){
            configuration.isAutoReconnectting = auto;
            return this;
        }

        public Builder enableSendingAsync(boolean enable){
            configuration.asyncSender = enable;
            return this;
        }

        public Builder connectionTimeout(int timeout){
            configuration.connectTimeout = timeout;
            return this;
        }

        public Builder soTimeout(int timeout){
            configuration.soTimeout = timeout;
            return this;
        }

        public Builder setIsKeepAlive(boolean keepAlive){
            configuration.isKeepAlive = keepAlive;
            return this;
        }


        public Builder setRetryCount(int retryCount) {
            configuration.retryCount = retryCount;
            return this;
        }

        public BSConfiguration create() {
            return configuration;
        }
    }

    public BSConfiguration(){}

    public BSConfiguration(String ip, int port){
        this.ip = ip;
        this.port = port;
    }

    /**
     *
     * @param ip
     * @param port
     * @param isReconnectting
     */
    public BSConfiguration(String ip, int port, boolean isReconnectting) {
        this.ip = ip;
        this.port = port;
        this.isAutoReconnectting = isReconnectting;
    }

    public void setAutoReconnectting(boolean autoReconnectting) {
        isAutoReconnectting = autoReconnectting;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public boolean isAsyncSender() {
        return asyncSender;
    }

    public void setIsKeepAlive(boolean keepAlive) {
        this.isKeepAlive = keepAlive;
    }

    public boolean isKeepAlive() {
        return isKeepAlive;
    }

    public void setAsyncSender(boolean asyncSender) {
        this.asyncSender = asyncSender;
    }

    public void setConnectTimeout(long connectTimeout) {
        this.connectTimeout = Math.max(connectTimeout, 1000);
    }

    public void setSoTimeout(int soTimeout){
        this.soTimeout = soTimeout;
    }

    public boolean isAutoReconnectting() {
        return isAutoReconnectting;
    }

    public int getSoTimeout() {
        return soTimeout;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port){
        this.port = port;
    }

    public long getConnectTimeout() {
        return connectTimeout;
    }
}
