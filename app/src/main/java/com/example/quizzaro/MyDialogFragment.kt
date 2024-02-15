package com.example.quizzaro

import CircleTransform
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso


class MyDialogFragment : DialogFragment() {

    lateinit var user_email_id : TextView
    lateinit var sign_out_button : Button
    lateinit var user_photo: ImageView
    val auth : FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view : View = inflater.inflate(R.layout.fragment_dialog, container, false)

        user_email_id = view.findViewById(R.id.user_email_id)
        sign_out_button = view.findViewById(R.id.sign_out_button)
        user_photo = view.findViewById(R.id.user_photo)

        dialog!!.window!!.setBackgroundDrawableResource(android.R.color.transparent)

        val user = auth.currentUser
        val username = user?.displayName
        val photoUrl = user?.photoUrl

        photoUrl?.let {
            Picasso.get().load(it).transform(CircleTransform()).into(user_photo)
        }

        username?.let {
            Log.d("user_display", it)
            user_email_id.text = it
        }

//        val loginActivity = activity as? LoginActivity

        sign_out_button.setOnClickListener {
            auth.signOut()
//            loginActivity?.googleSignInClient?.let {
//                it.signOut().addOnCompleteListener { task->
//                    if(task.isSuccessful){
//                        Toast.makeText(requireContext(), "Sign-out successful", Toast.LENGTH_SHORT).show()
//                        // You can navigate to the login screen or perform other actions here
//                    } else {
//                        // Handle sign-out failure
//                        Toast.makeText(requireContext(), "Sign-out failed", Toast.LENGTH_SHORT).show()
//                        Log.e("LoginActivity", "Sign-out failed: ${task.exception}")
//                    }
//                }
//            }
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
            dialog!!.dismiss()
        }
        return view
    }

}