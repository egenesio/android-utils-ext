package com.egenesio.utils.networking

/**
 * Created by egenesio on 11/04/2018.
 */
import android.os.Handler
import android.os.Looper.getMainLooper
import com.google.gson.*
import com.google.gson.reflect.TypeToken
import com.egenesio.utils.domain.APIResult
import com.egenesio.utils.domain.NetworkErrorBase
import com.egenesio.utils.extensions.let
import okhttp3.*
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * Created by egenesio on 5/20/17.
 */

enum class HTTPMethod(val method: String){
    GET("GET"),
    POST("POST"),
    PUT("PUT"),
    DELETE("DELETE")
}

abstract class APIEndpointBase {

    abstract val httpMethod: HTTPMethod
    abstract val isPrivate: Boolean
    abstract val method: String
    abstract val body: Any?
    abstract val responseKey: String?
    abstract val files: Map<String,File>?

    override fun toString(): String {
        return "HTTPMethod: [$httpMethod], isPrivate: [$isPrivate], method: [$method], body: [$body]"
    }
}

abstract class APIClientBase<out E: NetworkErrorBase> {

    companion object {
        val gson: Gson by lazy {
            GsonBuilder()
                .registerTypeAdapterFactory(LenientTypeAdapterFactory())
                //.addDeserializationExclusionStrategy(SerializedNameExclusionStrategy())
                .create()
        }
        val jsonParser: JsonParser by lazy { JsonParser() }
        val MEDIA_TYPE_JSON: MediaType by lazy { MediaType.parse("application/json; charset=utf-8")!! }
    }

    abstract val baseURL: String
    abstract val headerAccessToken: String?
    abstract val accessToken: String?
    abstract val logEnabled: Boolean
    abstract val extraHeaders: List<Pair<String,String>>?

    abstract val errorKey: String?
    abstract fun error(response: String? = null, code: Int? = null): E

    val mainHandler get() = Handler(getMainLooper())

    val httpClient: OkHttpClient = OkHttpClient.Builder()
            .connectTimeout(8, TimeUnit.SECONDS)
            .build()

    inline fun <reified T: APIResult> single(endpoint: APIEndpointBase, crossinline onCompletion: (result: T?, error: E?) -> Unit){

        val block: (result: String?, error: E?) -> Unit = { response, error ->
            jsonParser.single<T>(response, endpoint.responseKey)?.let {
                mainHandler.post { onCompletion(it, error) }
            } ?: run {
                val err = error ?: error(response)
                mainHandler.post { onCompletion(null, err) }
            }
        }

        endpoint.files?.let {
            requestWithFiles(it, endpoint, block)
        } ?: run {
            request(endpoint, block)
        }
    }

    inline fun <reified T: APIResult> list(endpoint: APIEndpointBase, crossinline onCompletion: (result: List<T>?, error: E?) -> Unit) {

        val block: (result: String?, error: E?) -> Unit = { response, error ->
            jsonParser.list<T>(response, endpoint.responseKey)?.let {
                mainHandler.post { onCompletion(it, error) }
            } ?: run {
                val err = error ?: error(response)
                mainHandler.post { onCompletion(null, err) }
            }
        }

        endpoint.files?.let {
            requestWithFiles(it, endpoint, block)
        } ?: run {
            request(endpoint, block)
        }

    }

