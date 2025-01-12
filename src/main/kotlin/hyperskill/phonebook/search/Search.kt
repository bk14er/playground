package phonebook.search

import phonebook.PhoneBook


typealias SearchResult = Pair<Int, Int>

interface Search {
    /**
     * @return how many items found
     */
    operator fun invoke(phoneBook: PhoneBook): SearchResult

    fun name(): String

}