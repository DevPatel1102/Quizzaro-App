package com.example.quizzaro

import android.content.ComponentName
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.quizzaro.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.GoogleAuthProvider
import kotlin.math.log

class LoginActivity : AppCompatActivity() {

    lateinit var loginBinding: ActivityLoginBinding

    val auth: FirebaseAuth = FirebaseAuth.getInstance()

    lateinit var googleSignInClient: GoogleSignInClient

    lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    var user_displayName: String? = null
    var userUID =""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loginBinding = ActivityLoginBinding.inflate(layoutInflater)
        val view = loginBinding.root

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.status_color)
        }
        setContentView(view)

        //register
        registerActivityForGoogleSignIn()

        val textOfGoogleButton = loginBinding.buttonSignInGoogle.getChildAt(0) as TextView
        textOfGoogleButton.text = "Sign in with Google"
        textOfGoogleButton.setTextColor(Color.BLACK)
        textOfGoogleButton.textSize = 18F

        loginBinding.buttonSignIn.setOnClickListener {

            var userEmail = loginBinding.editTextLoginEmail.text.toString()
            var userPassword = loginBinding.editTextLoginPassword.text.toString()
            signInUser(userEmail, userPassword)

        }
        loginBinding.buttonSignInGoogle.setOnClickListener {

            initializeGoogleSignInClient()

            val lastSignedInAccount = GoogleSignIn.getLastSignedInAccount(this)

            if (lastSignedInAccount != null) {
                // User is already signed in, revoke access and sign in with Google
                googleSignInClient.revokeAccess().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Now sign in with Google
                        signInGoogle()

                    } else {
                        // Handle the failure to revoke access
                        Log.e(TAG, "Failed to revoke access", task.exception)
                    }
                }
            } else {
                // User is not signed in, directly sign in with Google
                signInGoogle()
            }
        }

        loginBinding.textSignUp.setOnClickListener {

            val intent = Intent(this@LoginActivity, SignUpActivity::class.java)
            startActivity(intent)

        }
        loginBinding.textForgotPassword.setOnClickListener {
            val intent = Intent(this@LoginActivity, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }

    }
    private fun initializeGoogleSignInClient() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("393326624046-ece8146ab1itqs4cl5t92ce1vkknc2gq.apps.googleusercontent.com")
            .requestEmail().build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun signInGoogle() {
//        initializeGoogleSignInClient()
        val signInIntent: Intent = googleSignInClient.signInIntent
        activityResultLauncher.launch(signInIntent)
    }

    fun signInUser(userEmail: String, userPassword: String) {
        auth.signInWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(applicationContext, "Welcome To Quizzaro", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                val errorMessage = task.exception?.localizedMessage
                Toast.makeText(
                    applicationContext,
                    "Authentication Failed: $errorMessage",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e("LoginActivity", "Authentication Failed: $errorMessage")

            }
        }
    }

    private fun registerActivityForGoogleSignIn(){

        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult(),
            ActivityResultCallback {result ->

                val resultCode = result.resultCode
                val data = result.data

                if(resultCode== RESULT_OK && data != null){

                    val task : Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
                    firebaseSignInWithGoogle(task)

                }

            })

    }

    fun firebaseSignInWithGoogle(task : Task<GoogleSignInAccount>){


        try{
            val account : GoogleSignInAccount = task.getResult(ApiException::class.java)
            Toast.makeText(applicationContext, "Welcome To Quizzaro", Toast.LENGTH_SHORT).show()
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
            firebaseGoogleAccount(account)
        }catch (e :  ApiException){
            Toast.makeText(applicationContext, e.localizedMessage, Toast.LENGTH_SHORT).show()
        }

    }

    private fun firebaseGoogleAccount(account : GoogleSignInAccount){

        val authCredential = GoogleAuthProvider.getCredential(account.idToken,null)
        auth.signInWithCredential(authCredential).addOnCompleteListener { task->
            if(task.isSuccessful){
                val display_name = account.displayName.toString()
                user_displayName=display_name
                Log.d("User_Name",user_displayName.toString())

            }else{

            }
        }
    }

    override fun onStart() {
        super.onStart()
        val user = auth.currentUser
        Log.d("user",user.toString())
        if(user != null){

            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }


}