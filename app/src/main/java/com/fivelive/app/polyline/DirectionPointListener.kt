package com.fivelive.app.polyline

import com.google.android.gms.maps.model.PolylineOptions

interface DirectionPointListener {
    fun onPath(polyLine: PolylineOptions?)
}