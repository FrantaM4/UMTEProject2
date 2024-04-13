package cz.uhk.quicksellapp2

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment
import kotlin.math.log

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


        /*
        val recyclerview = findViewById<RecyclerView>(R.id.LIstMyDeals)
        recyclerview.layoutManager = LinearLayoutManager(this)*/
        val data = ArrayList<ItemsViewModel>()


        val db = Firebase.firestore
        //data
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        //val usernameString= sharedPreferences.getString("textUsername", "chyba")
        //val dealNames = mutableListOf<String>()


        //TODO Hnusookod predelat
        val myDealTxtField1 = findViewById<TextView>(R.id.dealName1)
        myDealTxtField1.setText(sharedPreferences.getString("dealID${0}","asd").toString())
        val myDealTxtField2 = findViewById<TextView>(R.id.dealName2)
        myDealTxtField2.setText(sharedPreferences.getString("dealID${1}","asd").toString())

        findViewById<ImageButton>(R.id.openMyDeal1).setOnClickListener{
                val intent = Intent(this, DealViewActivity::class.java)

                val editor = sharedPreferences.edit()
                editor.putString("lastOpenedDealName", myDealTxtField1.text.toString())
                editor.apply()
                startActivity(intent)

        }

        findViewById<ImageButton>(R.id.openMyDeal2).setOnClickListener{
            val intent = Intent(this, DealViewActivity::class.java)

            val editor = sharedPreferences.edit()
            editor.putString("lastOpenedDealName", myDealTxtField2.text.toString())
            editor.apply()
            startActivity(intent)

        }




        // This will pass the ArrayList to our Adapter
        //val adapter = CustomAdapter(data)

        // Setting the Adapter with the recyclerview
        //recyclerview.adapter = adapter



        /*
        val buttonOpenDeal = recyclerview.findViewById<ImageButton>(R.id.btnOpenDeal)
        Log.d(TAG,buttonOpenDeal.id.toString())*/

        /*
        buttonOpenDeal.setOnClickListener{
            val intent = Intent(this,AddActivity::class.java)
            startActivity(intent)
        }*/


        //TODO zprovoznit list


    }
}