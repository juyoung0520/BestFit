package com.example.bestfit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_signup.view.*

class SignUpFragment : Fragment() {
    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_signup, container, false)

        view.fragment_signup_btn_signup.setOnClickListener{
            signUp(view)
        }

        return view
    }

    private fun signUp(view: View) {
        val email = view.fragment_signup_text_email.text.toString()
        val password = view.fragment_signup_text_password.text.toString()

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val signInActivity = activity as SignInActivity
                signInActivity.startMainActivity()
            }
            else {
                if (task.exception?.message?.indexOf("badly formatted") != -1) {
                    // 올바른 이메일 형식
                }
                else if (task.exception?.message?.indexOf("email address is already in use") != -1) {
                    // 이미 존재하는 이메일

                }
                else if (task.exception?.message?.indexOf("password is invalid") != -1) {
                    // 올바른 비밀번호 형식 최소 6자
                }

                println(task.exception)
                Toast.makeText(context, "fail", Toast.LENGTH_SHORT).show()
            }
        }
    }
}