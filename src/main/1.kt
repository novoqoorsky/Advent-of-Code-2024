import java.io.File
import kotlin.math.abs

fun main() {
	val locations = readLocations()

	part1(locations)
	part2(locations)
}

private fun part1(locations: Locations) {
	val left = locations.leftList.sorted()
	val right = locations.rightList.sorted()

	var differences = 0L

	for (i in left.indices) {
		differences += abs(right[i] - left[i])
	}

	println(differences)
}

private fun part2(locations: Locations) {
	var similarity = 0

	for (i in locations.leftList.indices) {
		similarity += (locations.leftList[i] * locations.rightList.count { it == locations.leftList[i] })
	}

	println(similarity)
}

private fun readLocations(): Locations =
	Locations().apply {
		File(ClassLoader.getSystemResource("input1.txt").file).useLines { lines ->
			lines.forEach {
				val (left, right) = it.split("   ")
				leftList.add(left.toInt())
				rightList.add(right.toInt())
			}
		}
	}

data class Locations(
	val leftList: MutableList<Int> = mutableListOf(),
	val rightList: MutableList<Int> = mutableListOf(),
)
