package cz.uhk.quicksellapp2

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.nfc.Tag
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

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
                }

            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error getting documents.", exception)
            }


        // TODO predelat
        //textPName.setText(dealName)


    }
}