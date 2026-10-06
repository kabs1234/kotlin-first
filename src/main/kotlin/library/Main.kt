package library

import kotlin.time.measureTimedValue
import kotlinx.coroutines.runBlocking

private const val SIMULATION_DAYS = 30
private const val MODERN_ERA_START = 2000

fun main() = runBlocking {
    val library = Library(SampleData.books)

    printSection("New arrivals")
    val (arrivals, loadTime) = measureTimedValue { loadNewArrivals(SampleData.publisherFeeds) }
    library.addBooks(arrivals)
    arrivals.forEach { println("  + ${it.title} (${it.author})") }
    println("  ${SampleData.publisherFeeds.size} feeds loaded concurrently in ${loadTime.inWholeMilliseconds} ms")

    printSection("Members")
    SampleData.members.forEach { println("  ${it.describe()}") }

    printSection("Simulation")
    val receipts = runSimulation(library, SampleData.schedule)

    printSection("Report")
    printReport(library, receipts)
}

fun runSimulation(library: Library, schedule: Map<Int, List<LibraryEvent>>): List<Receipt> {
    val receipts = mutableListOf<Receipt>()
    for (day in 1..SIMULATION_DAYS) {
        val events = schedule[day] ?: continue
        for (event in events) {
            val message = when (event) {
                is LibraryEvent.Borrow -> describe(library.borrow(event.member, event.isbn, day))
                is LibraryEvent.Return -> library.returnBook(event.isbn, day).also { receipts += it }.let(::describe)
            }
            println("  Day ${day.toString().padStart(2)}: $message")
        }
    }
    return receipts
}

fun describe(result: BorrowResult): String = when (result) {
    is BorrowResult.Borrowed -> "${result.loan.member.name} borrowed \"${result.loan.book.title}\", due day ${result.loan.dueDay}"
    is BorrowResult.AlreadyOnLoan -> "\"${result.book.title}\" is already with ${result.holder.name}"
    is BorrowResult.LimitReached -> "${result.member.name} hit the limit of ${result.member.loanLimit} books"
    is BorrowResult.UnknownBook -> "no book with ISBN ${result.isbn}"
}

fun describe(receipt: Receipt): String {
    val who = receipt.loan.member.name
    val title = receipt.loan.book.title
    return if (receipt.daysLate == 0) {
        "$who returned \"$title\" on time"
    } else {
        "$who returned \"$title\" ${receipt.daysLate} days late, fee ${formatMoney(receipt.fee)}"
    }
}

fun publishedSince(year: Int): (Book) -> Boolean = { book -> book.publishedYear >= year }

fun printReport(library: Library, receipts: List<Receipt>) {
    val books = library.books
    val genres: Set<Genre> = books.flatMap { it.genres }.toSet()
    val booksPerGenre: Map<Genre, Int> = genres.associateWith { genre -> books.count { genre in it.genres } }
    val oldestBook = books.reduce { oldest, book -> if (book.publishedYear < oldest.publishedYear) book else oldest }
    val modernTitles = library.findBooks(publishedSince(MODERN_ERA_START)).map { it.title }
    val totalFees = receipts.map { it.fee }.reduceOrNull { sum, fee -> sum + fee } ?: 0.0
    val lateReaders = receipts.filter { it.daysLate > 0 }.map { it.loan.member.name }.toSet()

    println("  Books in catalog: ${books.size}")
    println("  Books per genre:  ${booksPerGenre.toSortedMap()}")
    println("  Oldest book:      ${oldestBook.title} (${oldestBook.publishedYear})")
    println("  Since $MODERN_ERA_START:       $modernTitles")
    println("  Late readers:     $lateReaders")
    println("  Fees collected:   ${formatMoney(totalFees)}")
}

fun printSection(title: String) {
    println()
    println("== $title ==")
}

fun formatMoney(amount: Double): String = "$%.2f".format(amount)
