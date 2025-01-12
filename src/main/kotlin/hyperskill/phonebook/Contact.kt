package phonebook

data class Contact(
    val name: String,
    val phone: String
) {

    operator fun contains(target: String): Boolean = name == target

    operator fun compareTo(pivot: Contact): Int = name.compareTo(pivot.name)

    constructor(record: String) : this(
        phone = record.substringBefore(" "),
        name = record.substringAfter(" ")
    )
}