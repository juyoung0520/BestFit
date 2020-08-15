package com.example.bestfit.viewmodel

import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bestfit.R
import kotlinx.android.synthetic.main.activity_main.*

class NavigationViewModel() : ViewModel() {
    private val _navigations = MutableLiveData<ArrayList<ArrayList<Fragment>>>(arrayListOf())
    val navigations: LiveData<ArrayList<ArrayList<Fragment>>> = _navigations

    private val _activatedNavigationIndex = MutableLiveData<Int>()
    val activatedNavigationIndex: LiveData<Int> = _activatedNavigationIndex

    private val _initailized = MutableLiveData<Boolean>()
    val initailized: LiveData<Boolean> = _initailized

    init {
        initNavigationFragments()
    }

    private fun notifyNavigationFragmentsChanged() {
        _navigations.value = _navigations.value
    }

    fun initNavigationFragments() {
        _activatedNavigationIndex.value = 0
        _navigations.value!!.clear()

        for (idx in 0 until 3)
            _navigations.value!!.add(arrayListOf())

        _initailized.value = true
        notifyNavigationFragmentsChanged()
    }

    fun getActivatedNavigationIndex() : Int {
        return _activatedNavigationIndex.value!!
    }

    fun getNavigationFragments() : ArrayList<ArrayList<Fragment>> {
        return _navigations.value!!
    }

    fun getNavigationFragments(navigationIndex: Int) : ArrayList<Fragment>? {
        if (navigationIndex < 0 || navigationIndex > _navigations.value?.size ?: 0)
            return null

        return _navigations.value!![navigationIndex]
    }

    fun addNavigationFragment(navigationIndex: Int, fragment: Fragment) {
        _navigations.value!![navigationIndex].add(fragment)
        notifyNavigationFragmentsChanged()
    }

    fun removeNavigationFragment(navigationIndex: Int, index: Int) {
        _navigations.value!![navigationIndex].removeAt(index)
        notifyNavigationFragmentsChanged()
    }

    fun changeActivatedNavigation(navigationIndex: Int) {
        _activatedNavigationIndex.value = navigationIndex
    }
}
