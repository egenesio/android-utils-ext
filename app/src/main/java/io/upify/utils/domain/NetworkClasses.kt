package io.upify.utils.domain


/**
 * Created by egenesio on 11/04/2018.
 */
interface APIResult {
    val isValid: Boolean
}

interface NetworkErrorBase: APIResult {
    val code: Int
    val mustLogout: Boolean
    val message: String
}