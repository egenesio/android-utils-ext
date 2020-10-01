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
    val requestData: APIRequestData?
    val mustLogout: Boolean
    val message: String
}

data class APIRequestData(
    val requestURL: String,
    val requestMethod: String,
    val requestBody: String = "",
    val response: String = "")