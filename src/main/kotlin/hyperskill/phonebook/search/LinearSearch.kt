package phonebook.search

import phonebook.PhoneBook

class LinearSearch : Search {

    override operator fun invoke(phoneBook: PhoneBook): SearchResult {
        var found = 0
        val targets = phoneBook.targets

        for (target in targets) {

            for (directory in phoneBook.records) {

                if (target in directory) {
                    ++found
                    break
                }
            }
        }

        return found to targets.size
    }

    override fun name(): String = "linear search"

}