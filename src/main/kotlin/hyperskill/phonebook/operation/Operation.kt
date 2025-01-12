package phonebook.operation

import phonebook.PhoneBook
import java.time.Duration

fun interface Operation {
    fun execute(phoneBook: PhoneBook): PhoneBook

    fun name() : String = "Sorting"
}

data class SortTimeoutException(val elapsed: Duration,val timeout: Duration) :
    RuntimeException("Timeout exception after ${elapsed.toSeconds()} [s]")