package com.example.translatorapp.data.repository.source.api

import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

private const val CONTENT_TYPE = "content-type"
private const val TYPE = "application/json"
private const val RAPID_KEY = "X-RapidAPI-Key"
private const val KEY = "e55c7d049bmsh762c8cdef8b0892p197f12jsnd48120feb269"
private const val RAPID_HOST = "X-RapidAPI-Host"
private const val HOST = "microsoft-translator-text.p.rapidapi.com"
private const val GET = "GET"
private const val CHARSET = "utf-8"
private const val ACCEPT_ENCODING = "accept-encoding"
private const val ENCODE = "json"

fun getJson(url: String): String {
    val connection = URL(url).openConnection() as HttpURLConnection
    connection.apply {
        useCaches = false
        setRequestProperty(ACCEPT_ENCODING, ENCODE)
        setRequestProperty(RAPID_KEY, KEY)
        setRequestProperty(RAPID_HOST, HOST)
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
