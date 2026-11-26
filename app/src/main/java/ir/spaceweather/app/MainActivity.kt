package ir.spaceweather.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import ir.spaceweather.app.data.L
import ir.spaceweather.app.data.Prefs
import ir.spaceweather.app.ui.AppRoot
import ir.spaceweather.app.ui.LaunchLoading
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.CancellationException
import ir.spaceweather.app.ui.preloadStartupImages
import android.os.SystemClock
import ir.spaceweather.app.ui.SpaceTheme

class MainActivity : ComponentActivity() {
    private val vm: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val openAdmin = intent?.getBooleanExtra("open_admin", false) == true  // از روی نوتیف
        L.init(this)
        setContent {
            // تغییر زبان از تب تنظیمات: کل رابط با زبان و جهت جدید دوباره ساخته می‌شود
            var en by remember { mutableStateOf(L.en) }
            val state by vm.state.collectAsStateWithLifecycle()
            var showLaunch by remember { mutableStateOf(true) }
            var progress by remember { mutableStateOf(0f) }
            var imagesReady by remember { mutableStateOf(false) }
            val ready by rememberUpdatedState(!state.loading && imagesReady)
            // Kept outside the splash branch so unfinished image downloads continue after entry.
            LaunchedEffect(Unit) {
                try {
                    preloadStartupImages(this@MainActivity, vm.state)
                } catch (e: CancellationException) {
                    throw e
                } catch (_: Exception) {
                    // A failed preload must never prevent entry.
                } finally {
                    imagesReady = true
                }
            }
            LaunchedEffect(Unit) {
                val timer = StartupProgress()
                withFrameNanos { } // first frame at 0%
                val start = SystemClock.elapsedRealtime()
                while (showLaunch) {
                    withFrameNanos { }
                    val elapsed = SystemClock.elapsedRealtime() - start
                    progress = timer.advance(elapsed, ready)
                    if (timer.canDismiss(elapsed)) showLaunch = false
                }
            }
            SpaceTheme(rtl = !en) {
                key(en) {
                    if (showLaunch) LaunchLoading(progress) else AppRoot(vm, openAdmin, onLanguage = { toEn ->
                        L.en = toEn
                        Prefs(this@MainActivity).lang = if (toEn) "en" else "fa"
                        en = toEn
                    })
                }
            }
        }
    }
}
