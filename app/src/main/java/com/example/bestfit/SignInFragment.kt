package com.example.bestfit

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_signin.*
import kotlinx.android.synthetic.main.fragment_signin.*
import kotlinx.android.synthetic.main.fragment_signin.view.*
import kotlin.math.sign

class SignInFragment : Fragment() {
    private val auth = FirebaseAuth.getInstance()
    private val GOOGLE_SIGNIN_CODE = 1210

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_signin, container, false)

        initToolbar()

        view.fragment_signin_text_email.doOnTextChanged { text, start, before, count ->
            view.fragment_signin_layout_text_email.error = null
        }

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

    private fun initToolbar() {
        val signInActivity = activity!! as SignInActivity
        signInActivity.activity_signin_tv_toolbar_title.text = "로그인"
        signInActivity.supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }

    private fun signIn(view: View) {
        val email = view.fragment_signin_text_email.text.toString()
        val password = view.fragment_signin_text_password.text.toString()

//        auth.fetchSignInMethodsForEmail(email).addOnCompleteListener { task ->
//            println(task.exception)
//            if (task.isSuccessful) {
//                if (!task.result?.signInMethods.isNullOrEmpty()) {
//                    // 이미 있는 이메일
//                    println(task.result?.signInMethods)
//                    println("이미 있어용~")
//                }
//            }
//        }
//
//        return

        if (email.isNullOrEmpty()) {
            view.fragment_signin_layout_text_email.error = "이메일을 입력해주세요."
        } else {
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val signInActivity = activity as SignInActivity
                    signInActivity.startMainActivity()
                } else {

                    if (task.exception?.message?.indexOf("badly formatted") != -1) {
                        // 올바른 이메일 형식
                    } else if (task.exception?.message?.indexOf("no user record") != -1) {
                        // 존재하지 않는 이메일

                    } else if (task.exception?.message?.indexOf("password is invalid") != -1) {
                        // 비밀번호 틀림
                    }

                    println(task.exception?.message)
                    Toast.makeText(context, "login fail", Toast.LENGTH_SHORT).show()
                }
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