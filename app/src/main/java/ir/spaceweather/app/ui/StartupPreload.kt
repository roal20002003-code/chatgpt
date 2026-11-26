package ir.spaceweather.app.ui

import android.content.Context
import coil.imageLoader
import coil.request.ImageRequest
import ir.spaceweather.app.data.UiState
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first

internal fun imageRefreshStamp() = System.currentTimeMillis() / 600_000L
internal fun liveSunImageUrl(url: String, stamp: Long) = "$url?t=$stamp"
internal fun historicalFlareImageUrl(day: Int, stamp: Long) =
    "https://www.sidc.be/solardemon/overview.php?d=$day&b=2&w=300&fid=0&science=0&t=$stamp"

/** Warm Coil's cache while the startup screen is visible; this work can outlive the splash. */
internal suspend fun preloadStartupImages(context: Context, state: StateFlow<UiState>) = coroutineScope {
    val stamp = imageRefreshStamp()
    suspend fun load(url: String, size: Int) {
        // Coil returns ErrorResult for ordinary network errors; a failed image must not block navigation.
        context.imageLoader.execute(ImageRequest.Builder(context).data(url).size(size).build())
    }
    val static = async {
        val urls = listOf(liveSunImageUrl("https://sdo.gsfc.nasa.gov/assets/img/latest/latest_1024_HMIIF.jpg", stamp)) +
            listOf(2, 4, 6, 8).map { historicalFlareImageUrl(it, stamp) }
        urls.map { url -> async { load(url, 640) } }.awaitAll()
    }
    val latest = async {
        val flare = state.first { !it.loading }.flare
        if (flare != null) {
            val urls = listOf(flare.overview) + (1..minOf(flare.count, 3)).map { flare.frame(it) }
            urls.map { url -> async { load(url, 480) } }.awaitAll()
        }
    }
    static.await()
    latest.await()
    Unit
}
