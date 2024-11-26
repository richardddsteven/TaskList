package tasklist.c14220154.tasklist_c14220154

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.Calendar

class taskUpdate : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_task_update)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        // Inisialisasi View
        val namaKegiatan = findViewById<TextView>(R.id.judulActivity)
        val judulTask = findViewById<EditText>(R.id.judulTask)
        val deskripsiTask = findViewById<EditText>(R.id.deskripsiTask)
        val dateButton = findViewById<Button>(R.id.dateTask)
        val createTaskButton = findViewById<Button>(R.id.addTask)

        // Variabel untuk menyimpan tanggal
        var selectedDate: String? = null

        val action = intent.getStringExtra("action")
        if (action != null) {
            namaKegiatan.text = action
        }
        val judul = intent.getStringExtra("judul")
        if (judul != null) {
            judulTask.setText(judul)
        }

        val deskripsi = intent.getStringExtra("deskripsi")
        if (deskripsi != null) {
            deskripsiTask.setText(deskripsi)
        }


        // Listener untuk memilih tanggal
        dateButton.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog =
                DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                    selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                    Toast.makeText(this, "Tanggal dipilih: $selectedDate", Toast.LENGTH_SHORT)
                        .show()
                }, year, month, day)

            datePickerDialog.show()
        }

        createTaskButton.setOnClickListener {
            val judul = judulTask.text.toString().trim()
            val deskripsi = deskripsiTask.text.toString().trim()

            if (judul.isEmpty() || deskripsi.isEmpty() || selectedDate == null) {
                Toast.makeText(this, "Mohon lengkapi semua data!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Simpan data ke Intent
            val resultIntent = Intent().apply {
                putExtra("judul", judul)
                putExtra("deskripsi", deskripsi)
                putExtra("date", selectedDate)
                selectedImage?.let { putExtra("image", it)
                    putExtra("position", intent.getIntExtra("position", -1)) // Add position

                }
            }
            setResult(RESULT_OK, resultIntent)
            finish()
        }


    }
}

private var selectedImage: Int? = null