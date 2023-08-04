package com.example.android_project_2

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.example.android_project_2.R
import java.io.IOException

class MapsFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val markerTitle = "Your Location"
    private var lastKnownMarker: Marker? = null

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // When map is ready and location permission is granted, get current location and add marker
            getCurrentLocationAndAddMarker()
        } else {
            // Request location permission if not granted
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }

        mMap.setOnMarkerClickListener { marker ->
            showMarkerInfo(marker)
            true
        }
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocationAndAddMarker() {
        mMap.isMyLocationEnabled = true

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location?.let {
                val currentLatLng = LatLng(it.latitude, it.longitude)
                lastKnownMarker?.remove() // Remove the previous marker (if any)
                lastKnownMarker = mMap.addMarker(
                    MarkerOptions()
                        .position(currentLatLng)
                        .title(markerTitle)
                )
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
            }
        }
    }

    private fun showMarkerInfo(marker: Marker) {
        val geocoder = Geocoder(requireContext())
        val latLng = marker.position
        var addressText = "Unknown Address"

        try {
            val addresses: List<Address> =
                geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1) as List<Address>
            if (addresses.isNotEmpty()) {
                addressText = addresses[0].getAddressLine(0)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        marker.title = markerTitle
        marker.snippet = "Lat:${latLng.latitude}, Long:${latLng.longitude}, Address: $addressText"
        marker.showInfoWindow()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // If location permission is granted, get current location and add marker
                getCurrentLocationAndAddMarker()
            }
        }
    }
}

