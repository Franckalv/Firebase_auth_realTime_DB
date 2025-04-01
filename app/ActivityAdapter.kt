package com.example.practico2_dsm
package com.example.practico2_dsm

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ActivityAdapter(
    private var activityList: List<ActivityModel>
) : RecyclerView.Adapter<ActivityAdapter.ActivityViewHolder>() {

    class ActivityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
        val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_activity, parent, false)
        return ActivityViewHolder(view)
    }

    override fun onBindViewHolder(holder: ActivityViewHolder, position: Int) {
        val activity = activityList[position]
        holder.tvTitle.text = activity.title
        holder.tvDescription.text = if (activity.description.isNotEmpty()) activity.description else "(Sin descripción)"
        holder.tvStatus.text = "Estado: ${activity.status}"
    }

    override fun getItemCount(): Int = activityList.size

    fun updateList(newList: List<ActivityModel>) {
        activityList = newList
        notifyDataSetChanged()
    }
}
