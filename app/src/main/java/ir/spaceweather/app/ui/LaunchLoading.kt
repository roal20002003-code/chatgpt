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

/** Local cinematic art, animated plasma highlights, and a real runtime progress indicator. */
@Composable
fun LaunchLoading(progress: Float) {
    val p = progress.coerceIn(0f, 1f)
    val transition = rememberInfiniteTransition(label = "solar-storm")
    val phase by transition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2100, easing = LinearEasing)), label = "plasma"
    )
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        Box(Modifier.fillMaxSize().background(Color(0xFF030817)).statusBarsPadding().navigationBarsPadding()) {
            BoxWithConstraints(Modifier.fillMaxSize()) {
                Image(
                    painter = painterResource(R.drawable.startup_cinematic),
                    contentDescription = null,
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier.fillMaxSize()
                )
                // Animated highlights follow the existing plasma stream; the underlying art is local.
                Canvas(Modifier.fillMaxSize()) {
                    val impact = Offset(size.width * .718f, size.height * .355f)
                    val pulse = (.5f + .5f * sin(phase * 2f * PI).toFloat())
                    val radius = size.width * .10f
                    drawCircle(Brush.radialGradient(listOf(Color(0xFF9DEBFF).copy(alpha = .19f + .13f * pulse), Color.Transparent), center = impact, radius = radius), radius, impact)
                    val aurora = Offset(size.width * .89f, size.height * .263f)
                    val auroraRadius = size.width * .16f
                    drawCircle(Brush.radialGradient(listOf(Color(0xFF28FFAB).copy(alpha = .06f + .12f * pulse), Color.Transparent), center = aurora, radius = auroraRadius), auroraRadius, aurora)
                    for (i in 0 until 17) {
                        val t = (phase + i / 17f) % 1f
                        val x = size.width * (.245f + .47f * t)
                        val y = size.height * (.285f + .070f * t + .012f * sin(t * 11f + i).toFloat())
                        val center = Offset(x, y)
                        val alpha = (.3f + .5f * sin(t * PI).toFloat()).coerceIn(0f, 1f)
                        val r = (1.1f + (i % 3) * .55f).dp.toPx()
                        drawCircle(Color(0xFFFFA12D).copy(alpha = alpha * .24f), r * 3f, center)
                        drawCircle(Color(0xFFFFE8A8).copy(alpha = alpha), r, center)
                    }
                }
                val compact = maxHeight < 530.dp
                val subtitleSize = if (compact) 12.sp else 16.sp
                Text(
                    tr("مرکز رصد فعالیت‌های خورشیدی", "Solar Activity Observation Center"),
                    color = Color(0xFFF2F6FF), fontSize = subtitleSize,
                    fontWeight = FontWeight.Medium, textAlign = TextAlign.Center,
                    modifier = Modifier.align(Alignment.TopCenter).offset(y = maxHeight * .646f).fillMaxWidth().padding(horizontal = 18.dp)
                )
                Row(
                    Modifier.align(Alignment.TopCenter).offset(y = maxHeight * .729f).fillMaxWidth(.85f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    NeonProgress(p, Modifier.weight(1f).height(if (compact) 18.dp else 25.dp))
                    Text("${(p * 100).toInt()}%", color = Color.White, fontSize = if (compact) 17.sp else 23.sp,
                        fontWeight = FontWeight.Bold, textAlign = TextAlign.End, modifier = Modifier.width(if (compact) 48.dp else 62.dp))
                }
                Text(
                    if (p >= 1f) tr("در حال ورود به برنامه…", "Opening SpaceWeather…")
                    else tr("در حال دریافت اطلاعات و آماده‌سازی برنامه…", "Receiving data and preparing the app…"),
                    color = Color(0xFFB1DFFF), fontSize = if (compact) 10.sp else 13.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.align(Alignment.TopCenter).offset(y = maxHeight * .793f).fillMaxWidth().padding(horizontal = 12.dp)
                )
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
