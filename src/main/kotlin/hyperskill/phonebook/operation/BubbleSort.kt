package phonebook.operation

import phonebook.PhoneBook
import phonebook.Timer
import java.time.Duration

class BubbleSort(private val timeout: Duration) : Operation {

    override fun execute(phoneBook: PhoneBook): PhoneBook {
        val timer = Timer.startCounting()
        val sortedRecords = phoneBook.records.toMutableList()

        for (i in 0 until sortedRecords.size) {

            for (j in 0 until sortedRecords.size - i - 1) {
                if (sortedRecords[j] > sortedRecords[j + 1]) {
                    val temp = sortedRecords[j]
                    sortedRecords[j] = sortedRecords[j + 1]
                    sortedRecords[j + 1] = temp
                }
            }

            if (tooLongExecution(timer)) {
                throw SortTimeoutException(timer.elapsed(), timeout)
            }

        }

        return PhoneBook(
            records = sortedRecords,
            targets = phoneBook.targets,
        )
    }

    private fun tooLongExecution(timer: Timer): Boolean {
        return timer.elapsed() > timeout
    }

}