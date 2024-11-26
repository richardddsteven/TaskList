package tasklist.c14220154.tasklist_c14220154

import android.content.DialogInterface
import java.util.concurrent.TimeUnit
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        _rvTask = findViewById(R.id.rv_task)

        val buttonCreate = findViewById<Button>(R.id.btn_add_task)

        buttonCreate.setOnClickListener {
            val intent = Intent(this, taskUpdate::class.java)
            intent.putExtra("action", "Tambah Task")
            startActivityForResult(intent, add_task)
        }
//        kalau perlu data dari string.xml (string.xml belum di configure)
//        fun SiapakanData() {
//            _judul = resources.getStringArray(R.array.namaTask).toMutableList()
//            _deskripsi = resources.getStringArray(R.array.DeskripsiTask).toMutableList()
//            _date = resources.getStringArray(R.array.TanggalTask).toMutableList()
//            _image = resources.getStringArray(R.array.GambarTask).toMutableList()
//
//        }

        sp = getSharedPreferences("MySharedPref", MODE_PRIVATE)
        val editor = sp.edit()
        editor.putString("key", "value")
        editor.apply()



        fun tambahData() {
            _listTask.clear()
            for (position in _judul.indices) {
                val task = task(
                    _judul[position],
                    _deskripsi[position],
                    _date[position],
                    calculateRemainingTime(_date[position])
                )
                _listTask.add(task)
            }
        }

        fun tampilkanData() {
            _taskAdapter = task_recycler(_listTask)
            _rvTask.layoutManager = LinearLayoutManager(this)
            _rvTask.adapter = _taskAdapter

            _taskAdapter.setOnItemClickCallback(object: task_recycler.OnItemClickCallback {

                override fun delData(pos: Int) {
                    if (pos >= 0 && pos < _listTask.size) {
                        AlertDialog.Builder(this@MainActivity)
                            .setTitle("HAPUS DATA")
                            .setMessage("Apakah benar ingin menghapus ${_listTask[pos].judul}?")
                            .setPositiveButton("Hapus") { _, _ ->
                                _listTask.removeAt(pos)
                                _taskAdapter.notifyItemRemoved(pos)
                                _taskAdapter.notifyItemRangeChanged(pos, _listTask.size)
                            }
                            .setNegativeButton("Batal") { dialog, _ ->
                                dialog.dismiss()
                                Toast.makeText(
                                    this@MainActivity,
                                    "Penghapusan dibatalkan",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            .show()
                    } else {
                        Toast.makeText(this@MainActivity, "Invalid position", Toast.LENGTH_SHORT)
                            .show()
                    }
                }

                override fun editData(pos: Int) {
                    if (pos >= 0 && pos < _listTask.size) {
                        val intent = Intent(this@MainActivity, taskUpdate::class.java)
                        intent.putExtra("action", "Edit Task")
                        intent.putExtra("position", pos)  // Tambahkan ini
                        intent.putExtra("judul", _listTask[pos].judul)
                        intent.putExtra("deskripsi", _listTask[pos].deskripsi)
                        intent.putExtra("date", _listTask[pos].date)
                        intent.putExtra("time", _listTask[pos].time)
                        startActivityForResult(intent, EDIT_TASK)  // Gunakan request code berbeda
                    } else {
                        Toast.makeText(this@MainActivity, "Invalid position", Toast.LENGTH_SHORT)
                            .show()
                    }
                }

                override fun updateStatus(pos: Int) {
                    // kalau status belum start, maka bisa di start
                    if (pos >= 0 && pos < _listTask.size && !_listTask[pos].isStatusStarted) {
                        _listTask[pos].isStatusStarted = true
                        _taskAdapter.notifyItemChanged(pos) // Beri tahu adapter untuk memperbarui item
                    } else if (pos >= 0 && pos < _listTask.size && _listTask[pos].isStatusStarted) {
                        _listTask[pos].isStatusEnded = true
                        _taskAdapter.notifyItemChanged(pos) // Beri tahu adapter untuk memperbarui item
                    } else {
                        Toast.makeText(this@MainActivity, "Invalid position", Toast.LENGTH_SHORT)
                            .show()
                    }
                }


            })
        }


        _judul = mutableListOf()
        _deskripsi = mutableListOf()
        _date = mutableListOf()
        _time = mutableListOf()

//      SiapakanData()
        tambahData()
        tampilkanData()
        displayFavoriteTask()
    }

    private fun displayFavoriteTask() {
        val favoriteTask = sp.getString("judul", "No favorite task")
        Toast.makeText(this, "Favorite Task: $favoriteTask", Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val add_task = 1
        const val EDIT_TASK = 2
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == add_task && resultCode == RESULT_OK) {
            val judul = data?.getStringExtra("judul")
            val deskripsi = data?.getStringExtra("deskripsi")
            val date = data?.getStringExtra("date")

            if (judul != null && deskripsi != null && date != null) {
                addTask(judul, deskripsi, date)
            }
        } else if (requestCode == EDIT_TASK && resultCode == RESULT_OK) {
            val position = data?.getIntExtra("position", -1)
            val judul = data?.getStringExtra("judul")
            val deskripsi = data?.getStringExtra("deskripsi")
            val date = data?.getStringExtra("date")

            if (position != null && position >= 0 && judul != null && deskripsi != null && date != null) {
                _listTask[position].judul = judul
                _listTask[position].deskripsi = deskripsi
                _listTask[position].date = date
                _taskAdapter.notifyItemChanged(position)
            }
        }
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


    private fun addTask(judul: String, deskripsi: String, date: String) {
        // Tambahkan task baru
        val task = task(judul, deskripsi, date, "")
        _listTask.add(task)

        // Perbarui adapter
        _taskAdapter.notifyItemInserted(_listTask.size - 1)
    }

    lateinit var sp : SharedPreferences
    private var _listTask: ArrayList<task> = arrayListOf()
    private lateinit var _rvTask: RecyclerView
    private lateinit var _taskAdapter: task_recycler
    private var _judul: MutableList<String> = mutableListOf()
    private var _deskripsi: MutableList<String> = mutableListOf()
    private var _date: MutableList<String> = mutableListOf()
    private var _time: MutableList<String> = mutableListOf()
}