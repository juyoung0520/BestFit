package com.example.bestfit.viewmodel

import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class NavigationFragmentViewModel : ViewModel() {
    private val navigationFragments = MutableLiveData<ArrayList<ArrayList<Fragment>>>()
    private var currentNavigationIndex = MutableLiveData<Int>(0)

    fun getNavigationFragments(navigationIndex: Int) : ArrayList<Fragment>? {
        if (navigationIndex < 0 || navigationIndex > navigationFragments.value?.size ?: 0)
            return null

        return navigationFragments.value!![navigationIndex]
    }

    fun addNavigationFragment(navigationIndex: Int, fragment: Fragment) {
        navigationFragments.value!![navigationIndex].add(fragment)
    }

    fun removeNavigationFragment(navigationIndex: Int, index: Int) {
        navigationFragments.value!![navigationIndex].removeAt(index)
    }

    fun changeCurrentNavigation(navigationIndex: Int) {
        currentNavigationIndex.value = navigationIndex
    }
}
