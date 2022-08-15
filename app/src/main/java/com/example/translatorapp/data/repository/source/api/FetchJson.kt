package com.example.translatorapp.data.repository.source.api

import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

private const val CONTENT_TYPE = "content-type"
private const val TYPE = "application/json"
private const val RAPID_KEY = "X-RapidAPI-Key"
private const val KEY = "e55c7d049bmsh762c8cdef8b0892p197f12jsnd48120feb269"
private const val RAPID_HOST = "X-RapidAPI-Host"
private const val HOST = "microsoft-translator-text.p.rapidapi.com"
private const val GET = "GET"
private const val POST = "POST"
private const val ACCEPT_ENCODING = "accept-encoding"
private const val ENCODE = "json"

fun getJson(url: String): String {
    val connection = URL(url).openConnection() as HttpURLConnection
    addHeader(connection)
    connection.apply {
        requestMethod = GET
        doInput = true
        connect()
        if (responseCode == HttpURLConnection.HTTP_OK) {
            return readResponse(this)
        }
        return ""
    }
}

fun readResponse(connection: HttpURLConnection): String {
    val stringBuilder = StringBuilder()
    val inputStream = InputStreamReader(connection.inputStream)
    val br = BufferedReader(inputStream)
    var line: String?
    while (br.readLine().also { line = it } != null) stringBuilder.append("$line")
    inputStream.close()
    br.close()
    return stringBuilder.toString()
}

fun postJson(url: String, body: String): String {
    val connection = URL(url).openConnection() as HttpURLConnection
    addHeader(connection)
    connection.apply {
        setRequestProperty(CONTENT_TYPE, TYPE)
        requestMethod = POST
        doOutput = true
        doInput = true
        connect()
        val writer = OutputStreamWriter(outputStream)
        writer.write(body)
        writer.flush()
        writer.close()
        Log.v("tag111", "$responseCode  $responseMessage")
        if (responseCode == HttpURLConnection.HTTP_OK) {
            return readResponse(this)
        }
        return ""
    }
}

fun addHeader(connection: HttpURLConnection) {
    connection.apply {
        useCaches = false
        setRequestProperty(ACCEPT_ENCODING, ENCODE)
        setRequestProperty(RAPID_KEY, KEY)
        setRequestProperty(RAPID_HOST, HOST)
    }
}
