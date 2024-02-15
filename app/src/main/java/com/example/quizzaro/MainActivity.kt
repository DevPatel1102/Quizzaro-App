package com.example.quizzaro

import CircleTransform
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import com.example.quizzaro.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class MainActivity : AppCompatActivity() {

    val database = FirebaseDatabase.getInstance()
    val reference = database.reference.child("Scores")

    lateinit var mainBinding: ActivityMainBinding
    val auth : FirebaseAuth = FirebaseAuth.getInstance()
    val user = auth.currentUser

    var userCorrect = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        val view = mainBinding.root

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.status_color)
        }
        setContentView(view)

        val photoUrl = user?.photoUrl

        photoUrl?.let {
            Picasso.get().load(it).transform(CircleTransform()).into(mainBinding.userLogo)
        }

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                user?.let {

                    val userUID = it.uid

                    userCorrect = snapshot.child(userUID).child("correct").value.toString()
                    mainBinding.userScore.text= userCorrect

                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        mainBinding.userLogo.setOnClickListener {

            val fragmentManager : FragmentManager = supportFragmentManager
            val myDialogFragment = MyDialogFragment()

            myDialogFragment.show(fragmentManager,"MyDialogFragment")

        }

        mainBinding.linearLayoutArt.setOnClickListener{
            val art = true
            val intent = Intent(this,QuizActivity::class.java)
            intent.putExtra("Art",art)
            startActivity(intent)
        }
        mainBinding.linearLayoutScience.setOnClickListener{
            val science = true
            val intent = Intent(this,QuizActivity::class.java)
            intent.putExtra("Science",science)
            startActivity(intent)
        }
        mainBinding.linearLayoutHistory.setOnClickListener{
            val history = true
            val intent = Intent(this,QuizActivity::class.java)
            intent.putExtra("History",history)
            startActivity(intent)
        }
        mainBinding.linearLayoutFood.setOnClickListener{
            val food = true
            val intent = Intent(this,QuizActivity::class.java)
            intent.putExtra("Food",food)
            startActivity(intent)
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
        finish()
    }
}