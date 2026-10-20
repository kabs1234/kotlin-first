package library.mobile.data

interface LateFeePolicy {
    fun lateFee(daysLate: Int): Double
}

abstract class Member(val id: String, val name: String) : LateFeePolicy {
    abstract val loanLimit: Int
    abstract val loanPeriodDays: Int
    abstract val role: String

    open fun describe(): String {
        return "Up to ${countOf(loanLimit, "book")} for ${countOf(loanPeriodDays, "day")}"
    }
}

class Student(id: String, name: String, private val faculty: String) : Member(id, name) {
    override val loanLimit = 2
    override val loanPeriodDays = 14
    override val role = "Student"

    override fun lateFee(daysLate: Int): Double = daysLate * DAILY_FEE

    override fun describe(): String = "$faculty. ${super.describe()}, ${formatMoney(DAILY_FEE)} per late day"

    private companion object {
        const val DAILY_FEE = 0.5
    }
}

class Professor(id: String, name: String) : Member(id, name) {
    override val loanLimit = 5
    override val loanPeriodDays = 60
    override val role = "Professor"

    override fun lateFee(daysLate: Int): Double = 0.0

    override fun describe(): String = "${super.describe()}, no late fees"
}

class Guest(id: String, name: String) : Member(id, name) {
    override val loanLimit = 1
    override val loanPeriodDays = 7
    override val role = "Guest"

    override fun lateFee(daysLate: Int): Double =
        if (daysLate > 0) FLAT_FEE + daysLate * DAILY_FEE else 0.0

    override fun describe(): String = "${super.describe()}, ${formatMoney(FLAT_FEE)} + ${formatMoney(DAILY_FEE)} per late day"

    private companion object {
        const val FLAT_FEE = 2.0
        const val DAILY_FEE = 1.0
    }
}
