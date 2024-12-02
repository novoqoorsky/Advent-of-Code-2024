import java.io.File

fun main() {
	val reports = readReports()

	part1(reports)
	part2(reports)
}

private fun part1(reports: List<Report>) {
	val safeCounter = reports.count { it.isSafe() }

	println(safeCounter)
}

private fun part2(reports: List<Report>) {
	var safeCounter = 0

	for (report in reports) {
		var safe = false

		if (report.isSafe()) {
			safe = true
		} else {
			for (i in 0..<report.levels.size) {
				if (report.dampered(i).isSafe()) {
					safe = true
					break
				}
			}
		}

		if (safe) safeCounter++
	}

	println(safeCounter)
}

private fun readReports(): List<Report> =
	File(ClassLoader.getSystemResource("input2.txt").file).readLines().map {
		Report(it.split(" ").map { level -> level.toInt() })
	}

data class Report(
	val levels: List<Int>,
) {

	fun isSafe(): Boolean {
		var safe = true
		val isIncreasing = levels[1] - levels[0] > 0
		for (i in 0..levels.size - 2) {
			val difference = levels[i + 1] - levels[i]
			if (isIncreasing) {
				if (difference < 1 || difference > 3) {
					safe = false
					break
				}
			} else {
				if (difference > -1 || difference < -3) {
					safe = false
					break
				}
			}
		}
		return safe
	}

	fun dampered(i: Int): Report =
		Report(levels.toMutableList().apply { this.removeAt(i) })
}
