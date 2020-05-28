package com.example.bestfit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_dressroom.view.*


class DressroomFragment : Fragment() {
    val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dressroom, container, false)

        setTabOfCategory(view)

        return view
    }

    fun setTabOfCategory(view: View) {
        db.collection("categories").orderBy("index").get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                for (title in category)
                    view.fragment_dressroom_tab.addTab(view.fragment_dressroom_tab.newTab())

                view.fragment_dressroom_viewpager.adapter = TabOfCategoryPagerAdapter(childFragmentManager, category)
                view.fragment_dressroom_tab.setupWithViewPager(view.fragment_dressroom_viewpager)
            }
        }
    }

    inner class TabOfCategoryPagerAdapter(fm: FragmentManager, private val catergory: ArrayList<String>) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        override fun getItem(position: Int): Fragment {
            println("getItem $position")
            val fragment = DressroomCategoryFragment()
            return fragment
        }

        override fun getCount(): Int {
            return catergory.size
        }

        override fun getPageTitle(position: Int): CharSequence {
            return catergory[position]
        }
    }
}