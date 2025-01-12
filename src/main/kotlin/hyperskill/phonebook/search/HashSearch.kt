package phonebook.search

import phonebook.PhoneBook

class HashSearch : Search {
    override fun invoke(phoneBook: PhoneBook): SearchResult {
        var found = 0
        val targets = phoneBook.targets
        val records = phoneBook.names as HashSet<String>

        for (target in targets) {
            if (records.contains(target)) {
                ++found
                continue
            }

        }

        return found to targets.size
    }

    override fun name(): String = "hash table"
}