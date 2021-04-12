package com.codeforces.smirnov.api

import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import okhttp3.*
import org.apache.commons.codec.digest.DigestUtils
import org.json.HTTP
import java.lang.StringBuilder
import java.net.URLEncoder

data class RequestFactory(private val polygonUrl: String, private val key: String, private val secret: String) {

    private val client = OkHttpClient()

    fun call(method: String, vararg params: Pair<String, String>): String {
        val allParams = params.toList() + listOf("apiKey" to key, "time" to currentTime())
        val paramsStr = allParams
            .sortedBy { it.first }
            .joinToString(separator = "&") { "${it.first}=${it.second}" }
        val methodWithParams = "$method?$paramsStr"
        val rand = genRand()
        val apiSig = "$rand${hash(rand, methodWithParams)}"
        val request = Request.Builder()
            .url("$polygonUrl$method")
            .post(
                MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .apply {
                        for ((paramKey, paramValue) in allParams) {
                            addFormDataPart(paramKey, paramValue)
                        }
                    }
                    .addFormDataPart("apiSig", apiSig)
                    .build()
            )
            .build()
        val response = client.newCall(request).execute()
        val result = response.body!!.string()
        val obj = Parser.default().parse(StringBuilder(result)) as JsonObject
        if (obj["status"].toString() != "OK") {
            println(request.url)
            println(result)
        }
        return result
    }

    private fun genRand(): String {
        var str = ""
        for (i in 1..6) {
            str += kotlin.random.Random.nextInt(0, 9)
        }
        return str
    }

    private fun hash(rand: String, url: String): String {
        val newUrl = "$rand/$url#$secret"
        return DigestUtils.sha512Hex(newUrl)
    }

    private fun currentTime(): String = (System.currentTimeMillis() / 1000).toString()
}