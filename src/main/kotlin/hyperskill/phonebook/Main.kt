package phonebook

import phonebook.Container.Companion.catch
import phonebook.operation.*
import phonebook.search.*
import java.io.File
import java.time.Duration

private fun timeTaken(total: Duration): String {
    return String.format("%d min. %d sec. %d ms.", total.toMinutes(), total.seconds % 60, total.toMillis() % 1000)
}

fun main() {
    val test = AlgorithmTester()
    val repository = FileRepository()

    test(
        AlgorithmTesterConfig(
            search = LinearSearch(), phoneBook = repository.readPhoneBooks()
        )
    ).also { println() }.run { multipliedBy(10) }.let {
            test(
                AlgorithmTesterConfig(
                    search = JumpSearch(),
                    phoneBook = repository.readPhoneBooks(),
                    operation = BubbleSort(it),
                    alternativeSearch = LinearSearch()
                )
            )
        }.also { println() }.let {
            test(
                AlgorithmTesterConfig(
                    search = BinarySearch(),
                    phoneBook = repository.readPhoneBooks(),
                    operation = QuickSort(),
                )
            )
        }
        .also { println() }.let {
            test(
                AlgorithmTesterConfig(
                    search = HashSearch(),
                    phoneBook = repository.readPhoneBooks(),
                    operation = LoadToHashSet(),
                )
            )
        }
}

data class AlgorithmTesterConfig(
    val search: Search,
    val phoneBook: PhoneBook,
    val operation: Operation? = null,
    val alternativeSearch: Search? = null
)

private class AlgorithmTester {
    operator fun invoke(
        config: AlgorithmTesterConfig
    ): Duration {
        val (search, phoneBook, operation, alternativeSearch) = config

        val timer = Timer.startCounting()
        println("Start searching (${search.name()})...")

        operation?.let { sorter -> catch { sorter.execute(phoneBook) } }.let { sortResult ->
            when (sortResult) {
                is Container.Success -> {
                    val sortTime = timer.elapsed()
                    val searchResult = search(sortResult.value)
                    val searchTime = timer.elapsed()
                    val total = sortTime + searchTime

                    displaySearchSummary(searchResult, total)
                    println("${operation!!.name()} time: ${timeTaken(sortTime)}")
                    println("Search time: ${timeTaken(searchTime)}")

                    return total
                }

                is Container.Failure -> {
                    val sortTime = timer.elapsed()

                    val searchTimer = Timer.startCounting()
                    val searchResult = alternativeSearch!!(phoneBook)
                    val searchTime = searchTimer.elapsed()

                    val total = sortTime + searchTime

                    displaySearchSummary(searchResult, total)
                    println("Sorting time: ${timeTaken(sortTime)} - STOPPED, moved to linear search")
                    println("Searching time: ${timeTaken(searchTime)}")
                    return total
                }

                // No sorter - only search.
                null -> {
                    val searchResult = search(phoneBook)
                    val searchTime = timer.elapsed()

                    displaySearchSummary(searchResult, searchTime)

                    return searchTime
                }
            }
        }
    }

    private fun displaySearchSummary(searchResult: SearchResult, searchTime: Duration) {
        println(
            "Found ${searchResult.first} / ${searchResult.second} entries. Time taken: ${
                timeTaken(
                    searchTime
                )
            }"
        )
    }


}

private enum class FileSource(val path: String) {
    FIND("D:\\hyperskills\\find.txt"), DIRECTORY("D:\\hyperskills\\directory.txt"),
//    FIND("D:\\hyperskills\\small_find.txt"),
//    DIRECTORY("D:\\hyperskills\\small_directory.txt")
}

private class FileRepository {

    fun readPhoneBooks(): PhoneBook =
        readAll(FileSource.DIRECTORY).map { Contact(it) }.let { PhoneBook(it, readToFind()) }

    private fun readToFind(): List<String> = readAll(FileSource.FIND)

    private fun readAll(file: FileSource): List<String> = File(file.path).readLines()

}

sealed class Container<out L, out R> {

    data class Failure<L>(val value: L) : Container<L, Nothing>()

    data class Success<R>(val value: R) : Container<Nothing, R>()

    companion object {
        inline fun <R> catch(block: () -> R): Container<Throwable, R> {
            return try {
                Success(block())
            } catch (e: SortTimeoutException) {
                Failure(e)
            } catch (e: Error) {
                throw e
            }
        }
    }
}