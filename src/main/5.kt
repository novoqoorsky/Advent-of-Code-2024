import java.io.File

fun main() {
	val pageOrderingRules = readPageOrderingRules()
	part1(pageOrderingRules)
	part2(pageOrderingRules)
}

private fun part1(pageOrderingRules: PageOrderingRules) {
	println(pageOrderingRules.correct.sumOf { it.middle() })
}

private fun part2(pageOrderingRules: PageOrderingRules) {
	println(
		pageOrderingRules.incorrect
			.map { it.sortedWith { a, b -> if (pageOrderingRules.rules.contains(Rule(a, b))) -1 else 1 } }
			.sumOf { it.middle() }
	)
}

private fun List<Int>.middle(): Int = get((size - 1) / 2)

private fun readPageOrderingRules(): PageOrderingRules {
	val input = File(ClassLoader.getSystemResource("input5.txt").file).readLines()
	val emptyLineIndex = input.indexOfFirst { it.isBlank() }

	val rules = input.subList(0, emptyLineIndex).map { line ->
		val (before, after) = line.split("|").map { it.toInt() }
		Rule(before, after)
	}
	val updates = input.subList(emptyLineIndex + 1, input.size).map { line ->
		line.split(",").map { it.toInt() }
	}

	return PageOrderingRules(rules, updates)
}

data class PageOrderingRules(
	val rules: List<Rule>,
	val updates: List<List<Int>>,
	val correct: MutableList<List<Int>> = mutableListOf(),
	val incorrect: MutableList<List<Int>> = mutableListOf(),
) {

	init {
		updates.forEach { update ->
			var isCorrect = true
			update.forEachIndexed { i, number ->
				if (update.subList(i + 1, update.size).any { rules.contains(Rule(it, number)) }) {
					isCorrect = false
					return@forEachIndexed
				}
			}
			if (isCorrect) {
				correct.add(update)
			} else {
				incorrect.add(update)
			}
		}
	}
}

data class Rule(
	val before: Int,
	val after: Int,
)
