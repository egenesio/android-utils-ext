package io.upify.utils.networking

import com.google.gson.TypeAdapter
import com.google.gson.reflect.TypeToken
import com.google.gson.Gson
import com.google.gson.TypeAdapterFactory
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.io.IOException
import com.google.gson.JsonSyntaxException

internal class LenientTypeAdapterFactory : TypeAdapterFactory {

    override fun <T> create(gson: Gson, type: TypeToken<T>): TypeAdapter<T> {

        val delegate = gson.getDelegateAdapter(this, type)

        return object : TypeAdapter<T>() {

            @Throws(IOException::class)
            override fun write(out: JsonWriter, value: T) {
                delegate.write(out, value)
            }

            @Throws(IOException::class)
            override fun read(`in`: JsonReader): T? {
                try { //Here is the magic
                    //Try to read value using default TypeAdapter
                    return delegate.read(`in`)
                } catch (e: JsonSyntaxException) {
                    e.printStackTrace()
                    //If we can't in case when we expecting to have an object but array is received (or some other unexpected stuff), we just skip this value in reader and return null
                    `in`.skipValue()
                    return null
                }

            }
        }
    }
}