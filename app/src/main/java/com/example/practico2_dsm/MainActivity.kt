package com.example.practico2_dsm
import com.example.practico2_dsm.ActivityAdapter

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ActivityAdapter
    private lateinit var database: DatabaseReference
    private var activityList = mutableListOf<ActivityModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        val fabAdd = findViewById<FloatingActionButton>(R.id.fabAdd)
        fabAdd.setOnClickListener {
            val intent = Intent(this, FormActivity::class.java)
            startActivity(intent)
        }

        recyclerView = findViewById(R.id.recyclerActivities)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = ActivityAdapter(
            activityList,
            onEditClick = { activity ->
                val intent = Intent(this, FormActivity::class.java)
                intent.putExtra("activityId", activity.id)
                intent.putExtra("title", activity.title)
                intent.putExtra("description", activity.description)
                intent.putExtra("status", activity.status)
                intent.putExtra("creationDate", activity.creationDate)
                startActivity(intent)
            },
            onDeleteClick = { activity ->
                database.child(activity.id).removeValue()
                    .addOnSuccessListener {
                        Toast.makeText(this, "Actividad eliminada", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Error al eliminar", Toast.LENGTH_SHORT).show()
                    }
            }
        )

        recyclerView.adapter = adapter

        database = FirebaseDatabase.getInstance().reference.child("activities")

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                activityList.clear()
                for (data in snapshot.children) {
                    val activity = data.getValue(ActivityModel::class.java)
                    activity?.let { activityList.add(it) }
                }
                adapter.updateList(activityList)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, "Error al leer datos", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        Log.d("MainActivity", "onCreateOptionsMenu llamado")
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_sign_out -> {
                FirebaseAuth.getInstance().signOut().also {
                    Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, RegisterActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
