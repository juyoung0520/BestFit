package com.example.bestfit

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.bestfit.model.ItemDTO
import com.example.bestfit.viewmodel.DibsFragmentViewModel
import com.example.bestfit.viewmodel.DressroomCategoryFragmentViewModel
import kotlinx.android.synthetic.main.fragment_dressroom_category.view.*
import kotlinx.android.synthetic.main.item_dressroom.view.*
import java.util.*
import kotlin.collections.ArrayList

class DibsFragment : Fragment() {
    private lateinit var viewModel: DibsFragmentViewModel
    private lateinit var dressroomCategoryFragmentViewModel: DressroomCategoryFragmentViewModel

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

//    private fun initRecyclerView(view: View) {
//        dressroomCategoryFragmentViewModel = ViewModelProvider(this).get(DressroomCategoryFragmentViewModel::class.java)
//
//        if (dressroomCategoryFragmentViewModel.getItemRecyclerViewAdapter() == null) {
//            itemRecyclerViewAdapter = ItemRecyclerViewAdapter()
//            dressroomCategoryFragmentViewModel.setItemRecyclerViewAdapter(itemRecyclerViewAdapter)
//        }
//        else
//            itemRecyclerViewAdapter = dressroomCategoryFragmentViewModel.getItemRecyclerViewAdapter()!!
//
//        layoutManager = GridLayoutManager(context, 2)
//        view.fragment_dressroom_category_recyclerview.adapter = itemRecyclerViewAdapter
//        view.fragment_dressroom_category_recyclerview.layoutManager = layoutManager
//    }

    inner class ItemRecyclerViewAdapter: ListAdapter<ItemDTO, ItemRecyclerViewAdapter.ItemViewHolder> {

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): ItemRecyclerViewAdapter.ItemViewHolder {
            TODO("Not yet implemented")
        }

        override fun onBindViewHolder(
            holder: ItemRecyclerViewAdapter.ItemViewHolder,
            position: Int
        ) {
            TODO("Not yet implemented")
        }

        inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            fun bind(itemDTO: ItemDTO) {
                val view = itemView

                Glide.with(view)
                    .load(itemDTO.images!![0])
                    .apply(RequestOptions().centerCrop())
                    .into(view.item_dressroom_iv_item)

                view.item_dressroom_tv_item_name.text = itemDTO.name
                view.setOnClickListener {
                    val navController = findNavController()
                    val action = when (navController.currentDestination!!.id) {
                        R.id.dressroomFragment -> DressroomFragmentDirections.actionToDetailFragment(itemDTO)
                        R.id.accountFragment -> AccountFragmentDirections.actionToDetailFragment(itemDTO)
                        else -> null
                    }

                    navController.navigate(action!!)
                }
            }
        }
    }

}