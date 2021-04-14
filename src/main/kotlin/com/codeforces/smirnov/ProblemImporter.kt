package com.codeforces.smirnov

import com.codeforces.smirnov.api.RequestFactory
import com.codeforces.smirnov.api.problem.*
import java.io.File
import java.io.IOException

class ProblemImporter(factory: RequestFactory, id: String, private val rootFolder: File) {
    private val statementSaver = SaveStatement(factory, id)
    private val testSaver = SaveTest(factory, id)
    private val solutionSaver = SaveSolution(factory, id)
    private val fileSaver = SaveFile(factory, id)
    private val validatorSetter = SetValidator(factory, id)
    private val checkerSetter = SetChecker(factory, id)
    private val sampleMarker = MarkSample(factory, id)

    fun import() {
        for (i in 1..10) {
            try {
                val layout = generateProblemLayout()

                uploadTests(layout)
                uploadSolutions(layout)
                uploadStatement(layout)
                uploadChecker(layout)
                uploadValidator(layout)
                markSampleTests(layout)
                break
            } catch (e: IOException) {
                e.printStackTrace()
                continue
            }
        }

        // TODO: tl, ml, comment, solution tags
    }

    private fun markSampleTests(layout: ProblemLayout) {
        val regex = Regex("exmp[{]")
        val text = layout.statement?.readText() ?: return
        val samplesNumber = regex.findAll(text).count()
        for (i in 1..samplesNumber) {
            sampleMarker.markSample("tests", i)
        }
    }

    private fun uploadChecker(layout: ProblemLayout) {
        val checker = layout.checker ?: return
        fileSaver.saveFile(checker.name, checker.readText())
        checkerSetter.setChecker(checker.name)
    }

    private fun uploadValidator(layout: ProblemLayout) {
        val validator = layout.validator ?: return
        fileSaver.saveFile(validator.name, validator.readText())
        validatorSetter.setValidator(validator.name)
    }

    private fun uploadSolutions(layout: ProblemLayout) {
        if (layout.solutions.isEmpty()) {
            return
        }
        val solutionsWithTags = layout.solutions.sortedBy { it.name.length }.mapIndexed { index, file ->
            file to if (index == 0) "MA" else "OK"
        }
        for ((solution, tag) in solutionsWithTags) {
            solutionSaver.saveSolution(solution.name, solution.readText(), tag, true)
        }
    }

    private fun uploadTests(layout: ProblemLayout) {
        for ((testSet, tests) in layout.tests) {
            for (test in tests.sortedBy { it.name }) {
                testSaver.saveTest(testSet, test.name.toInt(), test.readText(), true)
            }
        }
    }

    private fun uploadStatement(layout: ProblemLayout) {

        fun problemName(nameLine: String): String {
            return nameLine.split("{", "}")[3]
        }

        val statement = layout.statement?.readLines() ?: return
        val inputFileIndex = statement.withIndex().single { it.value.startsWith("\\InputFile") }.index
        val outputFileIndex = statement.withIndex().single { it.value.startsWith("\\OutputFile") }.index
        val exampleIndex = statement.withIndex().first { it.value.startsWith("\\Example") || it.value.startsWith("\\begin{example}") }.index
        val noteIndex = statement.withIndex().singleOrNull { it.value.startsWith("\\Note") }?.index
        val endProblemIndex = statement.withIndex().single { it.value.startsWith("\\end{problem}") }.index
        var beginProblemIndex = statement.withIndex().single { it.value.startsWith("\\begin{problem}") }.index

        val name = problemName(statement[beginProblemIndex])

        while (statement[beginProblemIndex + 1].startsWith("{")) {
            ++beginProblemIndex
        }

        val legend = statement.subList(beginProblemIndex + 1, inputFileIndex).joinToString(separator = "\n")
        val input = statement.subList(inputFileIndex + 1, outputFileIndex).joinToString(separator = "\n")
        val output = statement.subList(outputFileIndex + 1, exampleIndex).joinToString(separator = "\n")
        val note = if (noteIndex != null) {
            statement.subList(noteIndex + 1, endProblemIndex).joinToString(separator = "\n")
        } else {
            ""
        }

        statementSaver.saveStatement(name, legend, input, output, note)
    }

    private fun generateProblemLayout(): ProblemLayout {
        require(rootFolder.exists())
        require(rootFolder.isDirectory)

        return ProblemLayout(
            statement = listOf("statement", "statements", "statements/russian").mapNotNull { getStatement(it) }.firstOrNull(),
            checker = getChecker(),
            validator = getValidator(),
            solutions = getSolutions(),
            tests = getTests()
        )
    }

    private fun getStatement(statementDir: String): File? {
        val statementsDir = File(rootFolder, statementDir)
        if (!statementsDir.exists() || !statementsDir.isDirectory) return null
        return statementsDir
            .listFiles { file -> file.name.endsWith(".tex") }!!
            .minByOrNull { it.length() }
    }

    private fun getChecker(): File? {
        val checkerFile = File(rootFolder, "check.cpp")
        if (!checkerFile.exists() || !checkerFile.isFile) return null
        return checkerFile
    }

    private fun getValidator(): File? {
        val srcDir = File(rootFolder, "src")
        val validatorFile = File(srcDir, "validate.cpp")
        if (!validatorFile.exists() || !validatorFile.isFile) return null
        return validatorFile
    }

    private fun getSolutions(): List<File> {
        val solutionsDir = File(rootFolder, "solutions")
        if (!solutionsDir.exists() || !solutionsDir.isDirectory) return emptyList()
        return solutionsDir
            .listFiles { file -> file.name.endsWith(".cpp") || file.name.endsWith(".py") }!!
            .toList()
    }

    private fun getTests(): List<Pair<String, List<File>>> {
        val testsDir = File(rootFolder, "tests")
        if (!testsDir.exists() || !testsDir.isDirectory) return emptyList()
        val result = testsDir.listFiles()!!
            .map { testSet ->
                testSet.name to if (!testSet.exists() || !testSet.isDirectory) {
                    emptyList()
                } else {
                    testSet
                        .listFiles { file -> file.name.toIntOrNull() != null }!!
                        .toList()
                }
            }
            .filter { it.second.isNotEmpty() }
        return if (result.isEmpty()) {
            listOf(
                "tests" to testsDir
                    .listFiles { file -> file.name.toIntOrNull() != null }!!
                    .toList()
            )
        } else {
            result
        }
    }

    private data class ProblemLayout(
        val statement: File?,
        val checker: File?,
        val validator: File?,
        val solutions: List<File>,
        val tests: List<Pair<String, List<File>>>
    )
}