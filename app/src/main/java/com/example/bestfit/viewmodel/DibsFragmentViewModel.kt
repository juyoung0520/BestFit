package com.example.bestfit.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DibsFragmentViewModel : ViewModel() {
    private val _isEditMode = MutableLiveData(false)
    val isEditMode: LiveData<Boolean> = _isEditMode

    private val _selectionItems = MutableLiveData<ArrayList<String>>(arrayListOf())
    val selectionItems: LiveData<ArrayList<String>> = _selectionItems

    var lastClickItem: String? = null

    fun isEditMode(): Boolean {
        return _isEditMode.value!!
    }

    fun setEditMode(isEditMode: Boolean) {
        _isEditMode.value = isEditMode
    }

    private fun notifySelectionItemsChanged() {
        _selectionItems.value = _selectionItems.value!!
    }

    fun getSelectionItems(): ArrayList<String> {
        return _selectionItems.value!!
    }

    fun clearSelectionItems() {
        _selectionItems.value!!.clear()
    }

    fun select(itemId: String) {
        lastClickItem = itemId

        _selectionItems.value!!.add(itemId)
        notifySelectionItemsChanged()
    }

    fun deselect(itemId: String) {
        lastClickItem = itemId

        _selectionItems.value!!.remove(itemId)
        notifySelectionItemsChanged()
    }

    fun isSelected(itemId: String): Boolean {
        return _selectionItems.value!!.contains(itemId)
    }
}
