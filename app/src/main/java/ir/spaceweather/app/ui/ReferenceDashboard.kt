package ir.spaceweather.app.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.spaceweather.app.R
import ir.spaceweather.app.data.*

private val ink = Color(0xFF06152E)
private val border = Color(0xFF3976B9)

/** Three independent image states; a soft scale and opacity animation keeps the assets moving. */
@Composable
private fun AnimatedArtwork(level: Int, sun: Boolean, modifier: Modifier = Modifier) {
    val images = if (sun) listOf(R.drawable.sw_sun_1, R.drawable.sw_sun_2, R.drawable.sw_sun_3)
                 else listOf(R.drawable.sw_aurora_1, R.drawable.sw_aurora_2, R.drawable.sw_aurora_3)
    val transition = rememberInfiniteTransition(label = if (sun) "sun-image" else "aurora-image")
    val pulse by transition.animateFloat(.985f, 1.055f, infiniteRepeatable(tween(if (sun) 4200 else 6800, easing = EaseInOutSine), RepeatMode.Reverse), label="scale")
    val fade by transition.animateFloat(.86f, 1f, infiniteRepeatable(tween(2600, easing = EaseInOutSine), RepeatMode.Reverse), label="light")
    Box(modifier.clip(RoundedCornerShape(14.dp))) {
        if (level in 1..3) {
            Image(painterResource(images[level - 1]), null, Modifier.fillMaxSize().scale(pulse).alpha(fade), contentScale=ContentScale.Crop)
        } else {
            Box(Modifier.fillMaxSize().background(ink), contentAlignment=Alignment.Center) {
                Text(tr("در انتظار داده", "Waiting for data"), color=C.muted)
            }
        }
    }
}

private fun kpLevel(kp: Double?): Int = when {
    kp == null -> 0
    kp >= 5.0 -> 3
    kp >= 3.0 -> 2
    else -> 1
}
private fun xrayLevel(xray: String?): Int = when (xray?.firstOrNull()?.uppercaseChar()) {
    'X', 'M' -> 3
    'C' -> 2
    'A', 'B' -> 1
    else -> 0
}

@Composable
private fun Metric(label: String, value: String, color: Color, modifier: Modifier=Modifier, unit: String="") {
    Column(modifier, horizontalAlignment=Alignment.CenterHorizontally, verticalArrangement=Arrangement.Center) {
        Text(label, color=Color.White, fontSize=12.sp, maxLines=1)
        Spacer(Modifier.height(4.dp))
        Text(value + unit, color=color, fontWeight=FontWeight.Bold, fontSize=20.sp, maxLines=1)
    }
}

@Composable
private fun Panel(modifier: Modifier=Modifier, content: @Composable ColumnScope.() -> Unit) {
    Column(modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp))
        .background(Brush.verticalGradient(listOf(Color(0xFF0B2851),Color(0xFF031127))))
        .border(1.dp,border,RoundedCornerShape(18.dp)).padding(12.dp), content=content)
}

