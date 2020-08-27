package com.example.bestfit

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.example.bestfit.model.ItemDTO
import com.example.bestfit.viewmodel.DibsFragmentViewModel
import java.util.*
import kotlin.collections.ArrayList

class DibsFragment : Fragment() {
    private lateinit var viewModel: DibsFragmentViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
        ): View? {
        val view = inflater.inflate(R.layout.fragment_dibs, container, false)

        initViewModel(view)

        return view
    }

    private fun initViewModel(view: View) {
        viewModel = ViewModelProvider(this).get(DibsFragmentViewModel::class.java)

        val initItemDTOsObserver = Observer<ArrayList<ItemDTO>> {itemDTOs ->

        }

        viewModel.itemDTOs.observe(viewLifecycleOwner, initItemDTOsObserver)
    }

}