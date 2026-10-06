package library

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay

/** Stands in for a remote publisher API; the delay imitates network latency. */
class PublisherFeed(val name: String, private val latencyMillis: Long, private val books: List<Book>) {
    suspend fun fetchNewArrivals(): List<Book> {
        delay(latencyMillis)
        return books
    }
}

/** Queries every feed at the same time, so the total wait is the slowest feed, not the sum. */
suspend fun loadNewArrivals(feeds: List<PublisherFeed>): List<Book> = coroutineScope {
    feeds.map { feed -> async { feed.fetchNewArrivals() } }
        .awaitAll()
        .flatten()
}
