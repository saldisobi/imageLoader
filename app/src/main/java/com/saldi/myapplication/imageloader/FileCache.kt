package com.saldi.myapplication.imageloader

import android.content.Context
import android.os.Environment
import java.io.File

class FileCache(context: Context) {

    private var cacheDir: File = context.cacheDir

    fun getFile(url: String): File {

        val filename = url.hashCode().toString()

        return File(cacheDir, filename)
    }

    fun clear() {
        // list all files inside cache directory
        val files = cacheDir.listFiles() ?: return
        //delete all cache directory files
        for (f in files) f.delete()
    }

}