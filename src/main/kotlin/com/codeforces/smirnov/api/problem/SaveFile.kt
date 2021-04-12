package com.codeforces.smirnov.api.problem

import com.codeforces.smirnov.api.RequestFactory

class SaveFile(private val factory: RequestFactory, private val problemId: String) {
    fun saveFile(
        name: String,
        file: String,
        type: String = "source",
        checkExisting: Boolean = true
    ) {
        println("Saving file $name")
        factory.call(
            "problem.saveFile",
            "problemId" to problemId,
            "checkExisting" to checkExisting.toString(),
            "name" to name,
            "file" to file,
            "type" to type
        )
    }
}