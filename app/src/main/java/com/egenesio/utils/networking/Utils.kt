package com.egenesio.utils.networking

import com.google.gson.*
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import com.egenesio.utils.domain.APISerializable
import java.io.IOException

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

internal class SerializedNameExclusionStrategy: ExclusionStrategy {

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
}