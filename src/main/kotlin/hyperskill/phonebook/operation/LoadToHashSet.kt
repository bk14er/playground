package phonebook.operation

import phonebook.PhoneBook

class LoadToHashSet : Operation {
    override fun execute(phoneBook: PhoneBook): PhoneBook {

        val records = HashSet(phoneBook.records)

        return phoneBook.copy(
            records = records,
            names = records.map { it.name }.toCollection(HashSet())
        )
    }

    override fun name(): String = "Creating"
}