package com.example.bestfit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import androidx.fragment.app.Fragment
import com.example.bestfit.model.AccountDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_add_item_second.view.*
import kotlinx.android.synthetic.main.fragment_set_profile_second.*
import kotlinx.android.synthetic.main.fragment_set_profile_second.view.*
import kotlinx.android.synthetic.main.fragment_set_profile_third.view.*

class SetProfileThirdFragment : Fragment() {
    lateinit var fragmentView: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentView = inflater.inflate(R.layout.fragment_set_profile_third, container, false)

        fragmentView.fragment_set_profile_third_text_review.setTextInputLayout(fragmentView.fragment_set_profile_third_layout_text_review)

//        fragmentView.fragment_set_profile_third_btn_submit.setOnClickListener {
//            submitSetProfile()
//        }

        return fragmentView
    }

    private fun submitSetProfile() {
        val setProfileActivity = activity as SetProfileActivity
        setProfileActivity.submitSetProfile()
    }
}