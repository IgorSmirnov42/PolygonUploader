package com.codeforces.smirnov.api.problem

import com.codeforces.smirnov.api.RequestFactory

class SaveTest(private val factory: RequestFactory, private val problemId: String) {
    fun saveTest(
        testSet: String,
        testIndex: Int,
        testInput: String,
        checkExisting: Boolean
    ) {
        println("Saving test $testIndex from $testSet")
        factory.call(
            "problem.saveTest",
            "problemId" to problemId,
            "testset" to testSet,
            "testIndex" to testIndex.toString(),
            "testInput" to testInput,
            "checkExisting" to checkExisting.toString()
        )
    }
}