/** Layout deliberately matches the approved Persian three-phone mockup. All values are real or —. */
@Composable
fun ReferenceDashboard(h: Hourly?, charts: Charts) {
    val kp = h?.kp ?: charts.last("kp")
    val xray = Status.xrayClassOf(h, charts)
    val sunLevel = xrayLevel(xray)
    val earthLevel = kpLevel(kp)
    val wind = h?.windSpeed ?: charts.last("wind")
    val density = charts.last("dens")
    val temp = charts.last("temp")
    Column(verticalArrangement=Arrangement.spacedBy(9.dp)) {
        Box(Modifier.fillMaxWidth().height(152.dp).clip(RoundedCornerShape(16.dp))) {
            AnimatedArtwork(sunLevel,true,Modifier.fillMaxSize())
            Box(Modifier.fillMaxSize().background(Brush.horizontalGradient(listOf(Color.Transparent, Color(0xD9000A21)))))
            Column(Modifier.align(Alignment.CenterEnd).padding(14.dp),horizontalAlignment=Alignment.End) {
                Text(tr("مرکز رصد فعالیت‌های خورشیدی", "Solar Activity Observation Center"),color=Color.White,fontWeight=FontWeight.Bold,fontSize=15.sp)
                Text("SpaceWeather.ir",color=C.sun,fontWeight=FontWeight.Bold,fontSize=14.sp)
                Spacer(Modifier.height(8.dp))
                Text("X-ray: ${xray ?: "—"}",color=Color.White,fontSize=12.sp)
            }
        }
        Panel {
            Text(tr("☀  وضعیت فعلی فعالیت خورشیدی", "☀  Current Solar Activity"),color=Color.White,fontWeight=FontWeight.Bold,fontSize=15.sp)
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceEvenly) {
                Metric("F10.7", h?.f107?.let { Fa.num(it,0) } ?: charts.last("f107")?.let { Fa.num(it,0) } ?: "—",Color(0xFF0ED5FF),Modifier.weight(1f))
                Metric("Kp", kp?.let { Fa.num(it,1) } ?: "—",Color(0xFF4BE34D),Modifier.weight(1f))
                Metric("Bz (nT)", (h?.bz ?: charts.last("bz"))?.let { Fa.num(it,1) } ?: "—",Color(0xFF20CFFF),Modifier.weight(1f))
                Metric("Bt (nT)", (h?.bt ?: charts.last("bt"))?.let { Fa.num(it,1) } ?: "—",Color(0xFFDD51FF),Modifier.weight(1f))
            }
        }
        Panel {
            Text(tr("≋  باد خورشیدی (نزدیک زمین)", "≋  Solar wind (near Earth)"),color=C.sky,fontWeight=FontWeight.Bold,fontSize=15.sp)
            Spacer(Modifier.height(7.dp))
            Row(Modifier.fillMaxWidth()) {
                Metric(tr("سرعت", "Speed"),wind?.let { Fa.num(it,0) } ?: "—",Color(0xFFFF5555),Modifier.weight(1f)," km/s")
                Metric(tr("چگالی", "Density"),density?.let { Fa.num(it,1) } ?: "—",C.sky,Modifier.weight(1f))
                Metric(tr("دما", "Temp"),temp?.let { Fa.num(it,0) } ?: "—",C.sun,Modifier.weight(1f))
            }
        }
        Panel {
            Text(tr("🧲  وضعیت ژئومغناطیسی", "🧲  Geomagnetic conditions"),color=Color.White,fontWeight=FontWeight.Bold,fontSize=15.sp)
            Spacer(Modifier.height(7.dp))
            Row(verticalAlignment=Alignment.CenterVertically) {
                Metric("Dst",(h?.dst ?: charts.last("dst"))?.let { Fa.num(it,0) } ?: "—",C.ok,Modifier.weight(1f)," nT")
                Column(Modifier.weight(1f),horizontalAlignment=Alignment.CenterHorizontally) {
                    Text(tr("زمین · Kp", "Earth · Kp"),color=C.muted,fontSize=12.sp)
                    Text(when(earthLevel){1->tr("آرام", "Quiet");2->tr("فعال", "Active");3->tr("طوفانی", "Storm");else->"—"},color=if(earthLevel==3) C.storm else C.ok,fontWeight=FontWeight.Bold,fontSize=18.sp)
                }
            }
        }
        Box(Modifier.fillMaxWidth().height(112.dp)) {
            AnimatedArtwork(earthLevel,false,Modifier.fillMaxSize())
            Text(tr("شفق قطبی · Kp ${kp?.let { Fa.num(it,1) } ?: "—"}","Aurora · Kp ${kp?.let { Fa.num(it,1) } ?: "—"}"),
                color=Color.White,fontWeight=FontWeight.Bold,fontSize=13.sp,
                modifier=Modifier.align(Alignment.BottomEnd).background(Color(0x99000A21)).padding(6.dp))
        }
        Text(tr("تصاویر نمایش تقریبی شرایط هستند؛ شدت واقعی شفق به موقعیت و شرایط محلی بستگی دارد.",
            "Illustrations are indicative; local aurora visibility varies."),color=C.muted,fontSize=10.sp,textAlign=TextAlign.Center,modifier=Modifier.fillMaxWidth())
    }
}
