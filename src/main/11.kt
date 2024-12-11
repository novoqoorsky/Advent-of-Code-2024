import java.io.File
import java.math.BigDecimal

fun main() {
	val arrangement = readArrangement()
	iterate(arrangement, 25)
	iterate(arrangement, 75)
}

private fun iterate(arrangement: List<BigDecimal>, times: Int) {
	var current = arrangement.associateWith { 1L }

	repeat(times) {
		val newCurrent = mutableMapOf<BigDecimal, Long>()

		current.forEach { (stone, count) ->
			stone.blink().forEach { newStone ->
				newCurrent[newStone] = newCurrent.getOrDefault(newStone, 0) + count
			}
		}

		current = newCurrent
	}

	println(current.values.sum())
}

private fun BigDecimal.blink(): List<BigDecimal> =
	with(this.toPlainString()) {
		if (this == "0") {
			listOf(BigDecimal("1"))
		} else if (length % 2 == 0) {
			listOf(
				BigDecimal(substring(0, length / 2)),
				BigDecimal(substring(length / 2)),
			)
		} else {
			listOf(BigDecimal(this) * BigDecimal("2024"))
		}
	}

private fun readArrangement(): List<BigDecimal> =
	File(ClassLoader.getSystemResource("input11.txt").file).readLines()
		.flatMap { it.split(" ") }
		.map { it.toBigDecimal() }
