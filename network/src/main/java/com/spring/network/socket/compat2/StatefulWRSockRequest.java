package com.spring.network.socket.compat2;

/**
 * Created by daiepngfei on 2020-06-16
 */
public abstract class StatefulWRSockRequest implements IWRSockRequest {

    public static class Error {
        private Exception e;
        private int code;
        private String msg;

        public Error(Exception e, int code, String msg) {
            this.e = e;
            this.code = code;
            this.msg = msg;
        }
    }

    public enum State {
        IDLE, WRITING, READING, FINIEDED, ERROR
    }

    private State state = State.IDLE;
    private Error error = null;

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }

    public void setState(State state) {
        synchronized (this) {
            this.state = state;
        }
    }

    public State getCurrentState() {
        synchronized (this) {
            return this.state;
        }
    }

    public boolean isSuccess(){
        return getCurrentState() == State.FINIEDED;
    }

    public boolean isErrored() {
        return getCurrentState() == State.ERROR;
    }
}
