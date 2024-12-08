import java.io.File
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong

fun main() {
	val equations = readEquations()
	calculateCalibrationResult(equations, Equation::part1OperatorCombinations)
	calculateCalibrationResult(equations, Equation::part2OperatorCombinations)
}

private fun calculateCalibrationResult(equations: List<Equation>, operatorCombinations: (Equation) -> List<List<Char>>) {
	val sum = AtomicLong(0)
	val executor = Executors.newCachedThreadPool()

	equations.forEach {
		executor.submit {
			if (operatorCombinations(it).any { operators -> it.applyOperations(operators) == it.expectedResult }) {
				sum.getAndAdd(it.expectedResult)
			}
		}
	}

	executor.shutdown()
	executor.awaitTermination(60, TimeUnit.SECONDS)

	println(sum)
}

private fun readEquations(): List<Equation> =
	File(ClassLoader.getSystemResource("input7.txt").file).useLines { line ->
		line.map {
			val (result, numbers) = it.split(": ")
			Equation(result.toLong(), numbers.split(" ").map { number -> number.toLong() })
		}.toList()
	}

data class Equation(
	val expectedResult: Long,
	val numbers: List<Long>,
) {

	fun part1OperatorCombinations(): List<List<Char>> =
		combine(listOf('+', '*'), numbers.size - 1)

	fun part2OperatorCombinations(): List<List<Char>> =
		combine(listOf('+', '*', '|'), numbers.size - 1)

	private fun combine(operators: List<Char>, n: Int): List<List<Char>> =
		if (n == 0) {
			listOf(emptyList())
		} else {
			operators.flatMap { operator ->
				combine(operators, n - 1).map {
					listOf(operator) + it
				}
			}
		}

	fun applyOperations(operators: List<Char>): Long {
		var result = numbers[0]
		for (i in 1 until numbers.size) {
			when (operators[i - 1]) {
				'+' -> result += numbers[i]
				'*' -> result *= numbers[i]
				'|' -> result = "$result${numbers[i]}".toLong()
			}
		}
		return result
	}
}
