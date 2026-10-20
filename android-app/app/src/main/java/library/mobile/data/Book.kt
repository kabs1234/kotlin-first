package library.mobile.data

enum class Genre(val label: String) {
    CLASSIC("Classic"),
    FANTASY("Fantasy"),
    HISTORY("History"),
    PROGRAMMING("Programming"),
    SCIENCE("Science"),
}

data class Book(
    val isbn: String,
    val title: String,
    val author: String,
    val publishedYear: Int,
    val genres: Set<Genre>,
)
