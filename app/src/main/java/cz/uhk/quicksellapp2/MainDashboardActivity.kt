package cz.uhk.quicksellapp2

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL

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





        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)


        val dealCount = sharedPreferences.getInt("dealCount",0)

        val myDeals = mutableListOf<DealData>()


        for (i in 0 until dealCount){
            val dealName = sharedPreferences.getString("dealID${i}","idk")
            myDeals.add(DealData(dealName.toString()))
        }



        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView2)

        val recyclerView2 = findViewById<RecyclerView>(R.id.recyclerView3)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = TaskAdapter(myDeals)

        fetchData(sharedPreferences.getString("textUsername","").toString(),recyclerView2)

        recyclerView2.layoutManager= LinearLayoutManager(this)
        //recyclerView2.adapter = TaskAdapter(deals)



    }


    private fun fetchData(username: String, recyclerView: RecyclerView) {
        // Use lifecycleScope for coroutine in production
        GlobalScope.launch(Dispatchers.IO) {
            val result = getDataFromDB(username)
            updateUI(result, recyclerView)
        }
    }

    private suspend fun getDataFromDB(username: String): MutableList<DealData> {
        return try {
            val foreignDealsList = mutableListOf<DealData>()
            val db = Firebase.firestore
            db.collection("deals")
                .whereNotEqualTo("ownerUser", username)
                .get()
                .addOnSuccessListener { result ->

                    for (document in result) {
                        Log.d(ContentValues.TAG, "${document.id} => ${document.data}")
                        foreignDealsList.add(DealData(document.data.get("dealName").toString()))
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w(ContentValues.TAG, "Error getting documents.", exception)

                }
            delay(200)
            return foreignDealsList
        } finally {

        }
    }

    private suspend fun updateUI(deals : List<DealData>, recyclerView: RecyclerView) {
        withContext(Dispatchers.Main) {
            Log.d(TAG,deals.toString())
            recyclerView.adapter = TaskAdapter(deals)
        }
    }

}