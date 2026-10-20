package library.mobile.data

private const val TECH_PRESS_LATENCY_MILLIS = 1_500L
private const val SCIENCE_HOUSE_LATENCY_MILLIS = 1_000L

object SampleData {
    val members: List<Member> = listOf(
        Student("S1", "Alice", "Computer Science"),
        Student("S2", "Bob", "History"),
        Professor("P1", "Dr. Kim"),
        Guest("G1", "Visitor"),
    )

    val books: List<Book> = listOf(
        Book("111", "Clean Code", "Robert C. Martin", 2008, setOf(Genre.PROGRAMMING)),
        Book("222", "Kotlin in Action", "Dmitry Jemerov", 2017, setOf(Genre.PROGRAMMING)),
        Book("333", "The Hobbit", "J. R. R. Tolkien", 1937, setOf(Genre.FANTASY, Genre.CLASSIC)),
        Book("444", "Sapiens", "Yuval Noah Harari", 2011, setOf(Genre.HISTORY, Genre.SCIENCE)),
        Book("555", "Pride and Prejudice", "Jane Austen", 1813, setOf(Genre.CLASSIC)),
    )

    val publisherFeeds: List<PublisherFeed> = listOf(
        PublisherFeed(
            latencyMillis = TECH_PRESS_LATENCY_MILLIS,
            books = listOf(Book("666", "Effective Java", "Joshua Bloch", 2018, setOf(Genre.PROGRAMMING))),
        ),
        PublisherFeed(
            latencyMillis = SCIENCE_HOUSE_LATENCY_MILLIS,
            books = listOf(Book("777", "A Brief History of Time", "Stephen Hawking", 1988, setOf(Genre.SCIENCE))),
        ),
    )
}
