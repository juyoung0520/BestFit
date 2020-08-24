package com.example.bestfit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.bestfit.model.ItemDTO
import com.example.bestfit.viewmodel.AccountFragmentViewModel
import com.example.bestfit.viewmodel.DressroomFragmentViewModel
import kotlinx.android.synthetic.main.fragment_dressroom_category.view.*
import kotlinx.android.synthetic.main.item_dressroom.view.*

class DressroomCategoryFragment : Fragment() {
    private lateinit var dressroomViewModel: DressroomFragmentViewModel
    private lateinit var accountViewModel: AccountFragmentViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dressroom_category, container, false)

        val position = requireArguments().getInt("position")
        if (position == -1)
            initAccountViewModel(view)
        else
            initDressroomViewModel(view, position)

        return view
    }

    private fun initAccountViewModel(view: View) {
        accountViewModel = ViewModelProvider(this.requireParentFragment()).get(AccountFragmentViewModel::class.java)
        initDressroomCategoryFragment(view, accountViewModel.itemDTOs.value!!)
    }

    private fun initDressroomViewModel(view: View, position: Int) {
        dressroomViewModel = ViewModelProvider(this.requireParentFragment()).get(DressroomFragmentViewModel::class.java)

        // diffutil 사용해야함. (추가, 삭제)
        val initObserver = Observer<Boolean> { isInit ->
            if (isInit) {
                initDressroomCategoryFragment(view, dressroomViewModel.itemDTOs.value!![position])
            }
        }

        dressroomViewModel.isInitialized.observe(viewLifecycleOwner, initObserver)
    }

    private fun initDressroomCategoryFragment(view: View, itemDTOs: ArrayList<ItemDTO>) {
//        view.fragment_dressroom_category_recyclerview.setHasFixedSize(true)
        val itemRecyclerViewAdapter = ItemRecyclerViewAdapter()
        view.fragment_dressroom_category_recyclerview.adapter = itemRecyclerViewAdapter
        view.fragment_dressroom_category_recyclerview.layoutManager = GridLayoutManager(activity, 2)
        itemRecyclerViewAdapter.submitList(itemDTOs)
    }

    inner class ItemRecyclerViewAdapter: ListAdapter<ItemDTO, RecyclerView.ViewHolder>(object : DiffUtil.ItemCallback<ItemDTO>() {
        override fun areItemsTheSame(
            oldItem: ItemDTO,
            newItem: ItemDTO
        ): Boolean {
            // itemDTO의 id 비교?
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: ItemDTO,
            newItem: ItemDTO
        ): Boolean {
            return oldItem == newItem
        }
    }) {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_dressroom, parent, false)
            return ItemViewHolder(view)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val viewHolder = holder as ItemViewHolder
            viewHolder.bind(getItem(position))
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