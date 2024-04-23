package cz.uhk.quicksellapp2

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.nfc.Tag
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class DealViewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_deal_view)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets

        }


        val textPName = findViewById<TextView>(R.id.textProductNameView)
        val textPDesc = findViewById<TextView>(R.id.textProductDescriptionView)
        val textPNum = findViewById<TextView>(R.id.textProductNumberView)


        var imageDownloadUri = ""

        var latitude = "37.7749"
        var longitude = "-122.4194"


        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val dealName = sharedPreferences.getString("lastOpenedDealName","bruh")
        val usernameString= sharedPreferences.getString("textUsername", "Nezaregistrovaný uživatel")
        Log.d(TAG,dealName.toString())
        val db = Firebase.firestore


        db.collection("deals")
            .whereEqualTo("dealName",dealName)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.d(ContentValues.TAG, "${document.id} => ${document.data}")
                    textPName.text = document.data.get("dealName").toString()
                    textPDesc.text = document.data.get("dealDescription").toString()
                    textPNum.text = document.data.get("phoneNumber").toString()
                    latitude = document.data.get("latitude").toString()
                    longitude = document.data.get("longitude").toString()

                    downloadImage(document.data.get("imageUri").toString())
                    /*
                    val storage = Firebase.storage.reference

                    storage.child(document.data.get("imageUri").toString()).downloadUrl.addOnSuccessListener {
                        Log.d(TAG,"SAJMIHOUKOLEN")
                    }.addOnFailureListener {
                        Log.d(TAG,"MUZUTOPRCAT")
                    }*/



                }

            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error getting documents.", exception)
            }

        val buttonCall = findViewById<ImageButton>(R.id.buttonCall)
        buttonCall.setOnClickListener {
            val phoneNumber = textPNum.text.toString()
            callPhoneNumber(phoneNumber)
        }

        val buttonMap = findViewById<ImageButton>(R.id.buttonOpenMap)

        buttonMap.setOnClickListener{
            val gmmIntentUri = Uri.parse("geo:$latitude,$longitude?q=$latitude,$longitude(Label)")

            // Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)

            // Make the Intent explicit by setting the Google Maps package
            mapIntent.setPackage("com.google.android.apps.maps")

            // Attempt to start an activity that can handle the Intent
            startActivity(mapIntent)
        }

        val buttonClose = findViewById<ImageButton>(R.id.buttonCloseDeal)
        buttonClose.setOnClickListener{
            val intent = Intent(this,LoadActivity::class.java)
            startActivity(intent)
        }

    }
    private fun callPhoneNumber(phoneNumber: String) {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$phoneNumber")
        }
        startActivity(intent)

    }

    private fun downloadImage(imageUrl: String) {
        GlobalScope.launch(Dispatchers.Main) {
            try {
                // Download image from Firebase Storage URL
                val bitmap = getBitmapFromUrl(imageUrl)
                // Display the downloaded image in ImageView
                val uImageView = findViewById<ImageView>(R.id.imageViewUserImage)
                uImageView.setImageBitmap(bitmap)
            } catch (e: Exception) {
                // Handle error
                e.printStackTrace()
            }
        }
    }

    private suspend fun getBitmapFromUrl(imageUrl: String): Bitmap? {
        return try {
            val url = URL(imageUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val inputStream: InputStream = connection.inputStream
            BitmapFactory.decodeStream(inputStream)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
}