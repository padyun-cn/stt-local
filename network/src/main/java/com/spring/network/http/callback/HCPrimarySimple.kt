package com.padyun.spring.beta.network.http

import com.spring.network.http.callback.HCPrimary

/**
 * Created by daiepngfei on 5/3/18
 */
open class HCPrimarySimple<T> : HCPrimary<T> {
    constructor(cls: Class<T>) : super(cls)
    constructor(cls: Class<T>, invalidTokenVerify: Boolean) : super(cls, invalidTokenVerify)
    override fun onFailure(e: Exception?, code: Int, msg: String?) {}
}