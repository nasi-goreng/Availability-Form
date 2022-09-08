package com.example.availabilityform

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.availabilityform.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private  lateinit var  binding : ActivityMainBinding
    private lateinit var  appDb : AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        appDb = AppDatabase.getDatabase(this)

        binding.btnWriteData.setOnClickListener {
            writeData()
        }

        binding.btnReadData.setOnClickListener {
            readData()
        }

        binding.btnDelete.setOnClickListener {
            delete()
        }

        binding.btnDeleteAll.setOnClickListener {
            deleteAllData()
        }

        binding.btnUpdate.setOnClickListener {
            updateData()
        }
    }

    private fun deleteAllData() {
        GlobalScope.launch {
            appDb.studentDao().deleteAll()
            withContext(Dispatchers.Main) {
                binding.tvFirstName.text = ""
                binding.tvLastName.text = ""
                binding.tvStudentNo.text = ""
            }
        }
    }

    private fun delete() {
        val studentNo = binding.etStudentNoRead.text.toString()

        if (studentNo.isNotEmpty()) {
            GlobalScope.launch(Dispatchers.IO) {
                appDb.studentDao().delete(studentNo.toInt())
            }
            binding.etStudentNoRead.text.clear()
            Toast.makeText(this@MainActivity, "Successfully deleted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this@MainActivity, "Please Enter Student No.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateData() {
        val firstName = binding.etFirstName.text.toString()
        val lastName = binding.etLastName.text.toString()
        val studentNo = binding.etStudentNoRead.text.toString()

        if (firstName.isNotEmpty() && lastName.isNotEmpty()  && studentNo.isNotEmpty()) {

            GlobalScope.launch(Dispatchers.IO) {
                appDb.studentDao().update(firstName, lastName, studentNo.toInt())
            }
            binding.etFirstName.text.clear()
            binding.etLastName.text.clear()
            binding.etStudentNoRead.text.clear()

            Toast.makeText(this@MainActivity, "Update Successful", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this@MainActivity, "Please Enter Data", Toast.LENGTH_SHORT).show()
        }
    }

    private fun writeData() {
        val firstName = binding.etFirstName.text.toString()
        val lastName = binding.etLastName.text.toString()
//        val rollNo = binding.etRollNo.text.toString()

        if (firstName.isNotEmpty() && lastName.isNotEmpty() /* && rollNo.isNotEmpty() */) {
            val student = Student(
                null, firstName, lastName, /* rollNo.toInt() */
            )
            GlobalScope.launch(Dispatchers.IO) {
                appDb.studentDao().insert(student)
            }
            binding.etFirstName.text.clear()
            binding.etLastName.text.clear()
//            binding.etRollNo.text.clear()

            Toast.makeText(this@MainActivity, "Submission Successful", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this@MainActivity, "Please Enter Data", Toast.LENGTH_SHORT).show()
        }
    }

    private suspend fun displayOneData(student: Student) {
            withContext(Dispatchers.Main) {
                displayAllData(listOf(""))
                binding.tvStudentNo.text = student.id.toString()
                binding.tvFirstName.text = student.firstName
                binding.tvLastName.text = student.lastName
//            binding.tvRollNo.text = student.rollNo.toString()
            }
    }

    private fun displayAllData(student: List<String>) {
        //        val myDataset = Datasource().loadAffirmations()
//        val handler = Handler(Looper.getMainLooper())
//        handler.post {
//            val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
//            recyclerView.adapter = ItemAdapter(this, student)
//            recyclerView.setHasFixedSize(true)
//        }
        binding.tvFirstName.text = ""
        binding.tvLastName.text = ""
        binding.tvStudentNo.text = ""
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.adapter = ItemAdapter(this, student)
        recyclerView.setHasFixedSize(true)
    }


    private fun readData() {

        val studentNo = binding.etStudentNoRead.text.toString()

        if (studentNo.isNotEmpty()) {
            lateinit var student: Student

            GlobalScope.launch {
                student = appDb.studentDao().findByRoll(studentNo.toInt())
                println("###############################################")
                println(student)
                displayOneData(student)
            }
        } else {
            lateinit var student: List<Student>
            GlobalScope.launch {
                student = appDb.studentDao().getAll()
                println("###############################################")
                println(student)
                val studentStrings = mutableListOf<String>()
                for (person in student) {
                    val studentString = "${person.id.toString()} ${person.firstName} ${person.lastName}"
                    studentStrings.add(studentString)
                }
                println("###################################################################")
                println(studentStrings)
//                displayAllData(studentStrings)
                withContext(Dispatchers.Main) {
                    displayAllData(studentStrings)
                }
            }
        }

    }
}