package ir.spaceweather.app

/** Fixed eight-second cinematic sequence, independent of network readiness. */
internal class StartupProgress {
    companion object { const val MAX_MS = 8000L; const val HOLD_MS = 250L }
    private var fullAt: Long? = null
    fun advance(elapsedMs: Long, ready: Boolean): Float {
        val p = (elapsedMs.toFloat() / (MAX_MS - HOLD_MS)).coerceIn(0f, 1f)
        if (p == 1f && fullAt == null) fullAt = elapsedMs
        return p
    }
    fun canDismiss(elapsedMs: Long): Boolean {
        val full = fullAt ?: return false
        return elapsedMs >= MAX_MS && elapsedMs > full
    }
}
