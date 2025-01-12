package phonebook.search

import phonebook.Contact
import phonebook.PhoneBook

class BinarySearch : Search {
    override fun invoke(phoneBook: PhoneBook): SearchResult {
        var found = 0

        val sortedRecords = (phoneBook.records as List<Contact>)

        for (target in phoneBook.targets) {
            var left = 0
            var right = sortedRecords.size - 1

            while (left <= right) {
                val middle = (left + right) / 2

                if (target == sortedRecords[middle].name) {
                    ++found
                    break
                }

                if (target < sortedRecords[middle].name) {
                    right = middle - 1
                } else {
                    left = middle + 1
                }


            }

        }

        return found to phoneBook.targets.size
    }

    override fun name(): String = "quick sort + binary search"
}