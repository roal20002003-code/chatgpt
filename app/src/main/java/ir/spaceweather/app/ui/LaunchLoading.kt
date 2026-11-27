package ir.spaceweather.app.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.spaceweather.app.R
import ir.spaceweather.app.data.tr
import kotlin.math.PI
import kotlin.math.sin

import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import android.graphics.BitmapFactory

/** Local storyboard flight, synchronized to the fixed eight-second startup clock. */
@Composable
fun LaunchLoading(progress: Float) {
    val p = progress.coerceIn(0f, 1f)
    val context = LocalContext.current
    val frames = remember(context) {
        listOf(R.drawable.startup_flight_00, R.drawable.startup_flight_01, R.drawable.startup_flight_02, R.drawable.startup_flight_03, R.drawable.startup_flight_04, R.drawable.startup_flight_05, R.drawable.startup_flight_06, R.drawable.startup_flight_07, R.drawable.startup_flight_08, R.drawable.startup_flight_09, R.drawable.startup_flight_10, R.drawable.startup_flight_11, R.drawable.startup_flight_12, R.drawable.startup_flight_13, R.drawable.startup_flight_14, R.drawable.startup_flight_15).map { BitmapFactory.decodeResource(context.resources, it).asImageBitmap() }
    }
    val position = p * (frames.size - 1)
    val index = position.toInt().coerceIn(0, frames.lastIndex)
    val next = (index + 1).coerceAtMost(frames.lastIndex)
    val blend = position - index
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        Box(Modifier.fillMaxSize().background(Color(0xFF030817)).statusBarsPadding().navigationBarsPadding()) {
            Image(frames[index], null, Modifier.fillMaxSize(), contentScale = ContentScale.FillBounds)
            if (next != index) Image(frames[next], null, Modifier.fillMaxSize(), contentScale = ContentScale.FillBounds, alpha = blend)
            Column(Modifier.align(Alignment.BottomCenter).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                    Text("SPACEWEATHER", color = Color.White, fontSize = 27.sp, fontWeight = FontWeight.Black)
                    Text(".IR", color = Color(0xFFFFB23F), fontSize = 27.sp, fontWeight = FontWeight.Black)
                }
                Text(tr("مرکز رصد فعالیت‌های خورشیدی", "Solar Activity Observation Center"), color = Color(0xFFE2EDFF), fontSize = 15.sp, textAlign = TextAlign.Center)
                Spacer(Modifier.height(28.dp))
                NeonProgress(p, Modifier.fillMaxWidth(.84f).height(24.dp))
                Text("${(p * 100).toInt()}%", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(12.dp))
                Text(if (p >= 1f) tr("در حال ورود به برنامه…", "Opening SpaceWeather…") else tr("در حال دریافت اطلاعات هواشناسی فضایی…", "Receiving space weather data…"), color = Color(0xFFB1DFFF), fontSize = 13.sp, textAlign = TextAlign.Center, modifier = Modifier.padding(horizontal = 12.dp))
                Spacer(Modifier.height(40.dp))
            }
        }
    }
}

@Composable
private fun NeonProgress(progress: Float, modifier: Modifier = Modifier) {
    Canvas(modifier.semantics { progressBarRangeInfo = ProgressBarRangeInfo(progress, 0f..1f) }) {
        val pad = 2.dp.toPx()
        val trackH = size.height * .68f
        val top = (size.height - trackH) / 2f
        val corner = CornerRadius(trackH / 2f)
        drawRoundRect(Color(0xFF061226), Offset(pad, top), Size(size.width - 2 * pad, trackH), corner)
        drawRoundRect(Color(0xFF267DCE), Offset(pad, top), Size(size.width - 2 * pad, trackH), corner, style = Stroke(1.4.dp.toPx()))
        val inner = 2.dp.toPx()
        val fillH = trackH - 2 * inner
        val fillW = (size.width - 2 * (pad + inner)) * progress
        if (fillW > 0f && fillH > 0f) {
            val start = Offset(pad + inner, top + inner)
            val end = Offset(start.x + fillW, start.y + fillH / 2f)
            drawRoundRect(Color(0xFF009BFF).copy(alpha = .22f), Offset(start.x, top - inner), Size(fillW, trackH + 2 * inner), CornerRadius(trackH / 2f))
            drawRoundRect(Brush.horizontalGradient(listOf(Color(0xFF0067E8), Color(0xFF00B9F5), Color(0xFFB4FFFF)), startX = start.x, endX = end.x), start, Size(fillW, fillH), CornerRadius(minOf(fillH / 2f, fillW / 2f)))
            val glow = fillH * .75f
            drawCircle(Brush.radialGradient(listOf(Color.White.copy(alpha = .8f), Color(0xFF33D9FF).copy(alpha = .25f), Color.Transparent), center = end, radius = glow), glow, end)
        }
    }
}
