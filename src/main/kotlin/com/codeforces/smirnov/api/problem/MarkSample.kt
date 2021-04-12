package com.codeforces.smirnov.api.problem

import com.codeforces.smirnov.api.RequestFactory

class MarkSample(private val factory: RequestFactory, private val problemId: String) {
    fun markSample(
        testSet: String,
        testIndex: Int
    ) {
        println("Marking sample $testIndex from $testSet")
        factory.call(
            "problem.saveTest",
            "problemId" to problemId,
            "testset" to testSet,
            "testIndex" to testIndex.toString(),
            "testUseInStatements" to true.toString()
        )
    }
}