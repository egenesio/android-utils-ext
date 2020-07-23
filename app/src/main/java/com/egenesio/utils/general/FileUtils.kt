package com.egenesio.utils.general

import android.content.Context
import java.io.File

object FileUtils {

    fun readInternalFile(fileName: String): String {
        Utils.context.openFileInput(fileName).bufferedReader().useLines { lines ->
            return lines.fold("") { some, text ->
                "$some$text"
            }
        }
    }

    fun saveInternalFile(fileName: String, content: String?) {
        if (content == null) return

        Utils.context.openFileOutput(fileName, Context.MODE_PRIVATE).use {
            it.write(content.toByteArray())
        }
    }

    fun deleteInternalFile(fileName: String) {
        Utils.context.deleteFile(fileName)
    }

    fun saveInternalInFolder(folderPath: String, fileName: String, content: String?) {
        if (content == null) return

        val folder = internalFolder(folderPath) ?: return
        File(folder.absolutePath + "/$fileName").outputStream().use {
            it.write(content.toByteArray())
        }
    }

    fun readInternalInFolder(folderPath: String, fileName: String): String {
        val folder = internalFolder(folderPath) ?: return ""
        val file = File(folder.absolutePath + "/$fileName")
        if (!file.exists()) return ""

        file.bufferedReader().useLines { lines ->
            return lines.fold("") { some, text ->
                "$some$text"
            }
        }
    }

    fun deleteInternalInFolder(folderPath: String, fileName: String): Boolean {
        val folder = internalFolder(folderPath) ?: return false
        return File(folder.absolutePath + "/$fileName").delete()
    }


    fun deleteFolder(folderPath: String): Boolean {
        val folder = internalFolder(folderPath) ?: return false
        return deleteRecursive(folder)
    }

    private fun deleteRecursive(fileOrDirectory: File): Boolean {
        if (fileOrDirectory.isDirectory) {
            val children = fileOrDirectory.listFiles() ?: arrayOf()
            for (child in children) {
                deleteRecursive(child)
            }
        }
        return fileOrDirectory.delete()
    }

    private fun internalFolder(path: String): File? {
        var dir: File? = Utils.context.filesDir

        path.split("/").forEach {
            if (dir == null) return null

            val _dir = File(dir!!.absolutePath + "/$it")
            dir = when {
                _dir.isDirectory -> _dir
                _dir.mkdir() -> _dir
                else -> null
            }
        }

        return dir
    }
}
