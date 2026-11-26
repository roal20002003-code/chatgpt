package ir.spaceweather.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import ir.spaceweather.app.data.tr
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

@Composable
fun HistoricalFlares() {
    var stamp by remember { mutableStateOf(System.currentTimeMillis() / 600_000L) }
    LaunchedEffect(Unit) {
        while (isActive) {
            delay(60_000L)
            stamp = System.currentTimeMillis() / 600_000L
        }
    }
    Card {
        SectionTitle(tr("شراره‌های خورشیدی روزهای گذشته", "Solar flares in recent days"))
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(listOf(2, 4), listOf(6, 8)).forEach { days ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    days.forEach { day ->
                        Column(Modifier.weight(1f).clip(RoundedCornerShape(12.dp))
                            .background(C.deep).border(1.dp, C.line, RoundedCornerShape(12.dp)).padding(6.dp)) {
                            val title = tr("شراره‌های $day روز گذشته", "Flares · past $day days")
                            Text(title, color = C.text, fontSize = 12.sp, fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 6.dp))
                            SubcomposeAsyncImage(
                                model = "https://www.sidc.be/solardemon/overview.php?d=$day&b=2&w=300&fid=0&science=0&t=$stamp",
                                contentDescription = title,
                                contentScale = ContentScale.Fit,
                                modifier = Modifier.fillMaxWidth().aspectRatio(1f).clip(RoundedCornerShape(8.dp)).background(Color.Black),
                                loading = { Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(Modifier.size(24.dp), color = C.sun, strokeWidth = 2.dp) } },
                                error = { Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(tr("تصویر در دسترس نیست", "Image unavailable"), color = C.muted, fontSize = 11.sp) } }
                            )
                        }
                    }
                }
            }
        }
        Text(tr("منبع: SIDC Solar Demon", "Source: SIDC Solar Demon"), color = C.muted, fontSize = 11.sp, modifier = Modifier.padding(top = 8.dp))
    }
}
