package com.example.bestfit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.bestfit.model.AccountDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_set_profile_first.view.*

class SetProfileFirstFragment  : Fragment() {
    private val auth = FirebaseAuth.getInstance()
    private val currentUid = auth.currentUser!!.uid
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_set_profile_first, container, false)

//        view.fragment_set_profile_btn_submit.setOnClickListener {
//            setProfile(view)
//        }

        return view
    }

    private fun setProfile(view: View) {
        val accountDTO = AccountDTO()
//        accountDTO.nickname = view.fragment_set_profile_text_nickname.text.toString()
//        accountDTO.age
//        accountDTO.sex
//        accountDTO.height = view.fragment_set_profile_text_height.text.toString().toDouble()
//        accountDTO.weight = view.fragment_set_profile_text_weight.text.toString().toDouble()

        db.collection("accounts").document(currentUid).set(accountDTO).addOnCompleteListener { task ->
            if (task.isSuccessful) {
//                val setProfileActivity = activity as SetProfileActivity
//                setProfileActivity.replaceFragment(SetDetailProfileFragment())
            }
        }
    }
}