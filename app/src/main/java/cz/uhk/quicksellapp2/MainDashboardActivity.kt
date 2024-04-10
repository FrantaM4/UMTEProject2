package cz.uhk.quicksellapp2

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainDashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dashboard_main)
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

        val recyclerview = findViewById<RecyclerView>(R.id.LIstMyDeals)
        recyclerview.layoutManager = LinearLayoutManager(this)
        val data = ArrayList<ItemsViewModel>()

        // This loop will create 20 Views containing
        // the image with the count of view
        for (i in 1..20) {
            data.add(ItemsViewModel("Item " + i))
        }

        // This will pass the ArrayList to our Adapter
        val adapter = CustomAdapter(data)

        // Setting the Adapter with the recyclerview
        recyclerview.adapter = adapter


        //TODO zprovoznit list


    }
}