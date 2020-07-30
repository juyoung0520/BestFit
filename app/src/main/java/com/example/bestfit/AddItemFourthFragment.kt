package com.example.bestfit

import android.os.Bundle
import android.view.*
import androidx.core.view.get
import androidx.fragment.app.Fragment
import com.example.bestfit.model.SizeFormatDTO
import com.example.bestfit.util.InitData
import com.google.android.material.button.MaterialButton
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_add_item_fourth.view.*
import kotlinx.android.synthetic.main.fragment_add_item_third.*
import kotlinx.android.synthetic.main.fragment_add_item_third.view.*

class AddItemFourthFragment  : Fragment() {
    lateinit var fragmentView: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentView = inflater.inflate(R.layout.fragment_add_item_fourth, container, false)

        setHasOptionsMenu(true)

        fragmentView.fragment_add_item_fourth_text_review.setTextInputLayout(fragmentView.fragment_add_item_fourth_layout_text_review)

        return fragmentView
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.menu_add_item, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_add_item_action_add -> {
                submitAddItem()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun submitAddItem() {
        val addItemActivity = activity as AddItemActivity
        addItemActivity.submitAddItem()
    }
}