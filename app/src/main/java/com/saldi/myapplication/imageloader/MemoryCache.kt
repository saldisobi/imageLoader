package com.saldi.myapplication.imageloader

import android.graphics.Bitmap
import android.util.Log
import java.util.*

class MemoryCache {

    init {
        //we will use 25% od available heap size
        setLimit(Runtime.getRuntime().maxMemory() / 4)
    }

    //https://makeinjava.com/access-order-linkedhashmap-using-java-example/ accessOrder true means we will have it LRU, else it will maintain insertion order, now we have access order it is LRU
    private val cache = Collections.synchronizedMap(LinkedHashMap<String, Bitmap>(10, 1f, true))

    //current allocated size
    private var usedSize: Long = 0

    private var limit: Long = 0

    private fun setLimit(newLimit: Long) {
        limit = newLimit
    }

    fun get(id: String): Bitmap? {
        return try {
            if (!cache.containsKey(id)) null else cache[id]
        } catch (ex: NullPointerException) {
            ex.printStackTrace()
            null
        }
    }

    fun put(id: String, bitmap: Bitmap) {
        try {
            if (cache.containsKey(id)) usedSize -= getSizeInBytes(cache[id])
            cache[id] = bitmap
            usedSize += getSizeInBytes(bitmap)
            checkSize()
        } catch (th: Throwable) {
            th.printStackTrace()
        }
    }

    private fun checkSize() {
        if (usedSize > limit) {
            //least recently accessed item will be the first one iterated
            val iter: MutableIterator<Map.Entry<String, Bitmap>> =
                cache.entries.iterator()
            while (iter.hasNext()) {
                val entry = iter.next()
                usedSize -= getSizeInBytes(entry.value)
                iter.remove()
                if (usedSize <= limit) break
            }
        }
    }

    fun clear() {
        try {
            cache.clear()
            usedSize = 0
        } catch (ex: NullPointerException) {
            ex.printStackTrace()
        }
    }

    private fun getSizeInBytes(bitmap: Bitmap?): Long {
        return if (bitmap == null) 0 else (bitmap.rowBytes * bitmap.height).toLong()
    }
}