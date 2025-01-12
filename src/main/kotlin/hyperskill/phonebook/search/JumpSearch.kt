package phonebook.search

import phonebook.Contact
import phonebook.PhoneBook
import kotlin.math.floor
import kotlin.math.min
import kotlin.math.sqrt

class JumpSearch : Search {

    override operator fun invoke(phoneBook: PhoneBook): SearchResult {
        var found = 0
        val targets = phoneBook.targets
        val records = phoneBook.records as List<Contact>

        for (target in targets) {
            var curr = 0
            var prev = 0
            val last = records.size
            val step = floor(sqrt(last.toDouble())).toInt()

            while (target > getCurrentRecord(records, curr)) {
                if (curr == last) {
                    break
                }
                prev = curr
                curr = min(curr + step, last)
            }


            while (target < getCurrentRecord(records, curr)) {
                curr -= 1

                if (curr <= prev) {
                    break
                }
            }

            if (target contentEquals getCurrentRecord(records, curr)) {
                found++
            }

        }
        return found to targets.size
    }

    override fun name(): String = "bubble sort + jump search"

    private fun getCurrentRecord(sortedRecords: List<Contact>, index: Int) =
        sortedRecords[index].name
}