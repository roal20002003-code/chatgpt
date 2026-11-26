package ir.spaceweather.app

/** Monotonic startup progress. The 100% state is shown before navigating. */
internal class StartupProgress {
    companion object {
        const val MAX_MS = 8000L
        const val HOLD_MS = 100L
        private const val FILL_MS = MAX_MS - HOLD_MS
        private const val FAST_MS = 450L
    }
    private var readyAt: Long? = null
    private var fullAt: Long? = null

    fun advance(elapsedMs: Long, ready: Boolean): Float {
        val elapsed = elapsedMs.coerceAtLeast(0L)
        if (ready && readyAt == null) readyAt = elapsed.coerceAtMost(FILL_MS)
        val start = readyAt
        val value = if (start == null) {
            elapsed.toFloat() / FILL_MS
        } else {
            val end = minOf(FILL_MS, start + FAST_MS)
            val initial = start.toFloat() / FILL_MS
            if (elapsed >= end) 1f
            else initial + (1f - initial) * ((elapsed - start).toFloat() / (end - start).coerceAtLeast(1L))
        }.coerceIn(0f, 1f)
        if (value == 1f && fullAt == null) fullAt = elapsed
        return value
    }

    fun canDismiss(elapsedMs: Long): Boolean {
        val full = fullAt ?: return false
        // Even if a frame stalls past the deadline, keep 100% for a rendered frame.
        return elapsedMs > full && elapsedMs >= minOf(MAX_MS, full + HOLD_MS)
    }
}
