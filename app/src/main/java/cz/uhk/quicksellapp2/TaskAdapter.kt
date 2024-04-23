package cz.uhk.quicksellapp2

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView


class TaskAdapter(private val tasks: List<DealData>) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {
    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {



        private val actionButton: ImageButton = itemView.findViewById(R.id.buttonOpenDeal)
        fun bind(dealData: DealData) {

            itemView.findViewById<TextView>(R.id.dealNameView).text = dealData.title

            actionButton.setOnClickListener {

                val context = itemView.context

                val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putString("lastOpenedDealName",dealData.title)
                editor.apply()



                val intent = Intent(context,DealViewActivity::class.java)
                context.startActivity(intent)
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_deal, parent, false)
        return TaskViewHolder(view)
    }

    override fun getItemCount(): Int {
        return tasks.size
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        holder.bind(task)


    }


}