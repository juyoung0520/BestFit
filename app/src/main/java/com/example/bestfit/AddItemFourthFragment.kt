package com.example.bestfit

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_add_item_fourth.view.*

class AddItemFourthFragment : Fragment() {
    lateinit var fragmentView: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentView = inflater.inflate(R.layout.fragment_add_item_fourth, container, false)

        fragmentView.fragment_add_item_fourth_text_review.setTextInputLayout(fragmentView.fragment_add_item_fourth_layout_text_review)

        fragmentView.fragment_add_item_fourth_text_review.setOnFocusChangeListener { view, b ->
            fragmentView.fragment_add_item_fourth_error_review.visibility = View.GONE
        }

        fragmentView.fragment_add_item_fourth_rating.setOnRatingBarChangeListener { ratingBar, fl, b ->
            fragmentView.fragment_add_item_fourth_error_rating.visibility = View.GONE
        }

        return fragmentView
    }
}