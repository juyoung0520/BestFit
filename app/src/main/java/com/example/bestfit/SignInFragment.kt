package com.example.bestfit

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_signin.view.*

class SignInFragment : Fragment() {
    val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_signin, container, false)

        view.fragment_signin_btn_signin.setOnClickListener {
            signIn(view)
        }

        view.fragment_signin_btn_signup.setOnClickListener {
            signUp()
        }

        return view
    }

    fun signIn(view: View) {
        val email = view.fragment_signin_text_email.text.toString()
        val password = view.fragment_signin_text_password.text.toString()

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val signInActivity = activity as SignInActivity
                signInActivity.startMainActivity()
            }
            else {
                Toast.makeText(context, "login fail", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun signUp() {
        val signInActivity = activity as SignInActivity
        signInActivity.replaceFragment(SignUpFragment())
    }
}