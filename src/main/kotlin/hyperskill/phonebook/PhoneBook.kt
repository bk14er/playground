package phonebook

import hyperskill.phonebook.Contact

// Should be generic.
data class PhoneBook(
    val records: Collection<Contact>,
    val targets: Collection<String>,
    val names: Collection<String> = records.map { it.name },
)