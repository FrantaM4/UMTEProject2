package cz.uhk.quicksellapp2

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        //lokalni db -> porovnani lokalnich dat s cloudem
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val registeredBool = sharedPreferences.getBoolean("registeredBool", false)
        val usernameString= sharedPreferences.getString("textUsername", "Nezaregistrovaný uživatel")
        val textUsername = findViewById<TextView>(R.id.UsernameView)
        val btnLaunch = findViewById<Button>(R.id.buttonLaunch)
        val btnSettings = findViewById<Button>(R.id.buttonSettings)


        /*//TODO IDK NEJAK TO SPRAVIT
        var syncCheck = false;

        //firebase db
        if (registeredBool){
            val db = Firebase.firestore
            var usersFound = 0;
            db.collection("users")
                .whereEqualTo("username", usernameString)
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        Log.d(TAG, "${document.id} => ${document.data}")
                        usersFound+=1
                    }

                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents.", exception)
                }


            Log.i(TAG,usersFound.toString())
            if (usersFound == 1)
                syncCheck = true
        }

        if (syncCheck){
            textUsername.text = usernameString
        }else{
            textUsername.text = "Nezaregistrovaný uživatel"
            val editor = sharedPreferences.edit()
            editor.putString("textUsername","Nezaregistrovaný uživatel")
            editor.putBoolean("registeredBool",false)
            editor.apply()
        }
        */


        textUsername.text = usernameString

        btnLaunch.setOnClickListener{
            if (!registeredBool){
                val intent = Intent(this,RegisterActivity::class.java)
                startActivity(intent)
            }
            else{
                val intent = Intent(this,MapActivity::class.java)
                startActivity(intent)
            }


        }
        btnSettings.setOnClickListener{
            val intent = Intent(this,SettingsActivity::class.java)
            startActivity(intent)
        }

    }
}