package cz.uhk.quicksellapp2

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class LoadActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_load)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val db = Firebase.firestore
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val usernameString= sharedPreferences.getString("textUsername", "Nezaregistrovaný uživatel")

        db.collection("deals")
            .whereEqualTo("ownerUser", usernameString)
            .get()
            .addOnSuccessListener { result ->
                var i = 0;
                val editor = sharedPreferences.edit()


                for (document in result) {
                    Log.d(ContentValues.TAG, "${document.id} => ${document.data}")
                    editor.putString("dealID${i}",document.data.get("dealName").toString())
                    i++
                    editor.putInt("dealCount",i)
                }
                editor.apply()
                startActivity(Intent(this,MainDashboardActivity::class.java))
            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error getting documents.", exception)
                startActivity(Intent(this,MainActivity::class.java))
            }

    }
}