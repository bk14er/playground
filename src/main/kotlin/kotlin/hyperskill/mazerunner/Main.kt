package com.bk14er.kotlin.hyperskill.mazerunner

import java.io.File

fun main() = MazeManager().start()

class Maze(private var size: Int = 0, image: String = "") {
    enum class Cell(private val view: Char) {
        WALL('█'), PASS(' '), SOLVE('/');
        override fun toString() = "$view$view"
    }
    data class Crd(val r: Int = 1, val c: Int = 1) {
        fun step(to: Crd) = if (r == to.r) Crd(r, (c + to.c) / 2) else Crd((r + to.r) / 2, to.c)
        fun getNearList() = listOf(Crd(r, c + 2), Crd(r, c - 2), Crd(r + 2, c), Crd(r - 2, c))
        fun getWays() = listOf(Crd(r, c + 1), Crd(r, c - 1), Crd(r + 1, c), Crd(r - 1, c))
    }

    private val coin: Boolean get() = (0..1).random() > 0

    init { if (image.isNotEmpty()) size = image.substringBefore("\n").length / 2 }

    private val maze = MutableList(size) { MutableList(size) { Cell.WALL } }

    init { if (image.isNotEmpty()) image.let { check(it).run { decode(it) } } else make() }

    private fun decode(img: String) = (0 until size).let { for (r in it) for (c in it) decodeCell(img, r, c) }
    private fun decodeCell(img: String, r: Int, c: Int) { if (isPass(img, r, c)) maze[r][c] =
        Cell.PASS
    }
    private fun isPass(img: String, r: Int, c: Int) = img[r * size * 2 + r + c * 2] == ' '
    private fun check(img: String) = isBad(img) ?: throw Exception("Cannot load the maze. It has an invalid format")
    private fun isBad(image: String) = if (size * 2 * size + size - 1 != image.length) null else 0
    private fun make() = prim(mutableSetOf(), Crd(rnd(), rnd()).also { setCell(it) }).also { addExits() }
    private fun addExits() = repeat(2) { addExit() }
    private fun addExit(): Crd = (if (coin) getHorCrd() else getVerCrd())?.also { setExitPass(it) } ?: addExit()
    private fun setExitPass(crd: Crd) = crd.apply { setCell(crd) }.also { bottomExit(it) }.also { rightExit(it) }
    private fun bottomExit(crd: Crd) { if (crd.r == size - 1) setCell(Crd(crd.r - 1, crd.c)) }
    private fun rightExit(crd: Crd) { if (crd.c == size - 1) setCell(Crd(crd.r, crd.c - 1)) }
    private fun getVerCrd() = Crd(rnd(), if (coin) 0 else size - 1).let { if (isCell(it)) it else null }
    private fun getHorCrd() = Crd(if (coin) 0 else size - 1, rnd()).let { if (isCell(it)) it else null }
    private fun prim(ways: MutableSet<Crd>, cur: Crd): Unit =
        ways.addAll(getSteps(cur)).run { ways.randomOrNull() ?: return }
            .also { ways.remove(stepTo(it, getNearPath(it))) }.let { if (ways.size > 0) prim(ways, it) }
    private fun getNearPath(from: Crd) = from.getNearList().filter { inField(it) && !isCell(it) }.random()
    private fun rnd() = (1..(size - 1) / 2).random() * 2 - 1
    private fun setCell(crd: Crd, cell: Cell = Cell.PASS) = crd.also { maze[crd.r][crd.c] = cell }
    private fun stepTo(from: Crd, to: Crd) = setCell(from).also { setCell(from.step(to)) }.let { from }
    private fun inField(crd: Crd) = (1..size - 2).let { crd.run { r in it && c in it } }
    private fun isCell(crd: Crd, cell: Cell = Cell.WALL) = maze[crd.r][crd.c] == cell
    private fun getSteps(crd: Crd) = crd.getNearList().filter { inField(it) && isCell(it) }
    override fun toString() = maze.joinToString("\n") { it.joinToString("") }
    private fun clone() = Maze(image = toString())
    private fun doExit(crd: Crd, from: Crd) = findExit(crd, from).let { it?.apply { setCell(from,
        Cell.SOLVE
    ) } }
    private fun walk(to: Crd, from: Crd) = to.getWays().filter { it != from }.firstNotNullOfOrNull { doExit(it, to) }
    private fun findExit(to: Crd, from: Crd): Crd? =
        if (!isCell(to, Cell.PASS)) null else if (!inField(to)) setCell(to, Cell.SOLVE) else walk(to, from)
    private fun getEntry() = clone().findExit(Crd(), Crd())!!
    private fun getStart(entry: Crd) = entry.also { setCell(it, Cell.SOLVE) }.getWays().find { inField(it) }!!
    fun solve() = clone().apply { getEntry().let { entry -> getStart(entry).also { findExit(it, entry) } } }
}

class MazeManager {
    enum class Command(val code: String, val handler: MazeManager.() -> Unit, val item: String, val opt: Boolean) {
        ERROR("*", MazeManager::error, "\n=== Menu ===", true),
        GENERATE("1", MazeManager::create, "1. Generate a new maze", true),
        LOAD("2", MazeManager::loadMaze, "2. Load a maze", true),
        SAVE("3", MazeManager::saveMaze, "3. Save the maze", false),
        DISPLAY("4", MazeManager::display, "4. Display the maze", false),
        SOLVE("5", MazeManager::solve, "5. Find the escape", false),
        EXIT("0", MazeManager::exit, "0. Exit", true);
        companion object { fun parse(str: String) = values().find { it.code == str } ?: ERROR }
    }
    private var maze: Maze? = null
    fun start() = try { while (true) menu().handler.invoke(this) } catch (e: Exception) { println(e.message) }
    private fun menu() = println(menuItems().joinToString("\n")).run { Command.parse(readln()) }
    private fun menuItems() = Command.values().filter { it.code != "*" && it.opt || maze != null }.map { it.item }
    private fun error() = println("Incorrect option. Please try again")
    private fun create() { maze = generate().also { println(it) } }
    private fun generate() = Maze(println("Enter the size of a new maze").let { readln().toInt() })
    private fun loadMaze() = try { load(readln()) } catch (e: Exception) { println(e.message) }
    private fun load(fName: String) = File(fName).also { checkFile(it) }.readText().let { maze = Maze(image = it) }
    private fun checkFile(file: File) { if (!file.isFile()) throw Exception("The file $file does not exist") }
    private fun saveMaze() = File(readln()).writeText(maze?.toString()!!)
    private fun display() = println(maze)
    private fun solve() = maze!!.solve().let(::println)
    private fun exit(): Nothing = throw Exception("Bye!")
}