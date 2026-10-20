package library.mobile.data

fun countOf(count: Int, noun: String): String = if (count == 1) "1 $noun" else "$count ${noun}s"
