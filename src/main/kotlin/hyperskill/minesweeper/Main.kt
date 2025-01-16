package hyperskill.minesweeper

import hyperskill.minesweeper.Cell.*

private const val BOARD_WIDTH = 9

sealed class Cell {

    abstract fun display(): String
    open fun freeMark(): Cell = this
    open fun mineMark(): Cell = MarkedByPlayer(this)

    data class Mine(val isRevealed: Boolean = false) : Cell() {
        override fun display() = if (isRevealed) "X" else "."
        override fun freeMark(): Cell = this.copy(isRevealed = true)
    }

    data class Count(val count: Int, val isRevealed: Boolean = false) : Cell() {
        override fun display() = if (isRevealed) count.toString() else "."
        override fun freeMark(): Cell = this.copy(isRevealed = true)
    }

    object Empty : Cell() {
        override fun display() = "."
        override fun freeMark(): Cell = Visited
    }

    object Visited : Cell() {
        override fun display() = "/"
    }

    data class MarkedByPlayer(val holder: Cell) : Cell() {
        override fun display() = "*"
        override fun mineMark(): Cell = holder
        override fun freeMark(): Cell = holder.freeMark()
    }
}

data class Crd(val r: Int = 0, val c: Int = 0) {

    fun to() = r to c

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

    private fun isValid() = r in 0 until BOARD_WIDTH && c in 0 until BOARD_WIDTH

}

class GameBoard(
    private val mines: Int,
    private val board: List<MutableList<Cell>> = List(BOARD_WIDTH) {
        MutableList(BOARD_WIDTH) { Empty }
    },
) {

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
        val noEmptyCell = boardLinear.count { it is Empty } == 0

        if (noMineLeft) {
            return noEmptyCell
        }

        return false
    }

    operator fun get(x: Int, y: Int) = board[x][y]

    operator fun get(crd: Crd) = board[crd.r][crd.c]

    operator fun set(crd: Crd, cell: Cell) = cell.also { board[crd.r][crd.c] = it }

    private fun handleMineSelection(crd: Crd) {
        this[crd] = this[crd].mineMark()
    }

    private fun handleFreeSelection(crd: Crd){
        if (this[crd] is Mine) {
            error("You stepped on a mine and failed!")
        }

        val queue = ArrayDeque<Crd>()
        queue.add(crd)

        // Flood fill algorithm
        // for automatic cell visit
        while (queue.isNotEmpty()) {
            val crd = queue.removeLast()
            val cell = this[crd]

            if (cell is Empty) {
                queue.addAll(crd.adjacentOffsets())
            } else if (cell is MarkedByPlayer && cell.holder is Empty) {
                queue.addAll(crd.adjacentOffsets())
            }

            this[crd] = cell.freeMark()
        }

    }

    fun markCrd(crd: Crd, action: String) = when (action) {
        "mine" -> handleMineSelection(crd)
        "free" -> handleFreeSelection(crd)
        else -> error("Unknown $action action for cell $crd ")
    }

    fun addMines(): GameBoard {
        val allPositions = (board.indices).flatMap { row ->
            board[row].indices.map { col -> row to col }
        }

        val mineSet = allPositions.shuffled().take(mines).toSet()

        val updatedBoard = board.mapIndexed { row, line ->
            line.mapIndexed { col, cell ->
                if ((row to col) in mineSet) Mine() else cell
            }.toMutableList()
        }

        return GameBoard(mines, updatedBoard).calculateMines()
    }

    fun revealMines(): GameBoard {
        val newBoard = board.mapIndexed { row, line ->
            line.mapIndexed { col, cell ->
                if (cell is Mine) Mine(true) else cell
            }.toMutableList()
        }

        return GameBoard(mines, newBoard)
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
                    adjacentMineCount > 0 -> Count(adjacentMineCount)
                    else -> cell
                }
            }.toMutableList()
        }
        return GameBoard(mines, newBoard)
    }

}

private fun promptForMines() = run {
    var mines: Int?
    do {
        "How many mines do you want on the field? ".let(::println)
        mines = readlnOrNull()?.toIntOrNull()
    } while (mines == null)
    mines
}

private fun promptForAction(): Pair<Pair<Int, Int>, String> {
    "Set/unset mine marks or claim a cell as free: ".let(::println)
    val input = readln().split(" ")
    val result = parseActionInput(input)
    return result ?: promptForAction()
}

private fun parseActionInput(input: List<String>) = when (input.size) {
    3 -> {
        val row = input[0].toIntOrNull()?.minus(1)
        val column = input[1].toIntOrNull()?.minus(1)
        val command = input[2].lowercase()
        if (row != null && column != null && (command == "mine" || command == "free")) Pair(
            Pair(
                column,
                row
            ), command
        )
        else null
    }

    else -> null
}

private fun handleGameOver(exception: Throwable, board: GameBoard) {
    exception.localizedMessage.let(::println)
    board.revealMines().print()
}

private fun play(board: GameBoard) = runCatching {
    while (!board.isResolved()) {
        board.print()
        val (coordinates, cmd) = promptForAction()
        val (x, y) = coordinates
        board.markCrd(Crd(x, y), cmd)
    }
    board.print()
    println("Congratulations! You found all the mines!")
}.getOrElse { handleGameOver(it, board) }

fun main() {
    play(GameBoard(promptForMines()).addMines())
}


