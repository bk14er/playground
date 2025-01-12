package com.bk14er.hyperskill.vcs

import java.security.MessageDigest
import java.io.File
import java.time.Instant

const val VCS_DIRECTORY_NAME = "vcs"

private val commandMap = mapOf(
    "config" to CommandType.CONFIG,
    "add" to CommandType.ADD,
    "log" to CommandType.LOG,
    "commit" to CommandType.COMMIT,
    "checkout" to CommandType.CHECKOUT
)

fun Instant.hash(): String {
    val digest = MessageDigest.getInstance("SHA-256")
    val epochSecondBytes = this.epochSecond.toString().toByteArray()
    val nanoBytes = this.nano.toString().toByteArray()
    digest.update(epochSecondBytes)
    digest.update(nanoBytes)
    return digest.digest().joinToString("") { "%02x".format(it) }
}

fun File.hash(algorithm: String = "SHA-1"): String {
    val digest = MessageDigest.getInstance(algorithm)
    this.inputStream().buffered().use { inputStream ->
        val buffer = ByteArray(1024)
        var byteReads: Int

        while (inputStream.read(buffer).also { byteReads = it } != -1) {
            digest.update(buffer, 0, byteReads)
        }

    }
    return digest.digest().joinToString("") { "%02x".format(it) }
}

enum class CommitResultType(val message: String) {
    SUCCESS("Changes are committed."),
    NOTHING_TO_COMMIT("Nothing to commit."),
    COMMIT_MESSAGE_REQUIRED("Message was not passed.")

}

data class Command(val commandType: CommandType, val argument: String = "") {

    constructor(commandType: CommandType, args: Array<String>) : this(
        commandType,
        if (commandType == CommandType.UNKNOWN) args.getOrElse(0, {""}) else args.getOrElse(1, {""})
    )

    override fun toString(): String = commandType.description
}

data class Config(val userName: String) {

    fun isEmpty() = userName.isEmpty()

    override fun toString(): String = userName

    companion object {
        fun empty() = Config("")
    }
}

enum class CommandType(val description: String) {
    UNKNOWN("'%s' is not a SVCS command."),
    HELP(displayHelp()),
    CONFIG("Get and set a username."),
    ADD("Add a file to the index."),
    LOG("Show commit logs."),
    COMMIT("Save changes."),
    CHECKOUT("Restore a file.");
}

data class Commit(val files: List<File>, val commitId: String, val message: String = "") {

    companion object {
        fun newCommit(files: List<File>, message: String): Commit {
            return Commit(files, Instant.now().hash(), message)
        }
    }

}

enum class OperationType {
    ADD, NO_CHANGE, UPDATE
}

class FileRepository {
    private val vcsCatalog = File(VCS_DIRECTORY_NAME)
    private val configFile = File("$VCS_DIRECTORY_NAME${File.separator}config.txt")
    private val index = File("$VCS_DIRECTORY_NAME${File.separator}index.txt")
    private val log = File("$VCS_DIRECTORY_NAME${File.separator}log.txt")
    private val commitsCatalog = File("$VCS_DIRECTORY_NAME${File.separator}commits")

    init {
        if (!vcsCatalog.exists()) {
            vcsCatalog.mkdir()
            commitsCatalog.mkdir()
            configFile.createNewFile()
            index.createNewFile()
            log.createNewFile()
        }
    }

    fun getConfig(): Config =
        configFile.readText().let { if (it.isEmpty()) Config.empty() else Config(it) }

    fun setConfig(config: Config) = configFile.writeText(config.userName)

    fun addTrackedFile(file: File) = index.appendText("${file.name}\n")

    fun getAllTrackedFiles(): List<File> = index.readLines().map { File(it) }

    fun lastCommitFiles(): Set<File> {
        val catalog = commitsCatalog.listFiles() ?: return emptySet()
        return catalog.maxByOrNull { it.lastModified() }?.listFiles()?.toSet() ?: emptySet()
    }

    fun findCommit(commitId: String): Commit? {
        val commitDirectory = File(commitsCatalog, commitId)
        if (!commitDirectory.exists()) {
            return null
        }
        val files = commitDirectory.listFiles()?.toList() ?: return null
        return Commit(files, commitId)
    }

    fun addCommit(commit: Commit) {
        val commitDirectory = File(commitsCatalog, commit.commitId).apply { mkdir() }
        commit.files.forEach { it.copyTo(File(commitDirectory, it.name)) }
        val logMessage = """
            commit ${commit.commitId}
            Author: ${getConfig().userName}
            ${commit.message}
            ${System.lineSeparator()}
        """.trimIndent()
        log.writeText(logMessage + log.readText())
    }

    fun getLogs(): String = log.readLines().joinToString("\n").ifEmpty { "No commits yet." }

}

