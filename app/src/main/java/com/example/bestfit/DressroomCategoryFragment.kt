package com.example.bestfit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
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
import com.example.bestfit.viewmodel.DataViewModel
import kotlinx.android.synthetic.main.fragment_dressroom_category.view.*
import kotlinx.android.synthetic.main.item_dressroom.view.*

class DressroomCategoryFragment : Fragment() {
    private val dataViewModel: DataViewModel by activityViewModels()
    private lateinit var accountViewModel: AccountFragmentViewModel

    private lateinit var itemRecyclerViewAdapter: ItemRecyclerViewAdapter
    private lateinit var layoutManager: RecyclerView.LayoutManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dressroom_category, container, false)

        initRecyclerView(view)

        when (parentFragment) {
            is AccountFragment -> initAccountViewModel()
            is DressroomFragment -> initDressroomViewModel(requireArguments().getInt("categoryIndex"))
            is DibsFragment -> initDibsViewModel()
        }

        return view
    }

    private fun initAccountViewModel() {
        if (requireArguments().getBoolean("isMyAccount", false)) {
            val allItemDTOsObserver = Observer<ArrayList<ArrayList<ItemDTO>>> { allItemDTOs ->
                itemRecyclerViewAdapter.submitList(allItemDTOs[0].map { it.copy() })
            }

            dataViewModel.allItemDTOs.observe(viewLifecycleOwner, allItemDTOsObserver)
            return
        }

        val itemDTOsObserver = Observer<ArrayList<ItemDTO>> { itemDTOs ->
            itemRecyclerViewAdapter.submitList(itemDTOs.map { it.copy() })
        }

        accountViewModel = ViewModelProvider(this.requireParentFragment()).get(AccountFragmentViewModel::class.java)
        accountViewModel.itemDTOs.observe(viewLifecycleOwner, itemDTOsObserver)
    }

    private fun initDressroomViewModel(categoryIndex: Int) {
        val allItemDTOsObserver = Observer<ArrayList<ArrayList<ItemDTO>>> { allItemDTOs ->
            itemRecyclerViewAdapter.submitList(allItemDTOs[categoryIndex].map { it.copy() })
        }

        dataViewModel.allItemDTOs.observe(viewLifecycleOwner, allItemDTOsObserver)
    }

    private fun initDibsViewModel() {
        val dibsItemDTOsObserver = Observer<ArrayList<ItemDTO>> { dibsItemDTOs ->
            itemRecyclerViewAdapter.submitList(dibsItemDTOs.map { it.copy() })
        }

        dataViewModel.dibsItemDTOs.observe(viewLifecycleOwner, dibsItemDTOsObserver)
    }

    private fun initRecyclerView(view: View) {
        itemRecyclerViewAdapter = ItemRecyclerViewAdapter()
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
            // 드레스룸에서 찜 표시 하려면 이거 수정해야겠다.
            return oldItem.timestamps!!.size == newItem.timestamps!!.size
        }
    }

    inner class ItemRecyclerViewAdapter: ListAdapter<ItemDTO, ItemRecyclerViewAdapter.ItemViewHolder>(DiffItemCallback()) {
        override fun onCurrentListChanged(
            previousList: MutableList<ItemDTO>,
            currentList: MutableList<ItemDTO>
        ) {
            if (previousList.size == 0 && currentList.size == 0)
                return

            super.onCurrentListChanged(previousList, currentList)

            val newItemSize = currentList.size
            if (previousList.size != 0 && previousList.size < newItemSize)
                layoutManager.scrollToPosition(0)
        }

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
                        R.id.dibsFragment -> DibsFragmentDirections.actionToDetailFragment(itemDTO)
                        else -> null
                    }

                    navController.navigate(action!!)
                }
            }
        }
    }
}