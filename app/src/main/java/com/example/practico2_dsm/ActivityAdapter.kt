package com.example.practico2_dsm

import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import android.widget.PopupMenu

class ActivityAdapter(
    private var activityList: List<ActivityModel>,
    private val onEditClick: (ActivityModel) -> Unit,
    private val onDeleteClick: (ActivityModel) -> Unit
) : RecyclerView.Adapter<ActivityAdapter.ActivityViewHolder>() {

    inner class ActivityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
        val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
        val tvDate: TextView = itemView.findViewById(R.id.tvDate)

        init {
            itemView.setOnLongClickListener {
                val activity = activityList[adapterPosition]
                showPopupMenu(it, activity)
                true
            }
        }

        private fun showPopupMenu(view: View, activity: ActivityModel) {
            val popup = PopupMenu(view.context, view)
            popup.menuInflater.inflate(R.menu.menu_item_activity, popup.menu)

            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.menu_edit -> {
                        onEditClick(activity)
                        true
                    }
                    R.id.menu_delete -> {
                        onDeleteClick(activity)
                        true
                    }
                    else -> false
                }
            }

            popup.show()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_activity, parent, false)
        return ActivityViewHolder(view)
    }

    override fun onBindViewHolder(holder: ActivityViewHolder, position: Int) {
        val activity = activityList[position]
        val formattedDate = formatDate(activity.creationDate)

        holder.tvDate.text = "Creado el: $formattedDate"
        holder.tvTitle.text = activity.title
        holder.tvDescription.text = if (activity.description.isNotEmpty()) activity.description else "(Sin descripción)"
        holder.tvStatus.text = "Estado: ${activity.status}"
    }

    override fun getItemCount(): Int = activityList.size

    fun updateList(newList: List<ActivityModel>) {
        activityList = newList
        notifyDataSetChanged()
    }

    private fun formatDate(timestamp: String): String {
        return try {
            val millis = timestamp.toLong()
            val sdf = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault())
            sdf.format(java.util.Date(millis))
        } catch (e: Exception) {
            "Fecha inválida"
        }
    }
}
