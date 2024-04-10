package cz.uhk.quicksellapp2

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val btnRegister = findViewById<Button>(R.id.buttonRegister)
        val textUsername = findViewById<EditText>(R.id.textUsernameNew)

        val db = Firebase.firestore

        //TODO kontrola ze jmeno jeste neexistuje
        btnRegister.setOnClickListener{
            val intent = Intent(this,MainDashboardActivity::class.java)
            val editor = sharedPreferences.edit()
            editor.putString("textUsername",textUsername.text.toString())
            editor.putBoolean("registeredBool",true) //todo pak zmenit
            editor.apply()

            // creater a new user
            val user = hashMapOf(
                "username" to textUsername.text.toString(),
                //TODO cas
                //score
                //...
            )

            // Add a new document with a generated ID
            db.collection("users")
                .add(user)
                .addOnSuccessListener { documentReference ->
                    Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error adding document", e)
                }
            startActivity(intent)
        }
    }
}