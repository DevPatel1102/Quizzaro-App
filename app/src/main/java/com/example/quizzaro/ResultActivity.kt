package com.example.quizzaro

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.quizzaro.databinding.ActivityResultBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ResultActivity : AppCompatActivity() {

    lateinit var resultBinding: ActivityResultBinding

    val database = FirebaseDatabase.getInstance()
    val reference = database.reference.child("Scores")

    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser

    var userCorrect = ""

    override fun onCreate(savedInstanceState: Bundle?) {

        resultBinding = ActivityResultBinding.inflate(layoutInflater)
        val view = resultBinding.root
        super.onCreate(savedInstanceState)
        setContentView(view)

        reference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                user?.let {

                    val userUID = it.uid

                    userCorrect = snapshot.child(userUID).child("correct").value.toString()

                    resultBinding.userScore.text= userCorrect

                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        resultBinding.buttonNewQuiz.setOnClickListener {

            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}