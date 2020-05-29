package com.example.bestfit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_signup.view.*

class SignUpFragment : Fragment() {
    val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_signup, container, false)

        view.fragment_signup_btn_signup.setOnClickListener{
            signUp(view)
        }

        return view
    }

    fun signUp(view: View) {
        val email = view.fragment_signup_text_email.text.toString()
        val password = view.fragment_signup_text_password.text.toString()

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val signInActivity = activity as SignInActivity
                signInActivity.startMainActivity()
            }
            else {
                Toast.makeText(context, "fail", Toast.LENGTH_SHORT).show()
            }
        }
    }
}