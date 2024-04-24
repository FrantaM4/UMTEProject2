package cz.uhk.quicksellapp2

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {


    private lateinit var fusedLocationClient: FusedLocationProviderClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)


        //lokalni db -> porovnani lokalnich dat s cloudem
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val registeredBool = sharedPreferences.getBoolean("registeredBool", false)
        val usernameString= sharedPreferences.getString("textUsername", "Nezaregistrovaný uživatel")
        val textUsername = findViewById<TextView>(R.id.UsernameView)
        val btnLaunch = findViewById<Button>(R.id.buttonLaunch)
        val btnSettings = findViewById<Button>(R.id.buttonSettings)
        val db = Firebase.firestore


        //firebase db
        if (registeredBool){

            var usersFound = 0;
            db.collection("users")
                .whereEqualTo("username", usernameString)
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        Log.d(TAG, "${document.id} => ${document.data}")
                        usersFound+=1

                    }
                    if (usersFound == 1)
                        textUsername.text = usernameString
                    else{
                        textUsername.text = "Nezaregistrovaný uživatel"
                        val editor = sharedPreferences.edit()

                        editor.putString("textUsername","Nezaregistrovaný uživatel")
                        editor.putBoolean("registeredBool",false)
                        editor.apply()
                    }

                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents.", exception)
                }


        }

        //


        if (checkLocationPermissions()) {
            getLastLocation()
        } else {
            requestLocationPermissions()
        }


        textUsername.text = usernameString

        btnLaunch.setOnClickListener{
            if (!registeredBool){
                val intent = Intent(this,RegisterActivity::class.java)
                startActivity(intent)
            }
            else{
                val intent = Intent(this,LoadActivity::class.java)


                startActivity(intent)
            }


        }
        btnSettings.setOnClickListener{
            val intent = Intent(this,SettingsActivity::class.java)
            startActivity(intent)
        }

    }
    private fun checkLocationPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    // Use the user's last known location
                    val latitude = location.latitude
                    val longitude = location.longitude
                    Log.d(TAG,latitude.toString() + longitude.toString())
                    val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putString("latitude",latitude.toString())
                    editor.putString("longitude",longitude.toString())

                    editor.apply()

                    // Do something with latitude and longitude
                } else {
                    // Handle the case where the location is null
                    Toast.makeText(this, "Unable to retrieve location", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                // Handle any errors
                Toast.makeText(this, "Error getting location: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                getLastLocation()
            } else {
                Toast.makeText(
                    this,
                    "Location permissions denied",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }
}