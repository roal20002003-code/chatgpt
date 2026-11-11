package ir.spaceweather.app.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.spaceweather.app.data.*
import kotlin.math.*

/** Live visualisation: X-ray and Kp are independent inputs; no fabricated readings. */
@Composable
fun LiveSpaceScene(h: Hourly?, charts: Charts) {
    val cls = Status.xrayClassOf(h, charts)
    val sunLevel = when (cls?.firstOrNull()?.uppercaseChar()) {
        'X', 'M' -> 3
        'C' -> 2
        'A', 'B' -> 1
        else -> 0
    }
    val kp = h?.kp ?: charts.last("kp")
    val earthLevel = when {
        kp == null -> 0
        kp >= 5 -> 3
        kp >= 3 -> 2
        else -> 1
    }
    val transition = rememberInfiniteTransition(label = "live-space")
    val phase by transition.animateFloat(0f, 1f, infiniteRepeatable(tween(6500, easing = LinearEasing)), label = "orbit")
    val pulse by transition.animateFloat(0f, 1f, infiniteRepeatable(tween(1900), RepeatMode.Reverse), label = "flare")
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Box(Modifier.fillMaxWidth().border(1.dp, C.line, RoundedCornerShape(18.dp))
            .background(C.night, RoundedCornerShape(18.dp))) {
            Column {
                Canvas(Modifier.fillMaxWidth().height(170.dp)) {
                    val r = size.height * .51f
                    val c = Offset(size.width * .19f, size.height * .57f)
                    drawRect(Brush.verticalGradient(listOf(Color(0xFF03091C), Color(0xFF101D41))))
                    for (i in 0..50) {
                        val x = ((i * 97) % 101) / 101f * size.width
                        val y = ((i * 41) % 97) / 97f * size.height
                        drawCircle(Color.White.copy(alpha = .12f + (i % 4) * .11f), 1.2f, Offset(x, y))
                    }
                    val glow = if (sunLevel == 3) Color(0xFFFF4414) else Color(0xFFFFA500)
                    drawCircle(Brush.radialGradient(listOf(glow.copy(alpha = .55f + pulse * .15f), Color.Transparent), c, r * 1.75f), r * 1.75f, c)
                    drawCircle(Brush.radialGradient(listOf(Color(0xFFFFE67A), Color(0xFFFF8B0A), Color(0xFFAC2607)), c, r), r, c)
                    for (i in 0 until (sunLevel * 4)) {
                        val a = i * 2 * PI / 12 + phase * 2 * PI
                        val start = Offset(c.x + r * cos(a).toFloat(), c.y + r * sin(a).toFloat())
                        val end = Offset(c.x + r * cos(a + .55).toFloat(), c.y + r * sin(a + .55).toFloat())
                        val mid = Offset(c.x + r * (1.35f + pulse * .28f) * cos(a + .275).toFloat(), c.y + r * (1.35f + pulse * .28f) * sin(a + .275).toFloat())
                        val p = Path().apply { moveTo(start.x, start.y); quadraticBezierTo(mid.x, mid.y, end.x, end.y) }
                        drawPath(p, Color(0xFFFFD05A).copy(alpha = .8f), style = Stroke(2.8f))
                    }
                }
                Row(Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("☀  ${tr("فعالیت خورشید", "Solar activity")}", color = C.sun, fontWeight = FontWeight.Bold)
                    Text("X-ray: ${cls ?: "—"}", color = C.text)
                }
            }
        }
        Box(Modifier.fillMaxWidth().border(1.dp, C.line, RoundedCornerShape(18.dp))
            .background(C.night, RoundedCornerShape(18.dp))) {
            Column {
                Canvas(Modifier.fillMaxWidth().height(115.dp)) {
                    val c = Offset(size.width * .5f, size.height * .53f)
                    val r = size.height * .33f
                    drawRect(Brush.horizontalGradient(listOf(Color(0xFF06152C), Color(0xFF071C3A))))
                    for (i in 1..5) {
                        val w = r * (1.5f + i * .42f + .05f * sin(phase * 2 * PI).toFloat())
                        val p = Path().apply {
                            moveTo(c.x - w, c.y)
                            cubicTo(c.x - w, c.y - r * (1.5f + i * .2f), c.x + w, c.y - r * (1.5f + i * .2f), c.x + w, c.y)
                        }
                        drawPath(p, Color(0xFF26A8FF).copy(alpha = .25f + earthLevel * .13f), style = Stroke(1.6f))
                    }
                    drawCircle(Brush.radialGradient(listOf(Color(0xFF60D9FF), Color(0xFF1269BA), Color(0xFF061B48)), c, r), r, c)
                    drawCircle(Color(0xFF4DE2FF).copy(alpha = .25f + pulse * .2f), r * 1.16f, c, style = Stroke(2f))
                }
                Row(Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("🌍  ${tr("میدان مغناطیسی زمین", "Earth magnetic field")}", color = C.sky, fontWeight = FontWeight.Bold)
                    Text("Kp: ${kp?.let { Fa.num(it, 1) } ?: "—"}", color = C.text)
                }
            }
        }
        Box(Modifier.fillMaxWidth().border(1.dp, C.line, RoundedCornerShape(18.dp))
            .background(C.night, RoundedCornerShape(18.dp))) {
            Column {
                Canvas(Modifier.fillMaxWidth().height(140.dp)) {
                    drawRect(Brush.verticalGradient(listOf(Color(0xFF02081C), Color(0xFF0B2251))))
                    val intensity = if (earthLevel == 0) .08f else (.15f + earthLevel * .19f)
                    for (i in 0..9) {
                        val x = i * size.width / 9f
                        val p = Path().apply {
                            moveTo(x - size.width * .15f, size.height)
                            cubicTo(x - size.width * .10f, size.height * (.32f + .15f * sin(phase * 2 * PI + i).toFloat()),
                                x + size.width * .08f, size.height * (.30f + .13f * cos(phase * 2 * PI + i).toFloat()),
                                x + size.width * .15f, size.height)
                        }
                        drawPath(p, if (i % 3 == 0) Color(0xFFAC5DFF).copy(alpha = intensity) else Color(0xFF33FFAD).copy(alpha = intensity), style = Stroke(size.width * .085f))
                    }
                    drawRect(Brush.verticalGradient(listOf(Color.Transparent, Color(0xFF05142C))), topLeft = Offset(0f, size.height * .77f))
                }
                Row(Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("✦  ${tr("شفق قطبی", "Aurora")}", color = C.ok, fontWeight = FontWeight.Bold)
                    Text(if (earthLevel == 0) "—" else when (earthLevel) { 3 -> tr("گسترده‌تر", "Enhanced"); 2 -> tr("فعال", "Active"); else -> tr("ضعیف", "Weak") }, color = C.text, fontSize = 13.sp)
                }
            }
        }
        Text(tr("نمایش تقریبی بر اساس آخرین داده X-ray و Kp؛ پیش‌بینی محلی شفق نیست.", "Illustrative animation based on X-ray and Kp; not a local aurora forecast."), color = C.muted, fontSize = 11.sp)
    }
}
