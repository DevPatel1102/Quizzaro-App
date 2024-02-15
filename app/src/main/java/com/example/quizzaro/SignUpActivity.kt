package com.example.quizzaro

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.quizzaro.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth

class SignUpActivity : AppCompatActivity() {

    lateinit var signUpBinding: ActivitySignUpBinding

    val auth : FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        signUpBinding = ActivitySignUpBinding.inflate(layoutInflater)
        val view = signUpBinding.root

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            window.statusBarColor = ContextCompat.getColor(this, R.color.status_color)
        }
        setContentView(view)

        signUpBinding.buttonSignUp.setOnClickListener {
            var email = signUpBinding.editTextSignUpEmail.text.toString()
            var password = signUpBinding.editTextSignUpPassword.text.toString()
            signUpWithFirebase(email,password)
        }

    }

    fun signUpWithFirebase(email : String,password : String){

        signUpBinding.progressBar.visibility = View.VISIBLE
        signUpBinding.buttonSignUp.isClickable = false

        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener {task->

            if(task.isSuccessful){
                Toast.makeText(applicationContext,"Account is succesfully created",Toast.LENGTH_SHORT).show()
                finish()
                signUpBinding.progressBar.visibility = View.INVISIBLE
                signUpBinding.buttonSignUp.isClickable = true
            }
            else{
                Toast.makeText(applicationContext,task.exception?.localizedMessage,Toast.LENGTH_SHORT).show()
                signUpBinding.progressBar.visibility = View.INVISIBLE
            }
        }

    }
}