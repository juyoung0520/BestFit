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
import com.example.bestfit.viewmodel.DibsFragmentViewModel
import com.example.bestfit.viewmodel.DressroomFragmentViewModel
import kotlinx.android.synthetic.main.fragment_dressroom_category.view.*
import kotlinx.android.synthetic.main.item_dressroom.view.*

class DressroomCategoryFragment : Fragment() {
    private val dataViewModel: DataViewModel by activityViewModels()
    private lateinit var dressroomViewModel: DressroomFragmentViewModel
    private lateinit var accountViewModel: AccountFragmentViewModel
    private lateinit var dibsViewModel: DibsFragmentViewModel

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

    private fun initDressroomViewModel(categoryIndex: Int) {
        dressroomViewModel = ViewModelProvider(this.requireParentFragment()).get(DressroomFragmentViewModel::class.java)

        val isEditModeObserver = Observer<Boolean> { isEditMode ->
            if (dressroomViewModel.targetCategoryIndex != null && dressroomViewModel.targetCategoryIndex == categoryIndex) {
                val selectionItemsObserver = Observer<ArrayList<String>> { _ ->
                    itemRecyclerViewAdapter.submitList(dataViewModel.allItemDTOs.value!![categoryIndex].map { it.copy() })
                }

                if (isEditMode)
                    dressroomViewModel.selectionItems.observe(viewLifecycleOwner, selectionItemsObserver)
                else {
                    dressroomViewModel.targetCategoryIndex = null

                    dressroomViewModel.selectionItems.removeObservers(viewLifecycleOwner)
                    itemRecyclerViewAdapter.submitList(dataViewModel.allItemDTOs.value!![categoryIndex].map { it.copy() })

                    if (dataViewModel.getRemoveState() == DataViewModel.REMOVE_START) {
                        dataViewModel.removeItemDTOs(dressroomViewModel.getSelectionItems())
                    }
                }
            }
        }

        dressroomViewModel.isEditMode.observe(viewLifecycleOwner, isEditModeObserver)

        val allItemDTOsObserver = Observer<ArrayList<ArrayList<ItemDTO>>> { allItemDTOs ->
            itemRecyclerViewAdapter.submitList(allItemDTOs[categoryIndex].map { it.copy() })
        }

        dataViewModel.allItemDTOs.observe(viewLifecycleOwner, allItemDTOsObserver)
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

    private fun initDibsViewModel() {
        dibsViewModel = ViewModelProvider(this.requireParentFragment()).get(DibsFragmentViewModel::class.java)

        val isEditModeObserver = Observer<Boolean> { isEditMode ->
            val selectionItemsObserver = Observer<ArrayList<String>> { _ ->
                itemRecyclerViewAdapter.submitList(dataViewModel.dibsItemDTOs.value!!.map { it.copy() })
            }

            if (isEditMode)
                dibsViewModel.selectionItems.observe(viewLifecycleOwner, selectionItemsObserver)
            else {
                dibsViewModel.selectionItems.removeObservers(viewLifecycleOwner)
                itemRecyclerViewAdapter.submitList(dataViewModel.dibsItemDTOs.value!!.map { it.copy() })

                println("state = ${dataViewModel.getRemoveState()}")
                if (dataViewModel.getRemoveState() == DataViewModel.REMOVE_START) {
                    dataViewModel.removeDibsItem(dibsViewModel.getSelectionItems())
                }
            }
        }

        dibsViewModel.isEditMode.observe(viewLifecycleOwner, isEditModeObserver)

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

    inner class DiffItemCallback : DiffUtil.ItemCallback<ItemDTO>() {
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
            val navController = findNavController()
            when (navController.currentDestination!!.id) {
                R.id.dressroomFragment -> {
                    return if (dressroomViewModel.isEditMode())
                        dressroomViewModel.lastClickItem != newItem.id
                    else if (!dressroomViewModel.getSelectionItems().isNullOrEmpty())
                        !dressroomViewModel.isSelected(newItem.id!!)
                    else
                        oldItem.timestamps!!.size == newItem.timestamps!!.size
                }
                R.id.dibsFragment -> {
                    return if (dibsViewModel.isEditMode())
                        dibsViewModel.lastClickItem != newItem.id
                    else if (!dibsViewModel.getSelectionItems().isNullOrEmpty())
                        !dibsViewModel.isSelected(newItem.id!!)
                    else
                        oldItem.timestamps!!.size == newItem.timestamps!!.size
                }
            }

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
            val itemDTO = getItem(position)

            val navController = findNavController()
            when (navController.currentDestination!!.id) {
                R.id.dressroomFragment -> holder.bindEditMode(itemDTO, dressroomViewModel.isEditMode(), dressroomViewModel.isSelected(itemDTO.id!!))
                R.id.dibsFragment -> holder.bindEditMode(itemDTO, dibsViewModel.isEditMode(), dibsViewModel.isSelected(itemDTO.id!!))
                R.id.accountFragment -> holder.bind(itemDTO)
            }
        }

        inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            fun bindEditMode(itemDTO: ItemDTO, isEditMode: Boolean, isSelected: Boolean) {
                val view = itemView

                if (isEditMode)
                    view.item_dressroom_cardview.isChecked = isSelected
                else
                    view.item_dressroom_cardview.isChecked = false

                if (!itemDTO.images.isNullOrEmpty()) {
                    Glide.with(view)
                        .load(itemDTO.images!![0])
                        .apply(RequestOptions().centerCrop())
                        .into(view.item_dressroom_iv_item)
                }

                view.item_dressroom_tv_item_name.text = itemDTO.name

                view.setOnClickListener {
                    val navController = findNavController()
                    when (navController.currentDestination!!.id) {
                        R.id.dressroomFragment -> {
                            val isEditMode = dressroomViewModel.isEditMode()
                            val isSelected = dressroomViewModel.isSelected(itemDTO.id!!)

                            if (isEditMode) {
                                if (!isSelected)
                                    dressroomViewModel.select(itemDTO.id!!)
                                else
                                    dressroomViewModel.deselect(itemDTO.id!!)

                                return@setOnClickListener
                            }

                            val action = DressroomFragmentDirections.actionToDetailFragment(itemDTO)
                            navController.navigate(action)
                        }
                        R.id.dibsFragment -> {
                            val isEditMode = dibsViewModel.isEditMode()
                            val isSelected = dibsViewModel.isSelected(itemDTO.id!!)

                            if (isEditMode) {
                                if (!isSelected)
                                    dibsViewModel.select(itemDTO.id!!)
                                else
                                    dibsViewModel.deselect(itemDTO.id!!)

                                return@setOnClickListener
                            }

                            val action = DibsFragmentDirections.actionToDetailFragment(itemDTO)
                            navController.navigate(action)
                        }
                    }
                }
            }

            fun bind(itemDTO: ItemDTO) {
                val view = itemView

                Glide.with(view)
                    .load(itemDTO.images!![0])
                    .apply(RequestOptions().centerCrop())
                    .into(view.item_dressroom_iv_item)

                view.item_dressroom_tv_item_name.text = itemDTO.name

                view.setOnClickListener {
                    val navController = findNavController()
                    val action = AccountFragmentDirections.actionToDetailFragment(itemDTO)

                    navController.navigate(action)
                }
            }
        }
    }
}