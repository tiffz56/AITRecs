package hu.ait.aitrecs.data

import com.google.android.gms.maps.model.LatLng
import java.io.Serializable

data class Rec(
    var uid: String = "",
    var author: String = "",
    var locName: String = "",
    var lat: Double = 0.toDouble(),
    var lng: Double = 0.toDouble(),
    var description: String = "",
    var category: Int = 0
)
