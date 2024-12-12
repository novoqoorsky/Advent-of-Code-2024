import java.io.File

fun main() {
	val garden = readGarden()
	part1(garden)
	part2(garden)
}

private fun part1(garden: Garden) {
	println(
		garden.regions.sumOf { it.perimeter() * it.size }
	)
}

private fun List<GardenPosition>.perimeter(): Long =
	sumOf { 4L - it.neighboursFormingRegion().size }

private fun part2(garden: Garden) {
	println(
		garden.regions.sumOf { it.numberOfCorners() * it.size }
	)
}

fun List<GardenPosition>.numberOfCorners(): Long =
	sumOf { position ->
		val otherNeighbours = position.otherNeighbours()
		val outsideCorners = when (otherNeighbours.size) {
			// if it's inside a vertical or horizontal line, it doesn't have corners
			2 -> if (otherNeighbours[0].x != otherNeighbours[1].x && otherNeighbours[0].y != otherNeighbours[1].y) 1 else 0
			4 -> 4
			3 -> 2
			else -> 0

		}
		val insideCorners = setOf(Pair(1, 1), Pair(-1, 1), Pair(-1, -1), Pair(1, -1)).sumOf {
			val x1 = position.x + it.first
			val y1 = position.y
			val x2 = position.x
			val y2 = position.y + it.second

			// if the horizontal and vertical neighbours are the same plant and the diagonal neighbour is a different plant, then there's a corner
			if (
				singleOrNull { p -> p.x == x1 && p.y == y1 }?.plant == position.plant &&
				singleOrNull { p -> p.x == x2 && p.y == y2 }?.plant == position.plant &&
				singleOrNull { p -> p.x == position.x + it.first && p.y == position.y + it.second }?.plant != position.plant
			) {
				1L
			} else {
				0L
			}
		}

		outsideCorners + insideCorners
	}

private fun readGarden(): Garden {
	val positions = File(ClassLoader.getSystemResource("input12.txt").file).readLines().flatMapIndexed { x, line ->
		line.mapIndexed { y, plant ->
			GardenPosition(x, y, plant)
		}
	}

	positions.forEach { position ->
		val neighbours = listOf(
			positions.singleOrNull { it.x == position.x - 1 && it.y == position.y } ?: GardenPosition(position.x - 1, position.y, '.'),
			positions.singleOrNull { it.x == position.x + 1 && it.y == position.y } ?: GardenPosition(position.x + 1, position.y, '.'),
			positions.singleOrNull { it.x == position.x && it.y == position.y - 1 } ?: GardenPosition(position.x, position.y - 1, '.'),
			positions.singleOrNull { it.x == position.x && it.y == position.y + 1 } ?: GardenPosition(position.x, position.y + 1, '.'),
		)
		position.neighbours = neighbours
	}

	return Garden(positions)
}

data class Garden(val positions: List<GardenPosition>) {

	val regions: List<List<GardenPosition>>

	init {
		regions = findAllRegions()
	}

	private fun findAllRegions(): List<List<GardenPosition>> {
		val regions = mutableListOf<List<GardenPosition>>()
		val visited = mutableSetOf<GardenPosition>()

		fun dfs(position: GardenPosition, region: MutableList<GardenPosition>) {
			visited.add(position)
			region.add(position)

			for (neighbor in position.neighboursFormingRegion()) {
				if (neighbor !in visited) {
					dfs(neighbor, region)
				}
			}
		}

		for (position in positions) {
			if (position !in visited) {
				val region = mutableListOf<GardenPosition>()
				dfs(position, region)
				regions.add(region)
			}
		}

		return regions
	}
}

data class GardenPosition(val x: Int, val y: Int, val plant: Char) {

	lateinit var neighbours: List<GardenPosition>

	fun neighboursFormingRegion() = neighbours.filter { it.plant == plant }

	fun otherNeighbours() = neighbours.filter { it.plant != plant }
}
