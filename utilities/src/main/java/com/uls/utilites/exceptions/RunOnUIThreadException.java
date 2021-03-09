package com.uls.utilites.exceptions;

/**
 * Created by daiepngfei on 2020-05-29
 */
public class RunOnUIThreadException extends Exception {
    public RunOnUIThreadException(String s) {
        super(s);
    }

    public RunOnUIThreadException() {
        super();
    }
}
