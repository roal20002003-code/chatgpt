package ir.spaceweather.app.data

import java.util.Locale

data class LatestFlare(
    val id: Long, val count: Int, val miladi: String, val shamsi: String,
    val flareClass: String, val spot: String, val lat: String, val lot: String,
    val start: String, val peak: String, val end: String
) {
    val overview get() = "https://www.sidc.be/solardemon/overview.php?d=0&b=20000&w=350&fid=$id&science=0"
    fun frame(n: Int) = "https://www.sidc.be/solardemon/detections/qlk/094/flares/$id/cl_${String.format(Locale.US, "%04d", n)}.jpg"
}
