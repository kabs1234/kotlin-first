package library

object SampleData {
    private val alice = Student("S1", "Alice", "Computer Science")
    private val bob = Student("S2", "Bob", "History")
    private val drKim = Professor("P1", "Dr. Kim")
    private val visitor = Guest("G1", "Visitor")

    val members: List<Member> = listOf(alice, bob, drKim, visitor)

    val books: List<Book> = listOf(
        Book("111", "Clean Code", "Robert C. Martin", 2008, setOf(Genre.PROGRAMMING)),
        Book("222", "Kotlin in Action", "Dmitry Jemerov", 2017, setOf(Genre.PROGRAMMING)),
        Book("333", "The Hobbit", "J. R. R. Tolkien", 1937, setOf(Genre.FANTASY, Genre.CLASSIC)),
        Book("444", "Sapiens", "Yuval Noah Harari", 2011, setOf(Genre.HISTORY, Genre.SCIENCE)),
        Book("555", "Pride and Prejudice", "Jane Austen", 1813, setOf(Genre.CLASSIC)),
    )

    val publisherFeeds: List<PublisherFeed> = listOf(
        PublisherFeed(
            "Tech Press",
            latencyMillis = 400,
            books = listOf(Book("666", "Effective Java", "Joshua Bloch", 2018, setOf(Genre.PROGRAMMING))),
        ),
        PublisherFeed(
            "Science House",
            latencyMillis = 300,
            books = listOf(Book("777", "A Brief History of Time", "Stephen Hawking", 1988, setOf(Genre.SCIENCE))),
        ),
    )

    val schedule: Map<Int, List<LibraryEvent>> = mapOf(
        1 to listOf(
            LibraryEvent.Borrow(alice, "111"),
            LibraryEvent.Borrow(alice, "222"),
            LibraryEvent.Borrow(alice, "333"),
        ),
        2 to listOf(LibraryEvent.Borrow(bob, "111"), LibraryEvent.Borrow(bob, "444")),
        3 to listOf(LibraryEvent.Borrow(visitor, "777"), LibraryEvent.Borrow(drKim, "999")),
        4 to listOf(LibraryEvent.Borrow(drKim, "555")),
        10 to listOf(LibraryEvent.Return("111")),
        13 to listOf(LibraryEvent.Return("777")),
        20 to listOf(LibraryEvent.Return("222"), LibraryEvent.Return("444")),
        30 to listOf(LibraryEvent.Return("555")),
    )
}
