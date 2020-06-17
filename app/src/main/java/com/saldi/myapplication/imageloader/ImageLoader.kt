package com.saldi.myapplication.imageloader

import SingletonHolder
import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.util.Log
import android.widget.ImageView
import com.saldi.myapplication.imageloader.Utils.CopyStream
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class ImageLoader private constructor(var context: Application) {

    companion object : SingletonHolder<ImageLoader, Application>(::ImageLoader)

    private var mContext: Application = context

    private var executorService: ExecutorService = Executors.newFixedThreadPool(5)

    private var memoryCache = MemoryCache()

    private var fileCache: FileCache = FileCache(mContext)

    //handler to display images in UI thread
    var handler: Handler = Handler()

    fun loadImage(url: String, imageView: ImageView) {

        var bitmap = memoryCache.get(url)

        if (bitmap != null) {
            imageView.setImageBitmap(bitmap)
            Log.v("1111", "Loading from memory cache")
        } else {
            bitmap = getBitmapFromFileCache(url)
            if (bitmap != null) {
                Log.v("1111", "loading from file cache")
                imageView.setImageBitmap(bitmap)
            } else {
                //queue Photo to download from url
                Log.v("1111", "downloading now")
                queuePhoto(url, imageView)
            }
            //TODO pass image in this method only?
            //Before downloading image show default image
            // imageView.setImageResource(stub_id)
        }
    }

    private fun queuePhoto(url: String, imageView: ImageView) {
        executorService.submit(PhotosLoader(url, imageView))
    }

    private fun getBitmapFromFileCache(url: String): Bitmap? {
        val f: File = fileCache.getFile(url)

        return decodeFile(f)

    }


    internal inner class PhotosLoader(var url: String, var imageView: ImageView) : Runnable {
        override fun run() {
            try {
                // download image from web url
                val bmp = getBitmapFromRemote(url)
                // set image data in Memory Cache
                Log.v("1111", "image downloaded")
                memoryCache.put(url, bmp!!)

                // Get bitmap to display
                val bd = BitmapLoader(bmp, imageView)

                handler.post(bd)
            } catch (th: Throwable) {
                th.printStackTrace()
            }
        }

    }


    private fun getBitmapFromRemote(url: String): Bitmap? {
        val file = fileCache.getFile(url)
        return try {
            var bitmap: Bitmap? = null
            val imageUrl = URL(url)
            val conn: HttpURLConnection = imageUrl.openConnection() as HttpURLConnection
            conn.connectTimeout = 30000
            conn.readTimeout = 30000
            conn.instanceFollowRedirects = true
            val inputStream: InputStream = conn.inputStream

            // Constructs a new FileOutputStream that writes to file
            // if file not exist then it will create file
            val os: OutputStream = FileOutputStream(file)

            // See Utils class CopyStream method
            // It will each pixel from input stream and
            // write pixels to output stream (file)
            CopyStream(inputStream, os)
            os.close()
            conn.disconnect()

            //Now file created and going to resize file with defined height
            // Decodes image and scales it to reduce memory consumption
            if (file != null)
                bitmap = decodeFile(file)


            bitmap
        } catch (ex: Throwable) {
            ex.printStackTrace()
            if (ex is OutOfMemoryError) memoryCache.clear()
            null
        }

        // Download image file from web
    }

    //Decodes image and scales it to reduce memory consumption
    private fun decodeFile(f: File): Bitmap? {
        try {

            //Decode image size
            val o = BitmapFactory.Options()
            o.inJustDecodeBounds = true
            val stream1 = FileInputStream(f)
            BitmapFactory.decodeStream(stream1, null, o)
            stream1.close()

            //Find the correct scale value. It should be the power of 2.

            // Set width/height of recreated image
            val REQUIRED_SIZE = 85
            var width_tmp = o.outWidth
            var height_tmp = o.outHeight
            var scale = 1
            while (true) {
                if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE) break
                width_tmp /= 2
                height_tmp /= 2
                scale *= 2
            }

            //decode with current scale values
            val o2 = BitmapFactory.Options()
            o2.inSampleSize = scale
            val stream2 = FileInputStream(f)
            val bitmap = BitmapFactory.decodeStream(stream2, null, o2)
            stream2.close()
            return bitmap
        } catch (e: FileNotFoundException) {
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }


    //Used to display bitmap in the UI thread
    internal inner class BitmapLoader(
        private var bitmap: Bitmap?,
        private var imageView: ImageView
    ) : Runnable {
        override fun run() {
            Log.v("1111", "in bitmap loader now")
            if (bitmap != null) {
                Log.v("1111", "setting now")
                imageView.setImageBitmap(bitmap)
            }
        }
    }

}


