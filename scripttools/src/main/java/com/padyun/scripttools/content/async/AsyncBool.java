package com.padyun.scripttools.content.async;

import com.uls.utilites.content.CoreWorkers;

/**
 * Created by daiepngfei on 1/29/19
 */
public class AsyncBool extends CoreWorkers<Boolean> {

    public AsyncBool(Work<Boolean> dealer, IResult<Boolean> result) {
        super(dealer, result);
    }

}
