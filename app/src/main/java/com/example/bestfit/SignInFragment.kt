package com.example.bestfit

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.fragment_signin.view.*

class SignInFragment : Fragment() {
    private val auth = FirebaseAuth.getInstance()
    private val GOOGLE_SIGNIN_CODE = 9001

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

        view.fragment_signin_tv_goolge_signin.setOnClickListener {
            signUpGoogle()
        }

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GOOGLE_SIGNIN_CODE) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            if (task.isSuccessful) {
                val account = task.result!!
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)

                auth.signInWithCredential(credential).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val signInActivity = activity as SignInActivity
                        signInActivity.startMainActivity()
                    }
                    else {
                        Toast.makeText(context, "login fail", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun signIn(view: View) {
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

    private fun signUp() {
        val signInActivity = activity as SignInActivity
        signInActivity.replaceFragment(SignUpFragment())
    }

    private fun signUpGoogle() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        val googleSignInClient = GoogleSignIn.getClient(activity!!, gso)
        val intent = googleSignInClient?.signInIntent

        startActivityForResult(intent, GOOGLE_SIGNIN_CODE)
    }
}