package ir.spaceweather.app.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

/** Offline animation; progress is a startup deadline indicator, not downloaded byte percentage. */
@Composable
fun LaunchLoading(progress: Float) {
    val transition = rememberInfiniteTransition(label = "solar-storm")
    val phase by transition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2400, easing = LinearEasing)), label = "cme"
    )
    Column(
        Modifier.fillMaxSize().background(Color(0xFF080F22)).padding(26.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Canvas(Modifier.fillMaxWidth().height(260.dp)) {
            val sun = Offset(size.width * .20f, size.height * .49f)
            val earth = Offset(size.width * .82f, size.height * .49f)
            val radius = size.minDimension * .17f
            drawCircle(brush = Brush.radialGradient(listOf(Color(0xFFFFE37B), Color(0xFFFF8B20), Color(0xFFB62A11)), center = sun, radius = radius), radius = radius, center = sun)
            drawCircle(color = Color(0x55FF9C32), radius = radius * 1.3f, center = sun, style = Stroke(5.dp.toPx()))
            drawCircle(brush = Brush.radialGradient(listOf(Color(0xFF64D9FF), Color(0xFF146CB2), Color(0xFF07335C)), center = earth, radius = radius * .75f), radius = radius * .75f, center = earth)
            val impact = (phase * 1.4f).coerceIn(0f, 1f)
            val projectile = Offset(sun.x + (earth.x - sun.x) * impact, sun.y - 18.dp.toPx() * kotlin.math.sin(impact * Math.PI).toFloat())
            drawLine(Color(0x77FF8D37), sun, projectile, 7.dp.toPx())
            drawCircle(Color(0xFFFFE4A0), radius * .24f, projectile)
            if (impact > .85f) {
                drawCircle(Color(0xAA56D9FF), radius * 1.10f, earth, style = Stroke(3.dp.toPx()))
                drawArc(Color(0xFF55E8A0), 195f, 150f, false,
                    topLeft = Offset(earth.x - radius, earth.y - radius),
                    size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
                    style = Stroke(4.dp.toPx()))
            }
        }
        Spacer(Modifier.height(20.dp))
        Text("SPACEWEATHER.IR", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 24.sp)
        Text("مرکز رصد فعالیت‌های خورشیدی", color = Color(0xFFBAC9E6), fontSize = 15.sp, textAlign = TextAlign.Center)
        Spacer(Modifier.height(35.dp))
        LinearProgressIndicator(
            progress = { progress.coerceIn(0f, 1f) },
            modifier = Modifier.fillMaxWidth().height(9.dp),
            color = Color(0xFFFFA52B), trackColor = Color(0xFF27344E),
            gapSize = 0.dp, drawStopIndicator = {}
        )
        Spacer(Modifier.height(10.dp))
        Text("${(progress.coerceIn(0f, 1f) * 100).toInt()}٪", color = Color.White, fontSize = 16.sp)
        Text("در حال دریافت اطلاعات هواشناسی فضایی", color = Color(0xFF9EB0D0), fontSize = 12.sp)
    }
}
