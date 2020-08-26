package com.example.bestfit

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.bestfit.model.ItemDTO
import com.example.bestfit.util.InitData
import com.example.bestfit.viewmodel.AddItemActivityViewModel
import kotlinx.android.synthetic.main.fragment_add_item_fourth.view.*
import kotlinx.android.synthetic.main.fragment_add_item_third.view.*

class AddItemFourthFragment : Fragment() {
    private lateinit var viewModel: AddItemActivityViewModel

    lateinit var fragmentView: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentView = inflater.inflate(R.layout.fragment_add_item_fourth, container, false)

        initViewModel(fragmentView)

        fragmentView.fragment_add_item_fourth_text_review.setOnFocusChangeListener { view, b ->
            fragmentView.fragment_add_item_fourth_error_review.visibility = View.GONE
        }

        fragmentView.fragment_add_item_fourth_rating.setOnRatingBarChangeListener { ratingBar, fl, b ->
            fragmentView.fragment_add_item_fourth_error_rating.visibility = View.GONE
        }

        return fragmentView
    }

    private fun initViewModel(view: View) {
        viewModel = ViewModelProvider(requireActivity()).get(AddItemActivityViewModel::class.java)

        val tempItemDTOObserver = Observer<ItemDTO> { tempItemDTO ->
            initTempCategory(view, tempItemDTO)
        }

        viewModel.tempItemDTO.observe(viewLifecycleOwner, tempItemDTOObserver)
    }

    private fun initTempCategory(view: View, tempItemDTO: ItemDTO) {
        view.fragment_add_item_fourth_rating.rating = tempItemDTO.ratingReview!!
        view.fragment_add_item_fourth_text_review.setText(tempItemDTO.review)
    }
}