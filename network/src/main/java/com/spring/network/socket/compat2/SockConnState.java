package com.spring.network.socket.compat2;

/**
 * Lifecycle => {
 * UNINIT -> OPENING_SOCKET -> CONNECTTING -> CONNECTTED -> START_LOOPING(looping)
 *
 * ERROR/DISCONNECTED/TIMEOUT_CONN/TIMEOUT_READ -> {
 * CLOSING_SOCKET -> CLOSED
 * if
 * RECONNECTING - OPENING_SOCKET
 * else
 * -- end --
 * }
 * }
 * Created by daiepngfei on 10/28/19
 */
public enum SockConnState {
    UNINIT_ERROR,
    UNINIT,
    CONNECTTING,
    START_LOOPING,
    START_READING,
    ON_READING_LOOP,
    STOP_READING,
    DISCONNECTED,
    RECONNECTING,
    CONNECTTED,
    READY_TO_WORK,
    COMPLETE,
    TIMEOUT_CONN,
    TIMEOUT_READ,
    ERROR,
    FORCE_CLOSING,
    CLOSING_SOCKET,
    CLOSED,
    TERMINATED
}
