import java.io.File

fun main() {
	val input = File(ClassLoader.getSystemResource("input9.txt").file).readLines().single()
	part1(input)
	part2(input)
}

private fun part1(input: String) {
	val fileBlocks = input.toFileBlocks()
	val moved = fileBlocks.moveToTheLeftmostFreeSpace()
	println(moved.checksum())
}

private fun part2(input: String) {
	val files = input.toFiles()
	val moved = files.moveToTheLeftmostFreeSpace2()
	println(moved.checksum2())
}

private fun List<Block>.moveToTheLeftmostFreeSpace(): List<Block> {
	val moved = this.toMutableList()

	while (moved.hasGaps()) {
		val lastNonFree = moved.indexOfLast { it !is FreeBlock }
		val end = moved[lastNonFree]
		moved[lastNonFree] = FreeBlock()
		moved[moved.indexOfFirst { it is FreeBlock }] = end
	}

	return moved
}

private fun List<Block>.checksum(): Long {
	var checksum = 0L

	forEachIndexed { index, block ->
		if (block is FreeBlock) {
			return checksum
		}
		checksum += index * (block as DataBlock).id
	}

	return checksum
}

private fun List<Block>.hasGaps(): Boolean =
	slice(indexOfFirst { it is FreeBlock }..indexOfLast { it is FreeBlock }).any { it !is FreeBlock }

private fun List<Block2>.moveToTheLeftmostFreeSpace2(): List<Block2> {
	val moved = this.toMutableList()
	val orderedFiles = filterIsInstance<FileBlock>().sortedByDescending { it.id }

	orderedFiles.forEach { file ->
		var fileIndex = moved.indexOf(file)
		val freeBlockFitting = moved.subList(0, fileIndex).firstOrNull { it is FreeBlock2 && it.span >= file.span  }
		if (freeBlockFitting == null) {
			return@forEach
		}
		val freeBlockIndex = moved.indexOf(freeBlockFitting)

		moved[freeBlockIndex] = file
		if (freeBlockFitting.span > file.span) {
			moved.add(freeBlockIndex + 1, FreeBlock2(freeBlockFitting.span - file.span))
			fileIndex++
		}

		moved[fileIndex] = FreeBlock2(file.span)
	}

	return moved
}

private fun List<Block2>.checksum2(): Long {
	var checksum = 0L
	var currentIndex = 0

	forEach { block ->
		if (block is FileBlock) {
			repeat(block.span) {
				checksum += currentIndex * block.id
				currentIndex++
			}
		} else {
			currentIndex += block.span
		}
	}

	return checksum
}


private fun String.toFileBlocks(): List<Block> =
	buildList {
		var blockId = 0
		this@toFileBlocks.forEachIndexed { index, c ->
			if (index % 2 == 0) {
				addAll(List(c.digitToInt()) { DataBlock(blockId) })
				blockId++
			} else {
				addAll(List(c.digitToInt()) { FreeBlock() })
			}
		}
	}

private fun String.toFiles(): List<Block2> =
	buildList {
		var fileId = 0
		this@toFiles.forEachIndexed { index, c ->
			if (index % 2 == 0) {
				add(FileBlock(fileId, c.digitToInt()))
				fileId++
			} else {
				add(FreeBlock2(c.digitToInt()))
			}
		}
	}

data class DataBlock(val id: Int) : Block

class FreeBlock : Block

interface Block

data class FileBlock(val id: Int, override val span: Int): Block2

data class FreeBlock2(override val span: Int): Block2

interface Block2 {
	val span: Int
}
