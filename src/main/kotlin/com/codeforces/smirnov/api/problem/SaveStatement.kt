package com.codeforces.smirnov.api.problem

import com.codeforces.smirnov.api.RequestFactory

class SaveStatement(private val factory: RequestFactory, private val problemId: String) {
    fun saveStatement(
        name: String,
        legend: String,
        inputFormat: String,
        outputFormat: String,
        notes: String,
        language: String = "russian"
    ) {
        factory.call(
            "problem.saveStatement",
            "problemId" to problemId,
            "name" to name,
            "legend" to legend,
            "input" to inputFormat,
            "output" to outputFormat,
            "notes" to notes,
            "lang" to language,
            "encoding" to "utf-8"
        )
    }
}