package com.egenesio.utils.domain


/**
 * Created by egenesio on 11/04/2018.
 */

interface APISerializable {
    fun postInit() {}
}

interface APIResult: APISerializable {
    val isValid: Boolean
}

interface NetworkErrorBase: APIResult {
    val code: Int
    val mustLogout: Boolean
    val message: String
}