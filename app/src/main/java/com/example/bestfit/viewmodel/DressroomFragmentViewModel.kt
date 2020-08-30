package com.example.bestfit.viewmodel

import androidx.lifecycle.*
import androidx.recyclerview.selection.Selection
import com.example.bestfit.model.AccountDTO
import com.example.bestfit.model.ItemDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class DressroomFragmentViewModel: ViewModel() {
    private val _isEditMode = MutableLiveData(false)
    val isEditMode: LiveData<Boolean> = _isEditMode

    private val _selection = MutableLiveData<Selection<String>?>()

    var targetCategoryIndex: Int? = null

    fun isEditMode(): Boolean {
        return _isEditMode.value!!
    }

    fun setEditMode(isEditMode: Boolean) {
        _isEditMode.value = isEditMode
    }

    fun setSelection(selection: Selection<String>?) {
        _selection.value = selection
    }

    fun getSelection(): Selection<String>? {
        return _selection.value
    }
}
