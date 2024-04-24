package cz.uhk.quicksellapp2

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val userDeleteButton = findViewById<Button>(R.id.buttonResetName)
        val db = Firebase.firestore

        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val intent = Intent(this,MainActivity::class.java)
        userDeleteButton.setOnClickListener{

            val editor = sharedPreferences.edit()
            //TODO DELETE V DB VCETNE DEALU ve final verzi
            editor.clear()
            editor.putString("textUsername","Nezaregistrovaný uživatel")
            editor.putBoolean("registeredBool",false)
            editor.apply()



            startActivity(intent)

        }

        val darkmodeBtn = findViewById<Button>(R.id.buttonDarkmode)
        darkmodeBtn.setOnClickListener {
            toggleDarkMode()
            startActivity(intent)
        }

    }

    private fun toggleDarkMode() {
        val currentNightMode = resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK

        when (currentNightMode) {
            android.content.res.Configuration.UI_MODE_NIGHT_NO -> {
                // Switch to dark mode
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
            android.content.res.Configuration.UI_MODE_NIGHT_YES -> {
                // Switch to light mode
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            else -> {
                // Use the system default
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
        }
    }
}