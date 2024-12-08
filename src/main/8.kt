import java.io.File

fun main() {
	val antennaMap = readAntennaMap()
	part1(antennaMap)
	part2(antennaMap)
}

private fun part1(antennaMap: AntennaMap) {
	val antennasFrequency = antennaMap.antennas.groupBy { it.c }
	val antiNodes = antennasFrequency.flatMap { it.value.findPart1AntiNodes() }.toSet()

	println(
		antiNodes.filter {
			it.x >= 0 && it.x < antennaMap.xSize && it.y >= 0 && it.y < antennaMap.ySize
		}.size
	)
}

private fun part2(antennaMap: AntennaMap) {
	val antennasFrequency = antennaMap.antennas.groupBy { it.c }
	val antiNodes = antennasFrequency.flatMap { it.value.findPart2AntiNodes(antennaMap.xSize, antennaMap.ySize) }.toSet()

	println(antiNodes.size)
}

private fun List<Antenna>.findPart1AntiNodes(): Set<AntiNode> =
	flatMap {
		getOtherThan(it).map { other ->
			val horizontalDistance = it.x - other.x
			val verticalDistance = it.y - other.y

			AntiNode(it.x + horizontalDistance, it.y + verticalDistance)
		}
	}.toSet()

private fun List<Antenna>.findPart2AntiNodes(xBound: Int, yBound: Int): Set<AntiNode> {
	val antiNodes = mutableSetOf<AntiNode>()

	forEach {
		getOtherThan(it).forEach { other ->
			val horizontalDistance = it.x - other.x
			val verticalDistance = it.y - other.y

			var currentX = it.x
			var currentY = it.y

			while (currentX in 0..<xBound && currentY in 0..<yBound) {
				antiNodes.add(AntiNode(currentX, currentY))

				currentX += horizontalDistance
				currentY += verticalDistance
			}

			currentX = other.x
			currentY = other.y

			while (currentX in 0..<xBound && currentY in 0..<yBound) {
				antiNodes.add(AntiNode(currentX, currentY))

				currentX -= horizontalDistance
				currentY -= verticalDistance
			}
		}
	}

	return antiNodes
}

private fun List<Antenna>.getOtherThan(antenna: Antenna) =
	filter { it != antenna }

private fun readAntennaMap(): AntennaMap {
	val lines = File(ClassLoader.getSystemResource("input8.txt").file).readLines()

	val antennas = lines.mapIndexed { x, line ->
		line.mapIndexedNotNull { y, c ->
			if (c != '.') {
				Antenna(x, y, c)
			} else {
				null
			}
		}
	}.toList()

	val xSize = lines.size
	val ySize = lines.first().length

	return AntennaMap(antennas.flatten().toMutableList(), xSize, ySize)
}

data class AntennaMap(
	val antennas: List<Antenna>,
	val xSize: Int,
	val ySize: Int,
)

data class Antenna(
	val x: Int,
	val y: Int,
	val c: Char,
)

data class AntiNode(
	val x: Int,
	val y: Int,
)
