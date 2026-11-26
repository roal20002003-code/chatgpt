package ir.spaceweather.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.imageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import ir.spaceweather.app.data.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import java.util.Locale

@Composable
fun LatestFlareCard(f: LatestFlare?, failed: Boolean) {
    Card {
        SectionTitle(tr("آخرین شراره ثبت‌شده", "Latest recorded flare"))
        if (f == null) {
            Text(if (failed) tr("دریافت گزارش ناموفق بود", "Unable to load flare report") else tr("در حال دریافت گزارش…", "Loading flare report…"), color = C.muted)
        } else {
            val cls = f.flareClass.uppercase(Locale.ROOT)
            Text(tr(
                "${f.shamsi} (${f.miladi}) — شراره شماره ${f.id} در کلاس $cls از ناحیه فعال شماره ${f.spot} در موقعیت Lat ${f.lat} و Lot ${f.lot} مشاهده شد. این شراره در ساعت ${f.start} آغاز شد، در ساعت ${f.peak} به اوج رسید و در ساعت ${f.end} پایان یافت.",
                "${f.miladi} (${f.shamsi}) — Flare ${f.id}, class $cls, from active region ${f.spot}, Lat ${f.lat}, Lot ${f.lot}. Start: ${f.start}, peak: ${f.peak}, end: ${f.end}."
            ), color = C.text, fontSize = 14.sp, lineHeight = 24.sp)
            if (failed) Text(tr("آخرین گزارش ذخیره‌شده در برنامه", "Last successfully loaded report"), color = C.muted, fontSize = 11.sp)
            Spacer(Modifier.height(12.dp))
            val ctx = LocalContext.current
            var frame by remember(f.id, f.count) { mutableStateOf<android.graphics.drawable.Drawable?>(null) }
            LaunchedEffect(f.id, f.count) {
                if (f.count > 0) {
                    var n = 1
                    while (isActive) {
                        val result = ctx.imageLoader.execute(ImageRequest.Builder(ctx).data(f.frame(n)).size(480).build())
                        if (result is SuccessResult) frame = result.drawable
                        delay(1000L)
                        n = if (n >= f.count) 1 else n + 1
                    }
                }
            }
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AsyncImage(model = frame, contentDescription = tr("تصاویر متحرک شراره", "Flare sequence"), contentScale = ContentScale.Fit,
                        modifier = Modifier.weight(1f).aspectRatio(1f).clip(RoundedCornerShape(10.dp)).background(C.deep))
                    AsyncImage(model = f.overview, contentDescription = tr("موقعیت شراره روی خورشید", "Flare location on Sun"), contentScale = ContentScale.Fit,
                        modifier = Modifier.weight(1f).aspectRatio(1f).clip(RoundedCornerShape(10.dp)).background(C.deep))
                }
            }
            if (f.count == 0) Text(tr("تصویر متحرک موجود نیست", "No sequence available"), color = C.muted, fontSize = 12.sp)
        }
    }
}
