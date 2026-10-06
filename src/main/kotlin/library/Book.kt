package library

enum class Genre { CLASSIC, FANTASY, HISTORY, PROGRAMMING, SCIENCE }

data class Book(
    val isbn: String,
    val title: String,
    val author: String,
    val publishedYear: Int,
    val genres: Set<Genre>,
)
