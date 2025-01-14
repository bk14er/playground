package hyperskill.minesweeper

import hyperskill.minesweeper.Cell.Mine
import hyperskill.minesweeper.Cell.Unexplored
import java.util.LinkedList

private const val BOARD_WIDTH = 9

data class CellState(val isVisible: Boolean = false, val isMarked: Boolean = false) {
    fun makeVisible() = CellState(isVisible = true, isMarked = isMarked)

    fun switchMarker() = CellState(isVisible = isVisible, isMarked = !isMarked)
}

sealed class Cell(open val state: CellState) {

    abstract fun value(): String

    fun display(): String = when {
        state.isMarked -> "*"
        state.isVisible -> value()
        else -> "."
    }

    fun freeMark(): Cell = when (this) {
        is Unexplored -> Visited(state = state.makeVisible())
        is Count -> this.copy(state = state.makeVisible())
        else -> this
    }

    fun mineMark(): Cell = when (this) {
        is Visited -> this.copy(state = state.switchMarker())
        is Mine -> this.copy(state = state.switchMarker().makeVisible())
        is Count -> this.copy(state = state.switchMarker())
        else -> this
    }

    data class Mine(override val state: CellState = CellState()) : Cell(state) {
        override fun value(): String = "X"
    }

    data class Count(val counter: Char, override val state: CellState = CellState()) : Cell(state) {
        override fun value(): String = counter.toString()
    }

    data class Visited(override val state: CellState) : Cell(state) {
        override fun value(): String = "/"
    }

    data object Unexplored : Cell(state = CellState()) {
        override fun value(): String = "."
    }

}

data class Crd(val r: Int = 0, val c: Int = 0) {

    fun adjacentOffsets() = listOf(
        Crd(r - 1, c - 1),
        Crd(r, c - 1),
        Crd(r + 1, c - 1),
        Crd(r - 1, c),
        Crd(r + 1, c),
        Crd(r - 1, c + 1),
        Crd(r, c + 1),
        Crd(r + 1, c + 1),
    ).filter { it.isValid() }

    fun allWays() = listOf(
        Crd(r, c - 1),
        Crd(r, c + 1),
        Crd(r - 1, c),
        Crd(r + 1, c)
    ).filter { it.isValid() }


    private fun isValid() = r in 0 until BOARD_WIDTH && c in 0 until BOARD_WIDTH

}

class GameBoard(
    private val board: List<MutableList<Cell>> = List(BOARD_WIDTH) {
        MutableList(BOARD_WIDTH) { Unexplored }
    },
) {

    fun addMines(mineCount: Int): GameBoard {
        val allPositions = (board.indices).flatMap { row ->
            board[row].indices.map { col -> row to col }
        }

        val mineSet = allPositions.shuffled().take(mineCount).toSet()

        val updatedBoard = board.mapIndexed { row, line ->
            line.mapIndexed { col, cell ->
                if ((row to col) in mineSet) Mine() else cell
            }.toMutableList()
        }

        return GameBoard(updatedBoard).calculateMines()
    }

    fun print() {
        val header = (1..BOARD_WIDTH).joinToString("") // Generate column numbers
        val divider = "—".repeat(BOARD_WIDTH)        // Generate the horizontal divider

        println()
        println(" |$header|")
        println("—|$divider|")

        board.forEachIndexed { row, line ->
            val rowContent = line.joinToString("") { cell -> cell.display() }
            println("${row + 1}|$rowContent|")
        }

        println("—|$divider|")
    }

    fun isResolved(): Boolean {
        val boardLinear = board.flatten()

        val noMineLeft = boardLinear.count { it is Mine } == 0
        val unExplodedLeft = boardLinear.count { it is Unexplored } == 0

        if (noMineLeft) {
            return unExplodedLeft
        }

        return false
    }

    operator fun get(x: Int, y: Int) = board[x][y]

    operator fun get(crd: Crd) = board[crd.r][crd.c]

    operator fun set(crd: Crd, cell: Cell) = cell.also { board[crd.r][crd.c] = it }

    fun markCrd(crd: Crd, cmd: String): GameBoard {
        val selectedCell = this[crd]

        if (cmd == "mine") {
            this[crd] = selectedCell.mineMark()
            return this.also { it.print() }
        }

        // free cmd:
        if (selectedCell is Mine) {
            error("You stepped on a mine and failed!")
        }

        val queue = LinkedList<Crd>()
        queue.add(crd)

        while (queue.isNotEmpty()) {
            val crd = queue.poll()
            val cell = this[crd]

            if (cell is Unexplored) {
                queue.addAll(crd.allWays())
            }

            this[crd] = cell.freeMark()
        }

        return this.also { it.print() }
    }

    private fun calculateMines(): GameBoard {
        val newBoard = board.mapIndexed { row, line ->
            line.mapIndexed { col, cell ->
                val position = Crd(row, col)
                val adjacentPositions = position.adjacentOffsets()

                // If it's already a mine, leave it as is
                if (cell is Mine) return@mapIndexed cell

                // Count adjacent mines
                val adjacentMineCount =
                    adjacentPositions.count { adjacent -> board[adjacent.r][adjacent.c] is Mine }

                // Update the cell based on adjacent mines
                when {
                    adjacentMineCount > 0 -> Cell.Count('0' + adjacentMineCount)
                    else -> cell
                }
            }.toMutableList()
        }
        return GameBoard(newBoard)
    }

}

fun main() {
    val mines = generateSequence {
        print("How many mines do you want on the field? ")
        readlnOrNull()?.toIntOrNull()?.takeIf { it > 0 }
            ?: run { println("Please enter a positive integer."); null }
    }.first()

    val board = GameBoard().addMines(mines).also { it.print() }

    do {
        val (x, y, cmd) = generateSequence {
            print("Set/unset mines marks or claim a cell as free")
            readlnOrNull()?.trim()?.split(" ")
        }.first()

        val result = runCatching { board.markCrd(Crd(y.toInt() - 1, x.toInt() - 1), cmd) }

        if (result.isFailure) {
            result.exceptionOrNull()?.message?.also { println(it) }
            return
        }

    } while (!board.isResolved())

    board.print()
    println("Congratulations! You found all the mines!")
}


