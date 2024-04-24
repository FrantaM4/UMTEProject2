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
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.Debug
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
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

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

        val userImageView = findViewById<ImageView>(R.id.imageViewUserImage)


        val storageRef = Firebase.storage.reference

        var latitude = "37.7749"
        var longitude = "-122.4194"


        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val dealName = sharedPreferences.getString("lastOpenedDealName","bruh")
        val usernameString= sharedPreferences.getString("textUsername", "Nezaregistrovaný uživatel").toString()
        Log.d(TAG,dealName.toString())
        val db = Firebase.firestore
        var userOwner = ""
        var documentID = ""

        val buttonDeleteDeal = findViewById<Button>(R.id.buttonDeleteDeal)


        db.collection("deals")
            .whereEqualTo("dealName",dealName)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.d(ContentValues.TAG, "${document.id} => ${document.data}")
                    documentID = document.id
                    textPName.text = document.data.get("dealName").toString()
                    textPDesc.text = document.data.get("dealDescription").toString()
                    textPNum.text = "+420 ${document.data.get("phoneNumber").toString()}"
                    latitude = document.data.get("latitude").toString()
                    longitude = document.data.get("longitude").toString()
                    userOwner = document.data.get("ownerUser").toString()
                    if (usernameString != userOwner)
                        buttonDeleteDeal.visibility = View.INVISIBLE

                    storageRef.child(document.data.get("imageUri").toString()).getBytes(Long.MAX_VALUE).addOnSuccessListener { result ->
                        val bitmap = BitmapFactory.decodeByteArray(result, 0, result.size)
                        userImageView.setImageBitmap(bitmap)
                    }.addOnFailureListener {
                        // Handle any errors
                    }



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

            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)

            mapIntent.setPackage("com.google.android.apps.maps")

            startActivity(mapIntent)
        }

        val buttonClose = findViewById<ImageButton>(R.id.buttonCloseDeal)
        buttonClose.setOnClickListener{
            val intent = Intent(this,LoadActivity::class.java)
            startActivity(intent)
        }




        buttonDeleteDeal.setOnClickListener{
            val intent = Intent(this,LoadActivity::class.java)
            db.collection("deals").document(documentID).delete()
                .addOnSuccessListener {
                    startActivity(intent)
                    val editor = sharedPreferences.edit()
                    for (i in 0 until sharedPreferences.getInt("dealCount",0)) {
                        editor.remove("dealID${i}")
                    }
                    editor.remove("dealCount")
                    editor.apply()
                }
                .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e) }

        }



    }
    private fun callPhoneNumber(phoneNumber: String) {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$phoneNumber")
        }
        startActivity(intent)

    }

}