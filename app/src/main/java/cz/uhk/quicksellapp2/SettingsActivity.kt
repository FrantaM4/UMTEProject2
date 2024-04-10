package cz.uhk.quicksellapp2

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

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

        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        //todo pak zmenit - pridat delete v db
        userDeleteButton.setOnClickListener{
            val intent = Intent(this,MainActivity::class.java)
            val editor = sharedPreferences.edit()
            editor.putString("textUsername","Nezaregistrovaný uživatel")
            editor.putBoolean("registeredBool",false)
            editor.apply()
            startActivity(intent)

        }

        //TODO Darkmode ???
    }
}