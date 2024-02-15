package com.example.quizzaro

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.quizzaro.databinding.ActivityForgotPasswordBinding
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : AppCompatActivity() {

    lateinit var forgotPasswordBinding : ActivityForgotPasswordBinding
    val auth : FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {

        forgotPasswordBinding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        val view = forgotPasswordBinding.root

        super.onCreate(savedInstanceState)
        setContentView(view)

        forgotPasswordBinding.buttonResetPassword.setOnClickListener {
            var email = forgotPasswordBinding.editTextForgotPasswordEmail.text.toString()

            auth.sendPasswordResetEmail(email).addOnCompleteListener { task->

                if(task.isSuccessful){
                    Toast.makeText(applicationContext, "Reset link is sent to provided email (Check spam if not found)", Toast.LENGTH_LONG).show()
                    finish()
                }else{
                    Toast.makeText(applicationContext, task.exception?.localizedMessage, Toast.LENGTH_LONG).show()
                }
            }
        }

    }
}