package me.machao.android.anna.library

import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL


/**
 * Date  2019/2/22
 * @author charliema
 */
class UrlConnectionUploader : Uploader {

    companion object {
        const val DEFAULT_CONNECT_TIMEOUT_MILLIS = 15 * 1000 // 15s
        const val DEFAULT_READ_TIMEOUT_MILLIS = 20 * 1000 // 20s
    }

    override fun upload(urlStr: String, jsonStr: String) {
        val httpURLConnection = openConnection(urlStr)

        httpURLConnection.doOutput = true
        httpURLConnection.doInput = true

        httpURLConnection.requestMethod = "POST"
        httpURLConnection.addRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        httpURLConnection.setRequestProperty("Charset", "UTF-8")

        val jsonByteArr = jsonStr.toByteArray()
        httpURLConnection.setRequestProperty("Content-Length", jsonByteArr.size.toString())

        val os = httpURLConnection.outputStream
        os.write(jsonByteArr)
        os.flush()
        os.close()
        val responseCode = httpURLConnection.responseCode
        if (responseCode == 200) {
            // good for now

        } else {
            httpURLConnection.disconnect()
            throw IOException(responseCode.toString() + " " + httpURLConnection.getResponseMessage())
        }

    }

    private fun openConnection(urlStr: String): HttpURLConnection {
        val httpURLConnect = URL(urlStr).openConnection() as HttpURLConnection
        httpURLConnect.connectTimeout = DEFAULT_CONNECT_TIMEOUT_MILLIS
        httpURLConnect.readTimeout = DEFAULT_READ_TIMEOUT_MILLIS
        return httpURLConnect
    }

}