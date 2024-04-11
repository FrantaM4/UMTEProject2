package cz.uhk.quicksellapp2

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AddActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnUploadDeal= findViewById<ImageButton>(R.id.buttonSaveDeal)
        val btnCancelDeal= findViewById<ImageButton>(R.id.buttonCancelDeal)

        btnCancelDeal.setOnClickListener{
            val intent = Intent(this,MainDashboardActivity::class.java)
            startActivity(intent)
        }

        val db = Firebase.firestore

        val textDealName = findViewById<EditText>(R.id.textProductName)
        val textDealDescription = findViewById<EditText>(R.id.textDescription)
        val textTag = findViewById<EditText>(R.id.textTags)
        val textPhoneNumber = findViewById<EditText>(R.id.textPhoneNumber)

        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val usernameString= sharedPreferences.getString("textUsername", "chyba")


        btnUploadDeal.setOnClickListener{
            val intent = Intent(this,LoadActivity::class.java)

            val deal = hashMapOf(
                //"username" to textUsername.text.toString(),
                "dealName" to textDealName.text.toString(),
                "dealDescription" to textDealDescription.text.toString(),
                "tag" to textTag.text.toString(),
                "phoneNumber" to textPhoneNumber.text.toString(),
                "ownerUser" to usernameString

            )

            // Add a new document with a generated ID
            db.collection("deals")
                .add(deal)
                .addOnSuccessListener { documentReference ->
                    Log.d(ContentValues.TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                }
                .addOnFailureListener { e ->
                    Log.w(ContentValues.TAG, "Error adding document", e)
                }
            startActivity(intent)
        }
    }




}