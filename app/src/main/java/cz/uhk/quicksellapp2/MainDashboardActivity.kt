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
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class MainDashboardActivity : AppCompatActivity() {


    private lateinit var listMyDeals : List<DealData>
    private lateinit var listForeignDeals : List<DealData>
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

        val btnViewAllForeignDeal = findViewById<ImageButton>(R.id.buttonOpenAllForeignDeals)
        btnViewAllForeignDeal.setOnClickListener{
            val intent = Intent(this,ViewAllActivity::class.java)
            intent.putExtra("listDeals",ArrayList( listForeignDeals))
            startActivity(intent)
        }

        val btnViewAllMyDeals = findViewById<ImageButton>(R.id.buttonOpenAllMyDeals)
        btnViewAllMyDeals.setOnClickListener {
            val intent = Intent(this,ViewAllActivity::class.java)
            intent.putExtra("listDeals",ArrayList( listMyDeals))
            startActivity(intent)
        }




        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)



        val dealCount = sharedPreferences.getInt("dealCount",0)

        val myDeals = mutableListOf<DealData>()


        for (i in 0 until dealCount){
            val dealName = sharedPreferences.getString("dealID${i}","idk")
            myDeals.add(DealData(dealName.toString(),false,0.0)) //TODO Mozna predeleat
        }



        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView2)

        val recyclerView2 = findViewById<RecyclerView>(R.id.recyclerView3)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = DealAdapter(myDeals)

        fetchData(sharedPreferences.getString("textUsername","").toString(),recyclerView2)

        recyclerView2.layoutManager= LinearLayoutManager(this)
        //recyclerView2.adapter = TaskAdapter(deals)


        listMyDeals = myDeals




    }


    private fun fetchData(username: String, recyclerView: RecyclerView) {
        // Use lifecycleScope for coroutine in production
        GlobalScope.launch(Dispatchers.IO) {
            val result = getDataFromDB(username)
            val sortedList = result.sortedBy{ it.distance } //TODO nejak predelat asi

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
                        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                        //Log.d(ContentValues.TAG, "${document.id} => ${document.data}")
                        val latitudeDeal = document.data.get("latitude").toString()
                        val longitudeDeal = document.data.get("longitude").toString()
                        val latitudeCurrent = sharedPreferences.getString("latitude","").toString()
                        val longitudeCurrent = sharedPreferences.getString("longitude","").toString()
                        val distance = calculateDistance(latitudeDeal.toDouble(),longitudeDeal.toDouble(),latitudeCurrent.toDouble(),longitudeCurrent.toDouble())
                        foreignDealsList.add(DealData(document.data.get("dealName").toString(),true,distance))
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w(ContentValues.TAG, "Error getting documents.", exception)

                }
            delay(600)
            return foreignDealsList
        } finally {

        }
    }

    private suspend fun updateUI(deals : List<DealData>, recyclerView: RecyclerView) {
        withContext(Dispatchers.Main) {
            Log.d(TAG,deals.toString())
            recyclerView.adapter = DealAdapter(deals)
            listForeignDeals = deals
        }
    }



    fun calculateDistance(
        lat1: Double, lon1: Double, // Latitude and longitude of point 1
        lat2: Double, lon2: Double  // Latitude and longitude of point 2
    ): Double {
        val R = 6371.0 // Radius of the Earth in kilometers

        val latDistance = Math.toRadians(lat2 - lat1)
        val lonDistance = Math.toRadians(lon2 - lon1)
        val a = sin(latDistance / 2) * sin(latDistance / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(lonDistance / 2) * sin(lonDistance / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        val distance = R * c

        return distance // Distance in kilometers
    }


}