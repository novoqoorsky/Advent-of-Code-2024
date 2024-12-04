import java.io.File

fun main() {
	val letters = readLetters()
	part1(letters)
	part2(letters)
}

private fun part1(letters: List<Letter>) {
	var xmas = 0
	val xLetters = letters.filter { it.c == 'X' }

	xLetters.forEach { x ->
		xmas += letters.findXmasVertically(x)
		xmas += letters.findXmasHorizontally(x)
		xmas += letters.findXmasDiagonally(x)
	}

	println(xmas)
}

private fun part2(letters: List<Letter>) {
	var mas = 0
	val aLetters = letters.filter { it.c == 'A' }

	aLetters.forEach { a ->
		if (
			((letters.find(a.x - 1, a.y - 1)?.c == 'M' && letters.find(a.x + 1, a.y + 1)?.c == 'S') ||
				(letters.find(a.x - 1, a.y - 1)?.c == 'S' && letters.find(a.x + 1, a.y + 1)?.c == 'M')
				) &&
			((letters.find(a.x + 1, a.y - 1)?.c == 'M' && letters.find(a.x - 1, a.y + 1)?.c == 'S') ||
				((letters.find(a.x + 1, a.y - 1)?.c == 'S' && letters.find(a.x - 1, a.y + 1)?.c == 'M'))
				)
		) mas++
	}

	println(mas)
}

fun List<Letter>.findXmasVertically(xLetter: Letter): Int {
	var xmas = 0

	if (find(xLetter.x + 1, xLetter.y)?.c == 'M' && find(xLetter.x + 2, xLetter.y)?.c == 'A' && find(xLetter.x + 3, xLetter.y)?.c == 'S') {
		xmas++
	}

	if (find(xLetter.x - 1, xLetter.y)?.c == 'M' && find(xLetter.x - 2, xLetter.y)?.c == 'A' && find(xLetter.x - 3, xLetter.y)?.c == 'S') {
		xmas++
	}

	return xmas
}

fun List<Letter>.findXmasHorizontally(xLetter: Letter): Int {
	var xmas = 0

	if (find(xLetter.x, xLetter.y + 1)?.c == 'M' && find(xLetter.x, xLetter.y + 2)?.c == 'A' && find(xLetter.x, xLetter.y + 3)?.c == 'S') {
		xmas++
	}

	if (find(xLetter.x, xLetter.y - 1)?.c == 'M' && find(xLetter.x, xLetter.y - 2)?.c == 'A' && find(xLetter.x, xLetter.y - 3)?.c == 'S') {
		xmas++
	}

	return xmas
}

fun List<Letter>.findXmasDiagonally(xLetter: Letter): Int {
	var xmas = 0

	if (find(xLetter.x + 1, xLetter.y + 1)?.c == 'M' && find(xLetter.x + 2, xLetter.y + 2)?.c == 'A' && find(xLetter.x + 3, xLetter.y + 3)?.c == 'S') {
		xmas++
	}

	if (find(xLetter.x - 1, xLetter.y - 1)?.c == 'M' && find(xLetter.x - 2, xLetter.y - 2)?.c == 'A' && find(xLetter.x - 3, xLetter.y - 3)?.c == 'S') {
		xmas++
	}

	if (find(xLetter.x + 1, xLetter.y - 1)?.c == 'M' && find(xLetter.x + 2, xLetter.y - 2)?.c == 'A' && find(xLetter.x + 3, xLetter.y - 3)?.c == 'S') {
		xmas++
	}

	if (find(xLetter.x - 1, xLetter.y + 1)?.c == 'M' && find(xLetter.x - 2, xLetter.y + 2)?.c == 'A' && find(xLetter.x - 3, xLetter.y + 3)?.c == 'S') {
		xmas++
	}

	return xmas
}

private fun readLetters(): List<Letter> =
	File(ClassLoader.getSystemResource("input4.txt").file).readLines()
		.mapIndexed { x, line ->
			line.mapIndexed { y, c -> Letter(x, y, c) }
		}
		.flatten()

fun List<Letter>.find(x: Int, y: Int): Letter? = firstOrNull { it.x == x && it.y == y }

data class Letter(
	val x: Int,
	val y: Int,
	val c: Char,
)
