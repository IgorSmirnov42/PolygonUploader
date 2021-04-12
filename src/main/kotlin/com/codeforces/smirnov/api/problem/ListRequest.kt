package com.codeforces.smirnov.api.problem

import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import com.codeforces.smirnov.api.RequestFactory
import java.lang.StringBuilder

@Suppress("UNCHECKED_CAST")
class ListRequest(private val factory: RequestFactory) {
    fun problemId(problemName: String): String {
        val body = factory.call("problems.list", "name" to problemName, "showDeleted" to "false")
        println(body)
        val obj = Parser.default().parse(StringBuilder(body)) as JsonObject
        val problemsArray = obj["result"] as JsonArray<JsonObject>
        require(problemsArray.size == 1)
        return problemsArray[0]["id"].toString()
    }
}