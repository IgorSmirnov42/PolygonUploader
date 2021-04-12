package com.codeforces.smirnov.api.problem

import com.codeforces.smirnov.api.RequestFactory

class SetValidator(private val factory: RequestFactory, private val problemId: String) {
    fun setValidator(name: String) {
        println("Setting validator $name")
        factory.call(
            "problem.setValidator",
            "problemId" to problemId,
            "validator" to name
        )
    }
}