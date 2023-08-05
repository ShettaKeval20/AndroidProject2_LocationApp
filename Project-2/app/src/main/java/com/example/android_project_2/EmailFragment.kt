package com.example.android_project_2

import android.Manifest
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import android.location.Location
import java.io.IOException
import java.util.*
import android.content.Context
import android.content.SharedPreferences

class EmailFragment : Fragment() {
    private lateinit var editTextRecipient: EditText
    private lateinit var sendButton: Button
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var sharedPreferences: SharedPreferences

    companion object {
        const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_email, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        editTextRecipient = view.findViewById(R.id.emailText)
        sendButton = view.findViewById(R.id.sendButton)

        // Set click listener for the "Send Email" button
        sendButton.setOnClickListener { sendEmailWithCurrentLocation() }
        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())

        // Initialize SharedPreferences
        sharedPreferences = requireContext().getSharedPreferences(
            "MyPrefs",
            Context.MODE_PRIVATE
        )

        // Retrieve the last used email address and prepopulate the EditText
        val lastUsedEmail = sharedPreferences.getString("email", "")
        editTextRecipient.setText(lastUsedEmail)

        //Set lat and long on fragment load
        getCurrentLocationCoordinates()
    }

    private fun sendEmailWithCurrentLocation() {
        val recipient = editTextRecipient.text.toString().trim()

        // Check if the recipient's email address is valid
        if (recipient.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(recipient).matches()) {
            Toast.makeText(context, "Please enter a valid email address.", Toast.LENGTH_SHORT).show()
            return
        }

        //Save email for data persistence
        val editor = sharedPreferences.edit()
        editor.putString("email", recipient)
        editor.apply()


        // Obtain the address from latitude and longitude using reverse geocoding
        val address = getAddressFromLocation(latitude, longitude)

        // Compose the email content with the location information
        val emailContent = "Hello,\n\nI am currently at the following location:\n\n" +
                "Latitude: $latitude\nLongitude: $longitude\nAddress: $address\n\n" +
                "Best Regards,\n Aakash"

        // Create the email intent
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(recipient))
        intent.putExtra(Intent.EXTRA_SUBJECT, "Current Location")
        intent.putExtra(Intent.EXTRA_TEXT, emailContent)

        // Start the email intent
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(Intent.createChooser(intent, "Send Email"))
        } else {
            Toast.makeText(context, "No email app found on your device.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getCurrentLocationCoordinates() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    location?.let {
                        latitude = location.latitude
                        longitude = location.longitude
                    }
                }
                .addOnFailureListener { exception: Exception ->
                    // Handle location retrieval failure, if any
                    // For example, show an error message to the user
                    val errorMessage = "Error getting location: ${exception.localizedMessage}"
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                }
        } else {
            // Request location permission if not granted
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                MapsFragment.LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun getAddressFromLocation(latitude: Double, longitude: Double): String {

        val geocoder = Geocoder(requireContext(), Locale.getDefault())

        try {
            val addresses: List<Address> =
                geocoder.getFromLocation(latitude, longitude, 1) as List<Address>
            if (addresses != null && addresses.isNotEmpty()) {
                val address: Address = addresses[0]
                val stringBuilder = StringBuilder()
                for (i in 0..address.maxAddressLineIndex) {
                    stringBuilder.append(address.getAddressLine(i)).append(", ")
                }
                return stringBuilder.toString()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return ""

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == MapsFragment.LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // If location permission is granted, get current location and add marker
                getCurrentLocationCoordinates()
            }
        }
    }
}