    inline fun requestWithFiles(
            files: Map<String,File>,
            endpoint: APIEndpointBase,
            crossinline onCompletion: (response: String?, error: E?) -> Unit) {

        if (logEnabled) println("requestWithFile: ${endpoint.method}")
        if (logEnabled) println("body: ${gson.toJson(endpoint.body)}")

        val requestBodyBuilder = MultipartBody.Builder().setType(MultipartBody.FORM)
        val json = jsonParser.parse(gson.toJson(endpoint.body))

        files.entries.forEach {
            requestBodyBuilder.addFormDataPart(it.key, it.value.name, RequestBody.create(MediaType.parse("image/jpeg"), it.value))
        }

        when {
            json.isJsonObject -> {
                json.asJsonObject.entrySet().forEach {
                    requestBodyBuilder.addFormDataPart(it.key, it.value.asString)
                }
            }
            json.isJsonArray -> {
                requestBodyBuilder.addFormDataPart("imagedata", json.asJsonArray.toString()) //TODO FIX : fix field
            }
            else -> {}
        }

        val builder = Request.Builder()
                .url(baseURL + endpoint.method)

        when(endpoint.httpMethod) {
            HTTPMethod.POST -> builder.post(requestBodyBuilder.build())
            HTTPMethod.PUT -> builder.put(requestBodyBuilder.build())
            else -> {}
        }

        if (endpoint.isPrivate && accessToken != null) builder.addHeader(headerAccessToken, "$accessToken")

        extraHeaders?.forEach {
            builder.addHeader(it.first, it.second)
        }

        doCall(builder.build(), onCompletion)
    }

    inline fun request(
            endpoint: APIEndpointBase,
            crossinline onCompletion: (response: String?, error: E?) -> Unit) {

        if (logEnabled) println("request: ${endpoint.method}")
        if (logEnabled) println("body: ${gson.toJson(endpoint.body)}")

        var requestBody: RequestBody? = if(endpoint.body != null) RequestBody.create(MEDIA_TYPE_JSON, gson.toJson(endpoint.body)) else null

        when(endpoint.httpMethod) {
            HTTPMethod.POST, HTTPMethod.PUT -> requestBody = requestBody ?: RequestBody.create(MEDIA_TYPE_JSON, "{}")
            else -> {}
        }

        val builder = Request.Builder()
                .url(baseURL + endpoint.method)
                .method(endpoint.httpMethod.method, requestBody)

        if (endpoint.isPrivate && accessToken != null) builder.addHeader(headerAccessToken, "$accessToken")

        extraHeaders?.forEach {
            builder.addHeader(it.first, it.second)
        }

        doCall(builder.build(), onCompletion)
    }

    inline fun doCall(request: Request, crossinline onCompletion: (response: String?, error: E?) -> Unit) {
        httpClient.newCall(request).enqueue(object: Callback {

            override fun onFailure(call: Call?, e: IOException?) {
                if (logEnabled) println(e)
                onCompletion(null, error())
            }

            override fun onResponse(call: Call?, response: Response?) {
                if (logEnabled) println(response)
                response?.let {

                    val bodyString = it.body()?.string()
                    if (logEnabled) println(bodyString)

                    if (it.isSuccessful) {
                        onCompletion(bodyString, null)
                    } else {
                        onCompletion(null, error(bodyString, it.code()))
                    }
                } ?: onCompletion(null, error())
            }
        })
    }
}

inline fun <reified T: APIResult> JsonParser.single(string: String?, key: String? = null): T? = try {
    string?.let {
        val element = parse(it)
        val jsonObject: JsonObject = key?.let { element.asJsonObject.get(it).asJsonObject } ?: element.asJsonObject

        println("jsonObj: $jsonObject")

        val result = APIClientBase.gson.fromJson<T>(jsonObject, T::class.java)

        println("result: $result")

        if (result.isValid) result else null
    }
} catch (e: Exception){
    e.printStackTrace()
    null
}

inline fun <reified T: APIResult> JsonParser.list(string: String?, key: String? = null): List<T>? = try {
    string?.let {

        val json = parse(it)
        val jsonArray: JsonArray = key?.let { json.asJsonObject.get(it).asJsonArray } ?: json.asJsonArray
        val list = APIClientBase.gson.fromJson<List<T>>(jsonArray, object : TypeToken<List<T>>(){}.type)

        when {
            list.isEmpty() -> list
            list.first().isValid -> list
            else -> null
        }
    }
} catch (e: Exception){
    e.printStackTrace()
    null
}