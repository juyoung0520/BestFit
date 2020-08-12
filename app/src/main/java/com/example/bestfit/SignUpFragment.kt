package com.example.bestfit

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_signup.view.*

class SignUpFragment : Fragment() {
    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_signup, container, false)

        view.fragment_signup_text_email.setTextInputLayout(view.fragment_signup_layout_text_email)
        view.fragment_signup_text_password.setTextInputLayout(view.fragment_signup_layout_text_password)
        view.fragment_signup_text_password_check.setTextInputLayout(view.fragment_signup_layout_text_password_check)

        view.fragment_signup_text_email.setOnFocusChangeListener { _, b ->
            if (!b)
                checkEmail(view)
        }

        view.fragment_signup_text_email.doOnTextChanged { text, start, before, count ->
            view.fragment_signup_btn_signup.isEnabled =
                !(view.fragment_signup_text_email.text.isNullOrEmpty() || view.fragment_signup_text_password.text.isNullOrEmpty() || view.fragment_signup_text_password_check.text.isNullOrEmpty())

            if (view.fragment_signup_text_email.text.isNullOrEmpty()) {
                view.fragment_signup_layout_text_email.error = null
                view.fragment_signup_layout_text_email.endIconMode = TextInputLayout.END_ICON_NONE
            }
        }

        view.fragment_signup_text_password.doOnTextChanged { text, start, before, count ->
            view.fragment_signup_btn_signup.isEnabled =
                !(view.fragment_signup_text_email.text.isNullOrEmpty() || view.fragment_signup_text_password.text.isNullOrEmpty() || view.fragment_signup_text_password_check.text.isNullOrEmpty())

            if (view.fragment_signup_text_password.text.isNullOrEmpty()) {
                view.fragment_signup_text_password_check.text = null
                view.fragment_signup_layout.visibility = View.GONE
            }
            else
                view.fragment_signup_layout.visibility = View.VISIBLE

            checkPassword(view)
        }

        view.fragment_signup_text_password_check.doOnTextChanged { text, start, before, count ->
            view.fragment_signup_btn_signup.isEnabled =
                !(view.fragment_signup_text_email.text.isNullOrEmpty() || view.fragment_signup_text_password.text.isNullOrEmpty() || view.fragment_signup_text_password_check.text.isNullOrEmpty())

            checkPassword(view)
        }

        initToolbar(view)

        view.fragment_signup_btn_signup.setOnClickListener{
            signUp(view)
        }

        return view
    }

    private fun initToolbar(view: View) {
        view.fragment_signup_toolbar.setNavigationIcon(R.drawable.ic_close)
        view.fragment_signup_toolbar.setNavigationOnClickListener {
            val signInActivity = requireActivity() as SignInActivity

            val imm = signInActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)

            signInActivity.replaceFragment(SignInFragment())
        }
    }

    private fun signUp(view: View) {
        val email = view.fragment_signup_text_email.text.toString()
        val password = view.fragment_signup_text_password.text.toString()
        val passwordCheck = view.fragment_signup_text_password_check.text.toString()

        if (email.isNullOrEmpty() || password.isNullOrEmpty() || passwordCheck.isNullOrEmpty())
            return

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val signInActivity = activity as SignInActivity
                signInActivity.startMainActivity()
            }
            else {
                if (task.exception?.message?.indexOf("badly formatted") != -1) {
                    // 올바른 이메일 형식
                    view.fragment_signup_layout_text_email.error = "잘못된 형식의 이메일입니다."
                }
                else if (task.exception?.message?.indexOf("email address is already in use") != -1) {
                    view.fragment_signup_layout_text_email.error = "이미 존재하는 이메일입니다."

                }
                else if (task.exception?.message?.indexOf("password is invalid") != -1) {
                    // 올바른 비밀번호 형식 최소 6자
                    view.fragment_signup_layout_text_password.error = "비밀번호 형식이 틀렸습니다. 다시 입력해 주세요."
                }

                println(task.exception)
                Toast.makeText(context, "fail", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkEmail(view : View) {
        val email = view.fragment_signup_text_email.text.toString()

        if (email.isNullOrEmpty())
            return

        auth.fetchSignInMethodsForEmail(email).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                if (task.result!!.signInMethods!!.contains(EmailAuthProvider.EMAIL_PASSWORD_SIGN_IN_METHOD)) {
                    view.fragment_signup_layout_text_email.error = "이미 존재하는 이메일입니다. 다시 입력해주세요."
                } else {
                    view.fragment_signup_layout_text_email.error = null
                    view.fragment_signup_layout_text_email.endIconMode = TextInputLayout.END_ICON_CUSTOM
                    view.fragment_signup_layout_text_email.endIconDrawable = resources.getDrawable(R.drawable.ic_check)
                }
            } else {
                if (task.exception?.message?.indexOf("The email address is badly formatted.") != -1) {
                    view.fragment_signup_layout_text_email.error = "잘못된 이메일 형식입니다. 다시 입력해주세요."
                }

            }
        }
    }

    private fun checkPassword(view : View) {
        if (view.fragment_signup_text_password_check.text.isNullOrEmpty()) {
            view.fragment_signup_layout_text_password_check.error = null
        } else {
            if (view.fragment_signup_text_password_check.text.toString() == view.fragment_signup_text_password.text.toString()) {
                view.fragment_signup_layout_text_password_check.error = null
            } else {
                view.fragment_signup_layout_text_password_check.error =  "비밀번호와 일치하지 않습니다."
                view.fragment_signup_layout_text_password_check.errorIconDrawable = null
            }
        }

    }
}