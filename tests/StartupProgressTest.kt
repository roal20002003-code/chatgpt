package ir.spaceweather.app
fun main() {
    for (readyAt in listOf(0L, 100L, 1500L, 4000L, 7600L, 7900L, 8000L, Long.MAX_VALUE)) {
        val timer = StartupProgress()
        var previous = 0f
        var full = -1L
        var exit = -1L
        for (ms in 0L..8050L step 10L) {
            val p = timer.advance(ms, ms >= readyAt)
            check(p >= previous && p in 0f..1f)
            if (ms == 0L) check(p == 0f)
            if (p == 1f && full == -1L) full = ms
            if (timer.canDismiss(ms)) { exit = ms; check(p == 1f); break }
            previous = p
        }
        check(full >= 0 && exit > full && exit <= 8000)
        println("ready=$readyAt, 100%=$full, exit=$exit")
    }
}
