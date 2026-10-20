package library.mobile.ui

import library.mobile.data.Book
import library.mobile.data.Genre
import library.mobile.data.Loan
import library.mobile.data.Member
import library.mobile.data.Receipt

const val FIRST_DAY = 1

data class LibraryUiState(
    val members: List<Member>,
    val selectedMember: Member,
    val today: Int = FIRST_DAY,
    val filter: CatalogFilter = CatalogFilter(),
    val catalog: List<BookItem> = emptyList(),
    val memberLoans: List<Loan> = emptyList(),
    val receipts: List<Receipt> = emptyList(),
    val isLoadingArrivals: Boolean = false,
    val message: String? = null,
    val viewModelCounter: Int = 0,
) {
    val memberReceipts: List<Receipt> get() = receipts.filter { it.loan.member == selectedMember }
    val memberFees: Double get() = memberReceipts.sumOf { it.fee }
}

data class BookItem(val book: Book, val loan: Loan?) {
    val isAvailable: Boolean get() = loan == null
}

data class CatalogFilter(
    val query: String = "",
    val genres: Set<Genre> = emptySet(),
    val isAvailableOnly: Boolean = false,
) {
    fun matches(item: BookItem): Boolean =
        matchesQuery(item.book) && matchesGenres(item.book) && (item.isAvailable || !isAvailableOnly)

    private fun matchesQuery(book: Book): Boolean =
        query.isBlank() || book.title.contains(query, ignoreCase = true) || book.author.contains(query, ignoreCase = true)

    private fun matchesGenres(book: Book): Boolean = genres.isEmpty() || book.genres.any { it in genres }
}

data class BookDraft(val title: String, val author: String, val publishedYear: Int, val genre: Genre) {
    fun toBook(isbn: String): Book = Book(isbn, title.trim(), author.trim(), publishedYear, setOf(genre))
}
