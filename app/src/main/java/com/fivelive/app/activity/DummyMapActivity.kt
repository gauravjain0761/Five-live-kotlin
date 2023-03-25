package com.fivelive.app.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.fivelive.app.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


class DummyMapActivity : AppCompatActivity(), OnMapReadyCallback {
    private var mapView: View? = null
    var mGoogleMap: GoogleMap? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dummy_map)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
        mapView = mapFragment.view
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mGoogleMap = googleMap
        val sydney = LatLng(28.583911, 77.319116)
        mGoogleMap!!.addMarker(MarkerOptions().position(sydney).title("Marker in Malagarh"))
        mGoogleMap!!.moveCamera(CameraUpdateFactory.newLatLng(sydney))
        mGoogleMap!!.animateCamera(CameraUpdateFactory.zoomTo(14f), 2000, null)
    }
}