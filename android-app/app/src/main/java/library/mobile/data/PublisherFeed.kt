package library.mobile.data

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay

class PublisherFeed(private val latencyMillis: Long, private val books: List<Book>) {
    suspend fun fetchNewArrivals(): List<Book> {
        delay(latencyMillis)
        return books
    }
}

suspend fun loadNewArrivals(feeds: List<PublisherFeed>): List<Book> = coroutineScope {
    feeds.map { feed -> async { feed.fetchNewArrivals() } }
        .awaitAll()
        .flatten()
}
