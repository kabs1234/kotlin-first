package library.mobile.data

data class Loan(val book: Book, val member: Member, val borrowedOnDay: Int) {
    val dueDay: Int get() = borrowedOnDay + member.loanPeriodDays

    fun isOverdue(today: Int): Boolean = today > dueDay
}

data class Receipt(val loan: Loan, val returnedOnDay: Int) {
    val daysLate: Int get() = (returnedOnDay - loan.dueDay).coerceAtLeast(0)
    val fee: Double get() = loan.member.lateFee(daysLate)
}

data class BorrowRequest(val member: Member, val isbn: String, val day: Int)

sealed class BorrowResult {
    data class Borrowed(val loan: Loan) : BorrowResult()
    data class AlreadyOnLoan(val book: Book, val holder: Member) : BorrowResult()
    data class LimitReached(val member: Member) : BorrowResult()
    data class UnknownBook(val isbn: String) : BorrowResult()
}
