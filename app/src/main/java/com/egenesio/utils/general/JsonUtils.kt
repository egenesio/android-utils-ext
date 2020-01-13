package com.egenesio.utils.general

import com.egenesio.utils.domain.APIResult
import com.egenesio.utils.domain.APISerializable
import com.google.gson.*
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.io.IOException

object JsonUtils {

    fun buildGson(): Gson = GsonBuilder()
            .registerTypeAdapterFactory(LenientTypeAdapterFactory())
            .create()

    fun buildJsonParser(): JsonParser = JsonParser()

}

internal class LenientTypeAdapterFactory : TypeAdapterFactory {

    override fun <T> create(gson: Gson, type: TypeToken<T>): TypeAdapter<T> {

        val delegate = gson.getDelegateAdapter(this, type)

        return object : TypeAdapter<T>() {

            @Throws(IOException::class)
            override fun write(out: JsonWriter, value: T) {
                delegate.write(out, value)
            }

            @Throws(IOException::class)
            override fun read(inJsonReader: JsonReader): T? {
                try { //Here is the magic
                    //Try to read value using default TypeAdapter
                    //println(delegate.read(inJsonReader))
                    val obj = delegate.read(inJsonReader)
                    (obj as? APISerializable)?.postInit()
                    return obj
                } catch (e: JsonSyntaxException) {
                    e.printStackTrace()
                    //If we can't in case when we expecting to have an object but array is received (or some other unexpected stuff), we just skip this value in reader and return null
                    inJsonReader.skipValue()
                    return null
                }

            }
        }
    }
}

/*internal class SerializedNameExclusionStrategy: ExclusionStrategy {

    override fun shouldSkipClass(clazz: Class<*>?): Boolean {
        return false
    }

    override fun shouldSkipField(f: FieldAttributes?): Boolean {
        return f?.getAnnotation(SerializedName::class.java) == null
    }

}

class PostProcessingEnabler : TypeAdapterFactory {
    override fun <T> create(gson: Gson, type: TypeToken<T>): TypeAdapter<T> {
        val delegate = gson.getDelegateAdapter(this, type)

        return object : TypeAdapter<T>() {
            @Throws(IOException::class)
            override fun write(out: JsonWriter, value: T) {
                delegate.write(out, value)
            }

            @Throws(IOException::class)
            override fun read(`in`: JsonReader): T {
                val obj = delegate.read(`in`)
                //if (obj is PostProcessable) {
                //    (obj as PostProcessable).gsonPostProcess()
                //}
                return obj
            }
        }
    }
}*/


inline fun <reified T: APIResult> JsonParser.singleWithRaw(string: String?, key: String? = null): Pair<String, T>? {
    try {
        if (string == null) return null

        val element = parse(string)
        val jsonObject: JsonObject = key?.let { element.asJsonObject.get(it).asJsonObject } ?: element.asJsonObject

        val result = Utils.gson.fromJson<T>(jsonObject, T::class.java)
        return if (result.isValid) Pair(jsonObject.toString(), result) else null
    } catch (e: Exception){
        e.printStackTrace()
        return null
    }
}

inline fun <reified T: APIResult> JsonParser.single(string: String?, key: String? = null): T? =
        singleWithRaw<T>(string, key)?.second

inline fun <reified T: APIResult> JsonParser.listWithRaw(string: String?, key: String? = null): Pair<String, List<T>>? {
    try {
        if (string == null) return null

        val json = parse(string)
        val jsonArray: JsonArray = key?.let { json.asJsonObject.get(it).asJsonArray } ?: json.asJsonArray
        val list = Utils.gson.fromJson<List<T>>(jsonArray, object : TypeToken<List<T>>(){}.type)

        return when {
            list.isEmpty() -> Pair(jsonArray.toString(), list)
            list.first().isValid -> Pair(jsonArray.toString(), list)
            else -> null
        }

    } catch (e: Exception){
        e.printStackTrace()
        return null
    }
}

inline fun <reified T: APIResult> JsonParser.list(string: String?, key: String? = null): List<T>? =
        listWithRaw<T>(string, key)?.second