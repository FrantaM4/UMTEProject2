package cz.uhk.quicksellapp2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MapActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_map_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        val btnMainMenu = findViewById<ImageButton>(R.id.buttonBackToMenu)
        btnMainMenu.setOnClickListener{
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)

        }


        val btnAddNew = findViewById<ImageButton>(R.id.buttonAddNew)
        btnAddNew.setOnClickListener{
            val intent = Intent(this,AddActivity::class.java)
            startActivity(intent)
        }


    }
}