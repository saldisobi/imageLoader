package com.saldi.myapplication.imageloader

import java.io.InputStream
import java.io.OutputStream

object Utils {
    fun CopyStream(
        inputStream: InputStream,
        outputStream: OutputStream
    ) {
        val bufferSize = 1024
        try {
            val bytes = ByteArray(bufferSize)
            while (true) {

                //Read byte from input stream
                val count = inputStream.read(bytes, 0, bufferSize)
                if (count == -1) break

                //Write byte from output stream
                outputStream.write(bytes, 0, count)
            }
        } catch (ex: Exception) {
        }
    }
}