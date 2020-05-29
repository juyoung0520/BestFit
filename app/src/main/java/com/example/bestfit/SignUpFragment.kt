package com.example.bestfit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_signup.view.*

class SignupFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_signup, container, false)

        view.fragment_signup_btn_next.setOnClickListener{

            (activity as SignUpActivity).supportFragmentManager.beginTransaction().replace(R.id.activity_signup_layout_frame, ProfileFragment()).commit()
        }

        return view
    }
}