package library

data class Loan(val book: Book, val member: Member, val borrowedOnDay: Int) {
    val dueDay: Int get() = borrowedOnDay + member.loanPeriodDays
}

data class Receipt(val loan: Loan, val returnedOnDay: Int) {
    val daysLate: Int get() = (returnedOnDay - loan.dueDay).coerceAtLeast(0)
    val fee: Double get() = loan.member.lateFee(daysLate)
}

sealed class BorrowResult {
    data class Borrowed(val loan: Loan) : BorrowResult()
    data class AlreadyOnLoan(val book: Book, val holder: Member) : BorrowResult()
    data class LimitReached(val member: Member) : BorrowResult()
    data class UnknownBook(val isbn: String) : BorrowResult()
}

sealed class LibraryEvent {
    data class Borrow(val member: Member, val isbn: String) : LibraryEvent()
    data class Return(val isbn: String) : LibraryEvent()
}
