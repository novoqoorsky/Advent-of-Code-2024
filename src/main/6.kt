import java.io.File
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

fun main() {
	val map = readMap()
	part1(map, printVisitedCount = true)
	part2(map)
}

private fun part1(map: Map, printVisitedCount: Boolean = false): Boolean {
	val guard = map.positions.single { it.c in setOf('<', '>', '^', 'v') }
	var currentPosition: Position? = guard
	val visited = mutableSetOf<Position>()
	var previousVisited = 0
	var iterations = 0

	while (currentPosition != null) {
		visited.add(currentPosition)
		currentPosition = map.move(currentPosition)

		iterations++

		// naively check if there's any new path every 10000 iterations, if not - there's a loop
		if (iterations % 10000 == 0) {
			previousVisited = visited.size
		}

		if (visited.size == previousVisited) {
			return true
		}
	}

	if (printVisitedCount) {
		println(visited.distinctBy { Pair(it.x, it.y) }.count())
	}
	return false
}

private fun part2(map: Map) {
	val obstacles = AtomicInteger(0)
	val executor = Executors.newCachedThreadPool()

	for (x in 0..<map.xSize) {
		for (y in 0..<map.ySize) {
			if (map.positions.single { it.x == x && it.y == y }.c in setOf('#', '<', '>', '^', 'v')) {
				continue
			}
			executor.submit {
				val newMap = map.copy().apply {
					positions.removeIf { it.x == x && it.y == y }
					positions.add(Position(x, y, '#'))
				}
				val isLoop = part1(newMap)
				if (isLoop) {
					obstacles.getAndIncrement()
				}
			}
		}
	}
	executor.shutdown()
	executor.awaitTermination(60, TimeUnit.SECONDS)

	println(obstacles)
}

private fun readMap(): Map =
	File(ClassLoader.getSystemResource("input6.txt").file).useLines { lines ->
		val positions = lines.mapIndexed { x, line ->
			line.mapIndexed { y, c ->
				Position(x, y, c)
			}
		}.toList()

		val xSize = positions.size
		val ySize = positions.first().size

		Map(positions.flatten().toMutableList(), xSize, ySize)
	}

data class Map(
	val positions: MutableList<Position>,
	val xSize: Int,
	val ySize: Int,
) {

	fun move(fromPosition: Position): Position? {
		return when (fromPosition.c) {
			'^' -> next(fromPosition, fromPosition.x - 1, fromPosition.y, '^', '>')
			'v' -> next(fromPosition, fromPosition.x + 1, fromPosition.y, 'v', '<')
			'<' -> next(fromPosition, fromPosition.x, fromPosition.y - 1, '<', '^')
			'>' -> next(fromPosition, fromPosition.x, fromPosition.y + 1, '>', 'v')
			else -> throw IllegalArgumentException("Invalid direction")
		}
	}

	private fun next(fromPosition: Position, x: Int, y: Int, direction: Char, alternativeDirection: Char): Position? =
		positions.singleOrNull { it.x == x && it.y == y }?.let { next ->
			if (next.c != '#') next.copy(direction) else positions.singleOrNull { it.x == fromPosition.x && it.y == fromPosition.y }?.copy(alternativeDirection)
		}

	fun copy(): Map =
		Map(positions.map { it.copy() }.toMutableList(), xSize, ySize)
}

data class Position(
	val x: Int,
	val y: Int,
	val c: Char,
) {

	fun copy(direction: Char): Position =
		Position(x, y, direction)
}
