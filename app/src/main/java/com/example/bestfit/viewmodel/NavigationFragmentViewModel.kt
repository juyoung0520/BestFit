package com.example.bestfit.viewmodel

import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class NavigationFragmentViewModel : ViewModel() {
    private val navigationFragments = MutableLiveData<ArrayList<ArrayList<Fragment>>>()

    fun getNavigationFragments(index: Int) : ArrayList<Fragment>? {
        if (index < 0 || index > navigationFragments.value?.size ?: 0)
            return null

        return navigationFragments.value?.get(index) as ArrayList<Fragment>
    }

    fun addNavigationFragments() {
//    currentNavigation[newNavigationIndex].add(newFragment!!)

    }
}
