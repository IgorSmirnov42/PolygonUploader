package com.codeforces.smirnov

import com.codeforces.smirnov.api.RequestFactory
import com.codeforces.smirnov.api.problem.ListRequest
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.findOrSetObject
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import java.io.File

class Main : CliktCommand() {
    private val key: String = System.getenv("polygon.key")
    private val secret: String = System.getenv("polygon.secret")
    private val problemName: String by argument(name = "Problem name")
    private val pathToProblem: String by argument(name = "Path to problem")
    private val polygonApi: String by option(help = "Polygon api address").default("https://polygon.codeforces.com/api/")
    private val factory: RequestFactory by findOrSetObject {
        RequestFactory(polygonApi, key, secret)
    }
    private val list: ListRequest by findOrSetObject {
        ListRequest(factory)
    }

    override fun run() {
        val id = list.problemId(problemName)
        val importer = ProblemImporter(factory, id, File(pathToProblem))
        importer.import()
    }
}

fun main(args: Array<String>) {
    Main().main(args)
}

