package com.example.android_project_2

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PlacesFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PlaceAdapter
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_places, container, false)
        recyclerView = view.findViewById(R.id.placesView)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = PlaceAdapter()
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Request location permissions here if not granted
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                fetchNearbyPlaces(location)
            }
        }


    }

    private fun fetchNearbyPlaces(currentLocation: Location) {
        val apiKey = "AIzaSyAYCtReoL412FuOaTALp_0rybEqRTvG-Cw"
        val radius = 5000 // Radius in meters
        val locationStr = "${currentLocation.latitude},${currentLocation.longitude}"

        val apiUrl = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?" +
                "location=$locationStr&radius=$radius&key=$apiKey"

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = fetchUrl(apiUrl)
                withContext(Dispatchers.Main) {
                    parseNearbyPlacesResponse(response)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }

    private fun fetchUrl(url: String): String {
        val connection = URL(url).openConnection() as HttpURLConnection
        val inputStream = connection.inputStream
        val bufferedReader = BufferedReader(InputStreamReader(inputStream))
        val response = StringBuilder()

        bufferedReader.useLines { lines ->
            lines.forEach {
                response.append(it)
            }
        }

        return response.toString()
    }

    private fun parseNearbyPlacesResponse(response: String) {


        try {
            val jsonObject = JSONObject(response)
            if (jsonObject.has("results")) {
                val resultsArray: JSONArray = jsonObject.getJSONArray("results")
                val placesList = mutableListOf<Place>()



                for (i in 0 until resultsArray.length()) {
                    val placeObject = resultsArray.getJSONObject(i)
                    val name = placeObject.getString("name")
                    val vicinity = placeObject.getString("vicinity")
                    val locationObject = placeObject.getJSONObject("geometry").getJSONObject("location")
                    val latitude = locationObject.getDouble("lat")
                    val longitude = locationObject.getDouble("lng")

                    val place = Place(name, vicinity, LatLng(latitude, longitude))
                    placesList.add(place)
                }

                Log.d("Response data", placesList.toString());
                // Update adapter with nearby places
                adapter.updatePlaces(placesList)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    companion object {

    }

    data class Place(
        val name: String,
        val vicinity: String,
        val location: LatLng
    )
}