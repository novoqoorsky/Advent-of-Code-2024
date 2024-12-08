import java.io.File

fun main() {
	val input = File(ClassLoader.getSystemResource("input3.txt").file).readLines()
		.joinToString()

	val multiplications = readMultiplications(input)

	part1(multiplications)
	part2(multiplications, input)
}

private fun part1(multiplications: List<Multiplication>) {
	println(multiplications.sumOf { it.x * it.y })
}

private fun part2(multiplications: List<Multiplication>, input: String) {
	val dontRanges = Regex("don't").findAll(input).map { it.range }
	val doRanges = Regex("do").findAll(input)
		.filter { it.range.first !in dontRanges.map { dont -> dont.first } }
		.map { it.range }

	var doMultiply = true
	var sum = 0
	var currentPosition: Int
	var previousPosition = 0

	for (i in multiplications.indices) {
		currentPosition = multiplications[i].range.first

		val dontInstruction = dontRanges.lastOrNull { it.first in (previousPosition + 1)..<currentPosition }
		val doInstruction = doRanges.lastOrNull { it.first in (previousPosition + 1)..<currentPosition }

		if (dontInstruction.first() > doInstruction.first()) {
			doMultiply = false
		} else if (doInstruction.first() > dontInstruction.first()) {
			doMultiply = true
		}

		if (doMultiply) {
			sum += multiplications[i].x * multiplications[i].y
		}

		previousPosition = currentPosition
	}

	println(sum)
}

private fun IntRange?.first() = this?.first ?: -1

private fun readMultiplications(input: String): List<Multiplication> {
	val regex = Regex("(mul\\(([0-9]{1,3}),([0-9]{1,3})\\))")

	return regex.findAll(input)
		.map { Multiplication(it.groupValues[2].toInt(), it.groupValues[3].toInt(), it.range) }
		.toList()
}

data class Multiplication(
	val x: Int,
	val y: Int,
	val range: IntRange,
)
