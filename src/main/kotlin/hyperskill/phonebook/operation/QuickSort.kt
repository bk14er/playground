package phonebook.operation

import phonebook.Contact
import phonebook.PhoneBook

class QuickSort : Operation {
    override fun execute(phoneBook: PhoneBook): PhoneBook {
        val sortedRecords = phoneBook.records.toMutableList()

        quickSort(sortedRecords, 0, sortedRecords.size - 1)

        return PhoneBook(
            records = sortedRecords,
            targets = phoneBook.targets
        )
    }

    private fun quickSort(contacts: MutableList<Contact>, left: Int, right: Int) {
        if (left < right) {
            // partition the array around the pivot
            val pivot = partition(contacts, left, right)

            // recursively sort the lower side
            quickSort(contacts, left, pivot - 1)
            // recursively sort the upper side
            quickSort(contacts, pivot + 1, right)
        }

    }

    private fun partition(contacts: MutableList<Contact>, left: Int, right: Int): Int {
        val pivot = contacts[right]
        var i = left - 1

        //for (j in 1 until right - 1) {
        for(j in left until right){

            if (contacts[j] < pivot) {
                i += 1
                swap(contacts, i, j)
            }
        }

        // Put the pivot to the right of the lower side.
        swap(contacts, i + 1, right)
        // New pivot
        return i + 1
    }

    private fun swap(contacts: MutableList<Contact>, index1: Int, index2: Int) {
        val temp = contacts[index1]
        contacts[index1] = contacts[index2]
        contacts[index2] = temp
    }
}