import java.io.File
import java.math.BigDecimal
import java.math.RoundingMode

fun main() {
	val games = readGames()
	part1(games)
	part2(games)
}

private fun part1(games: List<Game>) {
	println(games.mapNotNull { it.findSolution(100) }.sumOf { it.cost() })
}

private fun part2(games: List<Game>) {
	val newGames = games.map {
		Game(it.a, it.b, Location(10000000000000 + it.prize.x, 10000000000000 + it.prize.y))
	}

	println(
		newGames.sumOf {
			val coefficients = arrayOf(
				doubleArrayOf(it.a.x.toDouble(), it.b.x.toDouble()),
				doubleArrayOf(it.a.y.toDouble(), it.b.y.toDouble())
			)
			val constants = doubleArrayOf(it.prize.x.toDouble(), it.prize.y.toDouble())
			val solution = gaussianElimination(coefficients, constants)

			solution[0].toPositiveWholeNumber()?.let { a ->
				solution[1].toPositiveWholeNumber()?.let { b ->
					a * 3L + b
				} ?: 0
			} ?: 0
		}
	)
}

private fun gaussianElimination(coefficients: Array<DoubleArray>, constants: DoubleArray): DoubleArray {
	val n = coefficients.size

	for (i in 0 until n) {
		val factor = coefficients[i][i]
		for (j in 0 until n) {
			coefficients[i][j] /= factor
		}
		constants[i] /= factor

		for (k in 0 until n) {
			if (k != i) {
				val factor2 = coefficients[k][i]
				for (j in 0 until n) {
					coefficients[k][j] -= factor2 * coefficients[i][j]
				}
				constants[k] -= factor2 * constants[i]
			}
		}
	}

	return constants
}

private fun Double.toPositiveWholeNumber(): Long? {
	if (this < 0) {
		return null
	}

	return BigDecimal(this).setScale(2, RoundingMode.HALF_UP)?.let {
		if (it.remainder(BigDecimal.ONE).movePointRight(2).toInt() == 0) {
			it.toLong()
		} else {
			null
		}
	}
}

private fun readGames(): List<Game> {
	val buttonRegex = Regex("X\\+(\\d+), Y\\+(\\d+)")
	val prizeRegex = Regex("X=(\\d+), Y=(\\d+)")

	return File(ClassLoader.getSystemResource("input13.txt").file).readLines().chunked(4).map { game ->
		Game(
			buttonRegex.find(game[0])!!.groups.let { Location(it[1]!!.value.toLong(), it[2]!!.value.toLong()) },
			buttonRegex.find(game[1])!!.groups.let { Location(it[1]!!.value.toLong(), it[2]!!.value.toLong()) },
			prizeRegex.find(game[2])!!.groups.let { Location(it[1]!!.value.toLong(), it[2]!!.value.toLong()) },
		)
	}
}

data class Game(val a: Location, val b: Location, val prize: Location) {

	fun findSolution(iterations: Int): Solution? {
		var solution: Solution? = null

		for (aPushes in 0..<iterations) {
			for (bPushes in 0..<iterations) {
				val x = aPushes * a.x + bPushes * b.x
				val y = aPushes * a.y + bPushes * b.y
				if (x == prize.x && y == prize.y) {
					Solution(aPushes, bPushes).let {
						if (it.cost() < solution.cost()) {
							solution = it
						}
					}
				}
			}
		}

		return solution
	}
}

data class Location(val x: Long, val y: Long)

data class Solution(val aPushes: Int, val bPushes: Int) {

	fun cost(): Long = aPushes * 3L + bPushes
}

private fun Solution?.cost(): Long = Long.MAX_VALUE