private fun parseCommand(args: Array<String>): Command {
    return if (args.isEmpty() || args[0] == "--help") {
        Command(CommandType.HELP)
    } else {
        Command(
            commandMap[args[0]] ?: CommandType.UNKNOWN,
            args
        )
    }
}

private fun handleConfig(command: Command, fileRepository: FileRepository) {
    val currentConfig = fileRepository.getConfig()
    if (command.argument.isEmpty()) {
        if (currentConfig.isEmpty()) {
            println("Please, tell me who you are.")
        } else {
            println("The username is $currentConfig.")
        }
    } else {
        fileRepository.setConfig(Config(command.argument))
        println("The username is ${command.argument}.")
    }
}

private fun handleAdd(command: Command, fileRepository: FileRepository) {
    val trackedFiles = fileRepository.getAllTrackedFiles().map { it.name }
    if (command.argument.isEmpty()) {
        if (trackedFiles.isNotEmpty()) {
            println("Tracked files:")
            println(trackedFiles.joinToString("\n"))
        } else {
            println(command)
        }
    } else {
        val fileName = command.argument
        val file = File(fileName)

        if (!file.exists()) {
            println("Can't find '$fileName'.")
            return
        }

        if (trackedFiles.contains(fileName)) {
            println("The file '$fileName' is already tracked.")
            return
        }

        fileRepository.addTrackedFile(file)
        println("The file '$fileName' is tracked.")
    }
}

private fun displayHelp() = """
    These are SVCS commands:
    config     Get and set a username.
    add        Add a file to the index.
    log        Show commit logs.
    commit     Save changes.
    checkout   Restore a file.
""".trimIndent()

fun diff(trackedFiles: List<File>, committedFiles: Set<File>): List<Pair<OperationType, File>> {
    val committedMap = committedFiles.associateBy { it.name }

    return trackedFiles.map { file ->
        when {
            file.name !in committedMap -> OperationType.ADD to file
            file.hash() != committedMap[file.name]?.hash() -> OperationType.UPDATE to file
            else -> OperationType.NO_CHANGE to file
        }
    }
}

private fun handleCommit(command: Command, fileRepository: FileRepository): CommitResultType {
    val commitMessage = command.argument

    if (commitMessage.isEmpty()) {
        return CommitResultType.COMMIT_MESSAGE_REQUIRED
    }

    val lastCommitFiles = fileRepository.lastCommitFiles()
    val trackedFiles = fileRepository.getAllTrackedFiles()

    if (trackedFiles.isEmpty()) {
        return CommitResultType.NOTHING_TO_COMMIT
    }

    // The first commit
    if (lastCommitFiles.isEmpty()) {
        val commit = Commit.newCommit(trackedFiles, commitMessage)
        fileRepository.addCommit(commit)
        return CommitResultType.SUCCESS
    }

    // Check if there are any changes
    val difference = diff(trackedFiles, lastCommitFiles)

    val nothingToCommit =
        difference.isEmpty() || difference.all { it.first == OperationType.NO_CHANGE }

    if (nothingToCommit) {
        return CommitResultType.NOTHING_TO_COMMIT
    }

    val commit = Commit.newCommit(difference.map { it.second }, commitMessage)
    fileRepository.addCommit(commit)
    return CommitResultType.SUCCESS
}

private fun handleCheckout(
    command: Command,
    repository: FileRepository,
) {

    val commitId = command.argument
    if(commitId.isEmpty()){
        println("Commit id was not passed.")
        return
    }

    val commitById = repository.findCommit(commitId)
    if(commitById == null){
        println("Commit does not exist.")
        return
    }

    val trackedFiles =  repository.getAllTrackedFiles()

    trackedFiles.forEach{ trackedFile ->
        commitById.files.forEach{ inComitFile ->
            if(trackedFile.name == inComitFile.name){
                inComitFile.copyTo(trackedFile, overwrite = true)
            }
        }
    }

    println("Switched to commit $commitId.")
}

private fun handleCommand(command: Command, fileRepository: FileRepository) {
    when (command.commandType) {
        CommandType.CONFIG -> handleConfig(command, fileRepository)
        CommandType.ADD -> handleAdd(command, fileRepository)
        CommandType.COMMIT -> handleCommit(command, fileRepository).also { println(it.message) }
        CommandType.CHECKOUT -> handleCheckout(command, fileRepository)
        CommandType.LOG -> println(fileRepository.getLogs())
        CommandType.HELP -> println(CommandType.HELP.description)
        CommandType.UNKNOWN -> println(CommandType.UNKNOWN.description.format(command.argument))
        else -> println(command)
    }
}

fun main(args: Array<String>) {
    val command = parseCommand(args)
    val fileRepository = FileRepository()
    handleCommand(command, fileRepository)
}


