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
import ir.spaceweather.app.data.L
import ir.spaceweather.app.data.Prefs
import ir.spaceweather.app.ui.AppRoot
import ir.spaceweather.app.ui.LaunchLoading
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.delay
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
            val isLoading by rememberUpdatedState(state.loading)
            LaunchedEffect(Unit) {
                val start = SystemClock.elapsedRealtime()
                // No minimum wait. Exit immediately on first completed main-data refresh,
                // or after 8 seconds even if the network is slow.
                while (showLaunch) {
                    val elapsed = SystemClock.elapsedRealtime() - start
                    progress = (elapsed / 8000f).coerceIn(0f, .95f)
                    if (!isLoading && elapsed >= 150L || elapsed >= 8000L) {
                        progress = 1f
                        showLaunch = false
                    } else delay(50)
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
