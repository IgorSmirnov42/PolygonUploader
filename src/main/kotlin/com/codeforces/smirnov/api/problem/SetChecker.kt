package com.codeforces.smirnov.api.problem

import com.codeforces.smirnov.api.RequestFactory

class SetChecker(private val factory: RequestFactory, private val problemId: String) {
    fun setChecker(name: String) {
        println("Setting checker $name")
        factory.call(
            "problem.setChecker",
            "problemId" to problemId,
            "checker" to name
        )
    }
}