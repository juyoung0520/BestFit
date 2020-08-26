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
import com.example.bestfit.viewmodel.DressroomCategoryFragmentViewModel
import com.example.bestfit.viewmodel.DressroomFragmentViewModel
import kotlinx.android.synthetic.main.fragment_dressroom_category.view.*
import kotlinx.android.synthetic.main.item_dressroom.view.*

class DressroomCategoryFragment : Fragment() {
    private lateinit var viewModel: DressroomCategoryFragmentViewModel
    private lateinit var dressroomViewModel: DressroomFragmentViewModel
    private lateinit var accountViewModel: AccountFragmentViewModel

    private lateinit var itemRecyclerViewAdapter: ItemRecyclerViewAdapter
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private var itemCount = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dressroom_category, container, false)

        initRecyclerView(view)

        val position = requireArguments().getInt("position")
        if (position == -1)
            initAccountViewModel(view)
        else
            initDressroomViewModel(view, position)

        return view
    }

    private fun initAccountViewModel(view: View) {
        accountViewModel = ViewModelProvider(this.requireParentFragment()).get(AccountFragmentViewModel::class.java)
//        initDressroomCategoryFragment(view, accountViewModel.itemDTOs.value!!)

        val initObserver = Observer<Boolean> { isInit ->
            if (isInit) {
//                initDressroomCategoryFragment(view, accountViewModel.itemDTOs.value!!)
            }
        }

        accountViewModel.isInitialized.observe(viewLifecycleOwner, initObserver)
    }

    private fun initDressroomViewModel(view: View, position: Int) {
        dressroomViewModel = ViewModelProvider(requireParentFragment()).get(DressroomFragmentViewModel::class.java)

        val itemDTOsObserver = Observer<ArrayList<ArrayList<ItemDTO>>> { itemDTOs ->
            if (itemDTOs[position].isNullOrEmpty()) {
                println("itemDTOs is empty")
                return@Observer
            }

            println("observer! ${itemDTOs[position].size}")

            val newItemSize = itemDTOs[position].size
            if (itemCount == newItemSize)
                return@Observer

            if (itemCount != 0 && itemCount < newItemSize)
                view.fragment_dressroom_category_recyclerview.smoothScrollToPosition(0)
            // 스크롤이 맨 아이템까지 안가고 하나 아래까지만 감 ㅠㅠ

            itemCount = newItemSize
            itemRecyclerViewAdapter.submitList(itemDTOs[position].map { it.copy() })
        }

        dressroomViewModel.itemDTOs.observe(requireParentFragment().viewLifecycleOwner, itemDTOsObserver)
    }

    private fun initRecyclerView(view: View) {
        viewModel = ViewModelProvider(this).get(DressroomCategoryFragmentViewModel::class.java)

        if (viewModel.getItemRecyclerViewAdapter() == null) {
            itemRecyclerViewAdapter = ItemRecyclerViewAdapter()
            viewModel.setItemRecyclerViewAdapter(itemRecyclerViewAdapter)
        }
        else
            itemRecyclerViewAdapter = viewModel.getItemRecyclerViewAdapter()!!

        // 스크롤 바꾸려고 한건데 안 쓰면 필요없움
        layoutManager = GridLayoutManager(context, 2)
        view.fragment_dressroom_category_recyclerview.adapter = itemRecyclerViewAdapter
        view.fragment_dressroom_category_recyclerview.layoutManager = layoutManager
    }

    class DiffItemCallback : DiffUtil.ItemCallback<ItemDTO>() {
        override fun areItemsTheSame(
            oldItem: ItemDTO,
            newItem: ItemDTO
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: ItemDTO,
            newItem: ItemDTO
        ): Boolean {
            return oldItem.id == newItem.id
        }
    }

    inner class ItemRecyclerViewAdapter: ListAdapter<ItemDTO, ItemRecyclerViewAdapter.ItemViewHolder>(DiffItemCallback()) {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_dressroom, parent, false)
            return ItemViewHolder(view)
        }

        override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
            holder.bind(getItem(position))
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