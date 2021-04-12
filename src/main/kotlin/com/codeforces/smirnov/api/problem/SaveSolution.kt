package com.codeforces.smirnov.api.problem

import com.codeforces.smirnov.api.RequestFactory

class SaveSolution(private val factory: RequestFactory, private val problemId: String) {
    fun saveSolution(
        name: String,
        solution: String,
        tag: String,
        checkExisting: Boolean
    ) {
        println("Saving solution $name")
        factory.call(
            "problem.saveSolution",
            "problemId" to problemId,
            "name" to name,
            "file" to solution,
            "checkExisting" to checkExisting.toString(),
            "tag" to tag
        )
    }
}