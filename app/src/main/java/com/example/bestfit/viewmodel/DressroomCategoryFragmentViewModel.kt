package com.example.bestfit.viewmodel

import androidx.lifecycle.*
import com.example.bestfit.DressroomCategoryFragment

class DressroomCategoryFragmentViewModel : ViewModel() {
    private val _itemRecyclerViewAdapter = SingleLiveEvent<DressroomCategoryFragment.ItemRecyclerViewAdapter>()

    fun setItemRecyclerViewAdapter(adapter: DressroomCategoryFragment.ItemRecyclerViewAdapter) {
        _itemRecyclerViewAdapter.value = adapter
    }

    fun getItemRecyclerViewAdapter(): DressroomCategoryFragment.ItemRecyclerViewAdapter? {
        return _itemRecyclerViewAdapter.value
    }
}
