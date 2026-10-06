package library

class Library(initialBooks: List<Book>) {
    private val catalog: MutableMap<String, Book> = initialBooks.associateBy { it.isbn }.toMutableMap()
    private val loansByIsbn: MutableMap<String, Loan> = mutableMapOf()

    val books: List<Book> get() = catalog.values.toList()

    fun addBooks(newBooks: List<Book>) {
        newBooks.forEach { catalog[it.isbn] = it }
    }

    fun findBooks(predicate: (Book) -> Boolean): List<Book> = catalog.values.filter(predicate)

    fun loansOf(member: Member): List<Loan> = loansByIsbn.values.filter { it.member == member }

    fun borrow(member: Member, isbn: String, today: Int): BorrowResult {
        val book = catalog[isbn] ?: return BorrowResult.UnknownBook(isbn)
        val currentLoan = loansByIsbn[isbn]
        return when {
            currentLoan != null -> BorrowResult.AlreadyOnLoan(book, currentLoan.member)
            loansOf(member).size >= member.loanLimit -> BorrowResult.LimitReached(member)
            else -> {
                val loan = Loan(book, member, today)
                loansByIsbn[isbn] = loan
                BorrowResult.Borrowed(loan)
            }
        }
    }

    fun returnBook(isbn: String, today: Int): Receipt {
        val loan = requireNotNull(loansByIsbn.remove(isbn)) { "Book $isbn is not on loan" }
        return Receipt(loan, today)
    }
}
