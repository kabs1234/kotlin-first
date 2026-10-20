package library.mobile.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import java.util.UUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import library.mobile.data.Book
import library.mobile.data.BorrowRequest
import library.mobile.data.BorrowResult
import library.mobile.data.Genre
import library.mobile.data.Library
import library.mobile.data.Member
import library.mobile.data.Receipt
import library.mobile.data.SampleData
import library.mobile.data.formatMoney
import library.mobile.data.loadNewArrivals

class LibraryViewModel : ViewModel() {
    private val library = Library(SampleData.books)

    private val _uiState = MutableStateFlow(
        LibraryUiState(members = SampleData.members, selectedMember = SampleData.members.first()),
    )
    val uiState: StateFlow<LibraryUiState> = _uiState.asStateFlow()

    init {
        refreshCatalog()
        fetchNewArrivals()
    }

    fun onQueryChange(query: String) = updateFilter { it.copy(query = query) }

    fun onGenreToggle(genre: Genre) = updateFilter { filter ->
        val genres = if (genre in filter.genres) filter.genres - genre else filter.genres + genre
        filter.copy(genres = genres)
    }

    fun onAvailableOnlyToggle() = updateFilter { it.copy(isAvailableOnly = !it.isAvailableOnly) }

    fun onMemberSelect(member: Member) {
        _uiState.update { it.copy(selectedMember = member) }
        refreshCatalog()
    }

    fun borrow(book: Book) {
        val state = _uiState.value
        val result = library.borrow(BorrowRequest(state.selectedMember, book.isbn, state.today))
        _uiState.update { it.copy(message = describe(result)) }
        refreshCatalog()
    }

    fun returnBook(book: Book) {
        if (library.loanOf(book.isbn) == null) return
        val receipt = library.returnBook(book.isbn, _uiState.value.today)
        _uiState.update { it.copy(receipts = it.receipts + receipt, message = describe(receipt)) }
        refreshCatalog()
    }

    fun advanceDay() = _uiState.update { it.copy(today = it.today + 1) }

    fun addBook(draft: BookDraft) {
        val book = draft.toBook(isbn = UUID.randomUUID().toString())
        library.addBooks(listOf(book))
        _uiState.update { it.copy(message = "Added \"${book.title}\" to the catalog") }
        refreshCatalog()
    }

    fun onMessageShown() = _uiState.update { it.copy(message = null) }

    fun incrementCounter() = _uiState.update { it.copy(viewModelCounter = it.viewModelCounter + 1) }

    private fun fetchNewArrivals() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingArrivals = true) }
            val arrivals = loadNewArrivals(SampleData.publisherFeeds)
            library.addBooks(arrivals)
            _uiState.update {
                it.copy(isLoadingArrivals = false, message = "${arrivals.size} new arrivals from publishers")
            }
            refreshCatalog()
        }
    }

    private fun updateFilter(transform: (CatalogFilter) -> CatalogFilter) {
        _uiState.update { it.copy(filter = transform(it.filter)) }
        refreshCatalog()
    }

    private fun refreshCatalog() = _uiState.update { state ->
        val items = library.books.map { BookItem(it, library.loanOf(it.isbn)) }
        state.copy(
            catalog = items.filter(state.filter::matches).sortedBy { it.book.title },
            memberLoans = library.loansOf(state.selectedMember).sortedBy { it.dueDay },
        )
    }

    private fun describe(result: BorrowResult): String = when (result) {
        is BorrowResult.Borrowed -> "${result.loan.member.name} borrowed \"${result.loan.book.title}\", due day ${result.loan.dueDay}"
        is BorrowResult.AlreadyOnLoan -> "\"${result.book.title}\" is already with ${result.holder.name}"
        is BorrowResult.LimitReached -> "${result.member.name} hit the limit of ${result.member.loanLimit} books"
        is BorrowResult.UnknownBook -> "No book with ISBN ${result.isbn}"
    }

    private fun describe(receipt: Receipt): String {
        val title = receipt.loan.book.title
        return if (receipt.daysLate == 0) {
            "Returned \"$title\" on time"
        } else {
            "Returned \"$title\" ${receipt.daysLate} days late, fee ${formatMoney(receipt.fee)}"
        }
    }
}
