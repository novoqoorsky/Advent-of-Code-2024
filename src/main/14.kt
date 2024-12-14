import java.io.File

fun main() {
	part1(readRobots())
	part2(readRobots())
}

private fun part1(robots: Robots) {
	repeat(100) {
		robots.robots.forEach {
			it.currentPosition = RobotPosition(
				getNewPosition(it.currentPosition.x, it.velocity.x, robots.xSize),
				getNewPosition(it.currentPosition.y, it.velocity.y, robots.ySize),
			)
		}
	}

	val middleVertically = (robots.ySize - 1) / 2
	val middleHorizontally = (robots.xSize - 1) / 2

	val grouped = robots.robots.groupBy { it.currentPosition }
	val first = grouped.filter { it.key.y < middleVertically && it.key.x < middleHorizontally }.map { it.value }.sumOf { it.count() }
	val second = grouped.filter { it.key.y < middleVertically && it.key.x > middleHorizontally }.map { it.value }.sumOf { it.count() }
	val third = grouped.filter { it.key.y > middleVertically && it.key.x < middleHorizontally }.map { it.value }.sumOf { it.count() }
	val fourth = grouped.filter { it.key.y > middleVertically && it.key.x > middleHorizontally }.map { it.value }.sumOf { it.count() }

	println(first * second * third * fourth)
}

private fun part2(robots: Robots) {
	repeat(10000) { iter ->
		robots.robots.forEach {
			it.currentPosition = RobotPosition(
				getNewPosition(it.currentPosition.x, it.velocity.x, robots.xSize),
				getNewPosition(it.currentPosition.y, it.velocity.y, robots.ySize),
			)
		}
		if (iter == 8049) {
			robots.print()
		}
	}
}

private fun getNewPosition(current: Int, move: Int, bound: Int) =
	if (current + move < 0) {
		bound + (current + move)
	} else if (current + move >= bound) {
		(current + move) - bound
	} else {
		current + move
	}

private fun readRobots(): Robots {
	val regex = Regex("p=(-?\\d*(\\.\\d+)?),(-?\\d*(\\.\\d+)?) v=(-?\\d*(\\.\\d+)?),(-?\\d*(\\.\\d+)?)")

	val robots = File(ClassLoader.getSystemResource("input14.txt").file).readLines().map {
		val match = regex.matchEntire(it)!!
		Robot(
			RobotPosition(
				match.groups[1]!!.value.toInt(),
				match.groups[3]!!.value.toInt(),
			),
			RobotVelocity(
				match.groups[5]!!.value.toInt(),
				match.groups[7]!!.value.toInt(),
			),
		)
	}

	return Robots(robots, 101, 103)
}

data class Robots(
	val robots: List<Robot>,
	val xSize: Int,
	val ySize: Int,
) {

	fun print() {
		for (y in 0..<ySize) {
			for (x in 0..<xSize) {
				if (robots.any { it.currentPosition.x == x && it.currentPosition.y == y }) {
					print("#")
				} else {
					print(".")
				}
			}
			println()
		}
		println()
	}
}

data class Robot(
	var currentPosition: RobotPosition,
	val velocity: RobotVelocity,
)

data class RobotPosition(
	val x: Int,
	val y: Int,
)

data class RobotVelocity(
	val x: Int,
	val y: Int,
)
