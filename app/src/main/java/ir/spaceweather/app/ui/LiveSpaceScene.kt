package ir.spaceweather.app.ui

import androidx.compose.animation.core.*
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.spaceweather.app.R
import ir.spaceweather.app.data.*

private fun sunTier(h: Hourly?, charts: Charts): Int = when (Status.xrayClassOf(h, charts)?.firstOrNull()?.uppercaseChar()) {
    'X' -> 3
    'M' -> 2
    'A', 'B', 'C' -> 1
    else -> 0
}
private fun earthTier(h: Hourly?, charts: Charts): Int {
    val kp = h?.kp ?: charts.last("kp")
    return when { kp == null -> 0; kp >= 6 -> 3; kp >= 4 -> 2; else -> 1 }
}

/** Nine bundled, offline WebP illustrations. Independent X-ray/Kp levels, no fake telemetry. */
@Composable
private fun AnimatedArtwork(kind: String, tier: Int, title: String, detail: String) {
    val images = when(kind) {
        "sun" -> intArrayOf(R.drawable.space_sun_1, R.drawable.space_sun_2, R.drawable.space_sun_3)
        "earth" -> intArrayOf(R.drawable.space_earth_1, R.drawable.space_earth_2, R.drawable.space_earth_3)
        else -> intArrayOf(R.drawable.space_aurora_1, R.drawable.space_aurora_2, R.drawable.space_aurora_3)
    }
    val transition = rememberInfiniteTransition(label = "space-artwork")
    val scale by transition.animateFloat(1f, 1.055f, infiniteRepeatable(tween(4500, easing = LinearEasing), RepeatMode.Reverse), label = "breathing")
    val alpha by transition.animateFloat(.92f, 1f, infiniteRepeatable(tween(2900), RepeatMode.Reverse), label = "glow")
    Column(Modifier.fillMaxWidth().background(C.night, RoundedCornerShape(18.dp))
        .border(1.dp, C.line, RoundedCornerShape(18.dp))) {
        Crossfade(targetState = (if (tier == 0) 1 else tier).coerceIn(1, 3),
            animationSpec = tween(durationMillis = 1100), label = "solar-tier") { level ->
            Image(painterResource(images[level - 1]),
                contentDescription = title, contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth().height(if (kind == "sun") 205.dp else 155.dp)
                    .graphicsLayer { scaleX = scale; scaleY = scale; this.alpha = alpha })
        }
        Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween) {
            Text(title, color = C.text, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(detail, color = if(tier == 0) C.muted else C.sun, fontSize = 12.sp)
        }
    }
}

@Composable
fun SolarArtwork(h: Hourly?, charts: Charts) {
    val tier = sunTier(h, charts)
    AnimatedArtwork("sun", tier, tr("☀ فعالیت خورشیدی", "☀ Solar activity"),
        "X-ray: ${Status.xrayClassOf(h, charts) ?: "—"}")
}

@Composable
fun EarthArtwork(h: Hourly?, charts: Charts) {
    val tier = earthTier(h, charts)
    val kp = h?.kp ?: charts.last("kp")
    AnimatedArtwork("earth", tier, tr("🌍 میدان مغناطیسی زمین", "🌍 Earth's magnetic field"),
        "Kp: ${kp?.let { Fa.num(it, 1) } ?: "—"}")
}

@Composable
fun AuroraArtwork(h: Hourly?, charts: Charts) {
    val tier = earthTier(h, charts)
    AnimatedArtwork("aurora", tier, tr("✦ شفق قطبی", "✦ Aurora"),
        if(tier == 0) "—" else when(tier) {
            3 -> tr("فعالیت شدید", "Strong activity")
            2 -> tr("فعالیت متوسط", "Moderate activity")
            else -> tr("فعالیت ضعیف", "Weak activity")
        })
    Text(tr("نمایش هنری وابسته به Kp؛ پیش‌بینی موقعیت شفق نیست.",
        "Kp-based illustration, not a local aurora forecast."), color = C.muted, fontSize = 11.sp)
}

@Composable
fun LiveSpaceScene(h: Hourly?, charts: Charts) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SolarArtwork(h, charts)
        EarthArtwork(h, charts)
        AuroraArtwork(h, charts)
    }
}
