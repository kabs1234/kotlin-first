package library

/** Decides how much a reader pays for returning a book late. */
interface LateFeePolicy {
    fun lateFee(daysLate: Int): Double
}

abstract class Member(val id: String, val name: String) : LateFeePolicy {
    abstract val loanLimit: Int
    abstract val loanPeriodDays: Int

    open fun describe(): String {
        val bookWord = if (loanLimit == 1) "book" else "books"
        return "$name: up to $loanLimit $bookWord for $loanPeriodDays days"
    }
}

class Student(id: String, name: String, private val faculty: String) : Member(id, name) {
    override val loanLimit = 2
    override val loanPeriodDays = 14

    override fun lateFee(daysLate: Int): Double = daysLate * DAILY_FEE

    override fun describe(): String = "[Student, $faculty] ${super.describe()}"

    private companion object {
        const val DAILY_FEE = 0.5
    }
}

class Professor(id: String, name: String) : Member(id, name) {
    override val loanLimit = 5
    override val loanPeriodDays = 60

    override fun lateFee(daysLate: Int): Double = 0.0

    override fun describe(): String = "[Professor] ${super.describe()}, no late fees"
}

class Guest(id: String, name: String) : Member(id, name) {
    override val loanLimit = 1
    override val loanPeriodDays = 7

    override fun lateFee(daysLate: Int): Double =
        if (daysLate > 0) FLAT_FEE + daysLate * DAILY_FEE else 0.0

    override fun describe(): String = "[Guest] ${super.describe()}"

    private companion object {
        const val FLAT_FEE = 2.0
        const val DAILY_FEE = 1.0
    }
}
