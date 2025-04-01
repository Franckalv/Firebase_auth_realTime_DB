package com.example.practico2_dsm

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class FormActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form)

        database = FirebaseDatabase.getInstance().reference.child("activities")

        val etTitle = findViewById<EditText>(R.id.etTitle)
        val etDescription = findViewById<EditText>(R.id.etDescription)
        val spinnerStatus = findViewById<Spinner>(R.id.spinnerStatus)
        val btnSave = findViewById<Button>(R.id.btnSave)

        val spinnerAdapter = ArrayAdapter.createFromResource(
            this, R.array.status_options, android.R.layout.simple_spinner_item
        )
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerStatus.adapter = spinnerAdapter

        val activityId = intent.getStringExtra("activityId")
        val isEditing = activityId != null
        val creationDate = intent.getStringExtra("creationDate") ?: System.currentTimeMillis().toString()

        if (isEditing) {
            etTitle.setText(intent.getStringExtra("title"))
            etDescription.setText(intent.getStringExtra("description"))

            val status = intent.getStringExtra("status")
            val spinnerPosition = if (status == "completada") 1 else 0
            spinnerStatus.setSelection(spinnerPosition)
        }

        btnSave.setOnClickListener {
            val title = etTitle.text.toString().trim()
            val description = etDescription.text.toString().trim()
            val status = spinnerStatus.selectedItem.toString()

            if (title.isEmpty()) {
                etTitle.error = "El título es obligatorio"
                return@setOnClickListener
            }

            if (isEditing) {
                val updates = mapOf(
                    "title" to title,
                    "description" to description,
                    "status" to status
                )
                database.child(activityId!!).updateChildren(updates)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Actividad actualizada", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show()
                    }
            } else {
                val id = database.push().key ?: return@setOnClickListener

                val activity = ActivityModel(id, title, description, status, creationDate)
                database.child(id).setValue(activity)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            Toast.makeText(this, "Actividad creada", Toast.LENGTH_SHORT).show()
                            finish()
                        } else {
                            Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }
    }
}
