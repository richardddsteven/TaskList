package tasklist.c14220154.tasklist_c14220154

import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class task_recycler(private val listTask: ArrayList<task>) : RecyclerView.Adapter<task_recycler.ListViewHolder>() {

    private lateinit var onItemclickCallback: OnItemClickCallback

    interface OnItemClickCallback {
        fun onItemClicked(data: task)
        fun delData(pos: Int)
        fun editData(pos: Int)
        fun updateStatus(pos: Int)
    }

    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var _judulTask: TextView = itemView.findViewById(R.id.judulTask)
        var _deskripsiTask: TextView = itemView.findViewById(R.id.deskripsiTask)
        var _dateTask: TextView = itemView.findViewById(R.id.dateTask)
        var _time: TextView = itemView.findViewById(R.id.timeTask)
        val _btnHapus: Button = itemView.findViewById(R.id.deleteTask)
        val _btnEdit: Button = itemView.findViewById(R.id.editTask)
        val _btnStatus: Button = itemView.findViewById(R.id.statusTask)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_task_layout, parent, false)
        return ListViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listTask.size
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val task = listTask[position]

        holder._judulTask.text = task.judul
        holder._deskripsiTask.text = task.deskripsi
        holder._dateTask.text = task.date
        holder._time.text = task.time
        val resourceId = holder.itemView.context.resources.getIdentifier(
            task.image,
            "drawable",
            holder.itemView.context.packageName
        )

        if (task.isStatusStarted) {
            holder._btnStatus.text = "End"
            holder._btnEdit.isEnabled = false   // Nonaktifkan tombol Edit
            holder._time.visibility = View.VISIBLE
            startTimer(holder, task, position)
        } else {
            holder._btnStatus.text = "Start"
            holder._btnStatus.isEnabled = true
            holder._btnEdit.isEnabled = true
        }

        if (task.isStatusEnded) {
            holder._btnStatus.text = "Ended"
            holder._btnStatus.isEnabled = false
            holder._btnEdit.isEnabled = false
            holder.itemView.findViewById<ImageView>(R.id.imageView2).setBackgroundResource(R.drawable.rounded_border_ended)
        }



        holder._btnHapus.setOnClickListener {
            onItemclickCallback.delData(position)
        }

        holder._btnEdit.setOnClickListener {
            onItemclickCallback.editData(position)
        }

        holder._btnStatus.setOnClickListener {
            onItemclickCallback.updateStatus(position)
        }
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemclickCallback = onItemClickCallback
    }
    private fun startTimer(holder: ListViewHolder, task: task, position: Int) {
        val handler = android.os.Handler()
        val runnable = object : Runnable {
            override fun run() {
                val remainingTime = calculateRemainingTime(task.date)
                holder._time.text = remainingTime
                if (remainingTime == "00:00") {
                    task.isStatusEnded = true
                    notifyItemChanged(position)
                } else {
                    handler.postDelayed(this, 60000) // Update every minute
                }
            }
        }
        handler.post(runnable)
    }


    private fun calculateRemainingTime(date: String): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val targetDate = dateFormat.parse("$date 23:59")
        val currentDate = Date()

        val diffInMillis = targetDate.time - currentDate.time
        if (diffInMillis <= 0) {
            return "00:00"
        }
        val hours = TimeUnit.MILLISECONDS.toHours(diffInMillis)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(diffInMillis) % 60

        return String.format("%02d:%02d", hours, minutes)
    }
}
