package com.example.bestfit

import android.os.Bundle
import android.view.*
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

        setHasOptionsMenu(true)

        fragmentView.fragment_set_profile_third_text_review.setTextInputLayout(fragmentView.fragment_set_profile_third_layout_text_review)

        return fragmentView
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.menu_set_profile, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_set_profile_action_submit -> {
                submitSetProfile()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun submitSetProfile() {
        val setProfileActivity = activity as SetProfileActivity
        setProfileActivity.submitSetProfile()
    }
}