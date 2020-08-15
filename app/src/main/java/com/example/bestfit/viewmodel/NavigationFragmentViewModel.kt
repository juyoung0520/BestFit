package com.example.bestfit.viewmodel

import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bestfit.R
import kotlinx.android.synthetic.main.activity_main.*

class NavigationFragmentViewModel() : ViewModel() {
    private val _navigationFragments = MutableLiveData<ArrayList<ArrayList<Fragment>>>(arrayListOf())
    val navigationFragments: LiveData<ArrayList<ArrayList<Fragment>>> = _navigationFragments

    private val _activatedNavigationIndex = MutableLiveData<Int>()
    val activatedNavigationIndex: LiveData<Int> = _activatedNavigationIndex

    private val _initailized = MutableLiveData<Boolean>()
    val initailized: LiveData<Boolean> = _initailized

    init {
        initNavigationFragments()
    }

    private fun notifyNavigationFragmentsChanged() {
        _navigationFragments.value = _navigationFragments.value
    }

    fun initNavigationFragments() {
        _activatedNavigationIndex.value = 0
        _navigationFragments.value!!.clear()

        for (idx in 0 until 3)
            _navigationFragments.value!!.add(arrayListOf())

        _initailized.value = true
        notifyNavigationFragmentsChanged()
    }

    fun getActivatedNavigationIndex() : Int {
        return _activatedNavigationIndex.value!!
    }

    fun getNavigationFragments() : ArrayList<ArrayList<Fragment>> {
        return _navigationFragments.value!!
    }

    fun getNavigationFragments(navigationIndex: Int) : ArrayList<Fragment>? {
        if (navigationIndex < 0 || navigationIndex > _navigationFragments.value?.size ?: 0)
            return null

        return _navigationFragments.value!![navigationIndex]
    }

    fun addNavigationFragment(navigationIndex: Int, fragment: Fragment) {
        _navigationFragments.value!![navigationIndex].add(fragment)
        notifyNavigationFragmentsChanged()
    }

    fun removeNavigationFragment(navigationIndex: Int, index: Int) {
        _navigationFragments.value!![navigationIndex].removeAt(index)
        notifyNavigationFragmentsChanged()
    }

    fun changeActivatedNavigation(navigationIndex: Int) {
        _activatedNavigationIndex.value = navigationIndex
    }
}
