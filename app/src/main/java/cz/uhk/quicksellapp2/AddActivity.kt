package cz.uhk.quicksellapp2

import android.app.Activity
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.media.Image
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.util.UUID

class AddActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private val PICK_IMAGE_REQUEST = 1

    private lateinit var storageReference : StorageReference
    private lateinit var database : FirebaseFirestore

    private lateinit var imageUriRef : String

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

        storageReference = Firebase.storage.reference
        database = Firebase.firestore

        imageView = findViewById(R.id.imageViewUserCustom)
        val btnSelectImage = findViewById<ImageButton>(R.id.buttonImageUpload)

        btnSelectImage.setOnClickListener {
            openGallery()
        }

        btnCancelDeal.setOnClickListener{
            val intent = Intent(this,MainDashboardActivity::class.java)
            startActivity(intent)
        }

        val db = Firebase.firestore

        val textDealName = findViewById<EditText>(R.id.textProductName)
        val textDealDescription = findViewById<EditText>(R.id.textDescription)
        val textPhoneNumber = findViewById<EditText>(R.id.textPhoneNumber)

        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val usernameString= sharedPreferences.getString("textUsername", "chyba")
        val latitude = sharedPreferences.getString("latitude","0")
        val longitude = sharedPreferences.getString("longitude","0")
        imageUriRef = ""


        btnUploadDeal.setOnClickListener{
            val intent = Intent(this,LoadActivity::class.java)
            if (textDealName.text.isNotEmpty() && textDealDescription.text.isNotEmpty() && textPhoneNumber.text.isNotEmpty() && imageUriRef.isNotEmpty()){
                db.collection("deals")
                    .whereEqualTo("dealName",textDealName.text.toString())
                    .get()
                    .addOnSuccessListener {result ->
                        if (!result.isEmpty)
                            Toast.makeText(this, "Název nabídky je již používán, zvolte jiný", Toast.LENGTH_LONG).show()
                        else{
                            val deal = hashMapOf(
                                "dealName" to textDealName.text.toString(),
                                "dealDescription" to textDealDescription.text.toString(),
                                "phoneNumber" to textPhoneNumber.text.toString(),
                                "ownerUser" to usernameString,
                                "latitude" to latitude,
                                "longitude" to longitude,
                                "imageUri" to imageUriRef
                            )

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
                    .addOnFailureListener {

                    }


            }
            else{
                Toast.makeText(this, "Vyplňte veškerá pole", Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            // Get the URI of the selected image
            val imageUri = data.data
            // Set the image in the ImageView
            imageView.setImageURI(imageUri)


            val filename = UUID.randomUUID().toString()
            val ref = storageReference.child("images/$filename")
            ref.putFile(imageUri!!)
                .addOnSuccessListener {
                    // Get the download URL of the uploaded image
                    ref.downloadUrl.addOnSuccessListener { uri ->
                        // Store the download URL in Firebase Realtime Database
                        imageUriRef = "images/$filename"
                        //databaseReference.reference.child("images").push().setValue(uri.toString())
                    }
                }
        }
    }


}