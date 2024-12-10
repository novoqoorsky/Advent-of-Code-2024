import java.io.File
import java.util.LinkedList

fun main() {
	val topographicMap = readTopographicMap()
	part1(topographicMap)
	part2(topographicMap)
}

private fun part1(topographicMap: TopographicMap) {
	var score = 0

	topographicMap.startingPositions().forEach { start ->
		topographicMap.endingPositions().forEach { end ->
			if (trailExists(start, end)) {
				score += 1
			}
		}
	}

	println(score)
}

private fun part2(topographicMap: TopographicMap) {
	var score = 0

	topographicMap.startingPositions().forEach { start ->
		topographicMap.endingPositions().forEach { end ->
			score += findAllTrails(start, end).size
		}
	}

	println(score)
}

private fun trailExists(start: TopographicMapPosition, end: TopographicMapPosition): Boolean {
	val queue = LinkedList<TopographicMapPosition>()
	val visited = mutableSetOf<TopographicMapPosition>()

	queue.add(start)
	visited.add(start)

	while (queue.isNotEmpty()) {
		val current = queue.poll()

		if (current == end) {
			return true
		}

		for (neighbor in current.reachableNeighbours()) {
			if (neighbor !in visited) {
				queue.add(neighbor)
				visited.add(neighbor)
			}
		}
	}

	return false
}

private fun findAllTrails(start: TopographicMapPosition, end: TopographicMapPosition): List<List<TopographicMapPosition>> {
	val allTrails = mutableListOf<List<TopographicMapPosition>>()
	val currentTrail = mutableListOf<TopographicMapPosition>()
	val visited = mutableSetOf<TopographicMapPosition>()

	fun dfs(current: TopographicMapPosition) {
		if (current == end) {
			allTrails.add(currentTrail.toList())
			return
		}

		visited.add(current)
		currentTrail.add(current)

		for (neighbor in current.reachableNeighbours()) {
			if (neighbor !in visited) {
				dfs(neighbor)
			}
		}

		visited.remove(current)
		currentTrail.removeAt(currentTrail.size - 1)
	}

	dfs(start)
	return allTrails
}

private fun readTopographicMap(): TopographicMap {
	val positions = File(ClassLoader.getSystemResource("input10.txt").file).readLines().flatMapIndexed { x, line ->
		line.mapIndexed { y, height ->
			TopographicMapPosition(x, y, height.digitToInt())
		}
	}

	positions.forEach { position ->
		val neighbours = listOfNotNull(
			positions.singleOrNull { it.x == position.x - 1 && it.y == position.y },
			positions.singleOrNull { it.x == position.x + 1 && it.y == position.y },
			positions.singleOrNull { it.x == position.x && it.y == position.y - 1 },
			positions.singleOrNull { it.x == position.x && it.y == position.y + 1 },
		)
		position.neighbours = neighbours
	}

	return TopographicMap(positions)
}

data class TopographicMap(
	val positions: List<TopographicMapPosition>,
) {

	fun startingPositions() = positions.filter { it.height == 0 }

	fun endingPositions() = positions.filter { it.height == 9 }
}

data class TopographicMapPosition(
	val x: Int,
	val y: Int,
	val height: Int,
) {

	lateinit var neighbours: List<TopographicMapPosition>

	fun reachableNeighbours() = neighbours.filter { it.height == height + 1 }
}
