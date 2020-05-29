package com.example.bestfit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.bestfit.model.CategoryDTO
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
                var categories = arrayListOf<CategoryDTO>()

                for (snapshot in task.result!!) {
                    val categoryDTO = snapshot.toObject(CategoryDTO::class.java)
                    categories.add(categoryDTO)

                    view.fragment_dressroom_tab.addTab(view.fragment_dressroom_tab.newTab())
                }

                view.fragment_dressroom_viewpager.adapter = TabOfCategoryPagerAdapter(childFragmentManager, categories)
                view.fragment_dressroom_tab.setupWithViewPager(view.fragment_dressroom_viewpager)
            }
        }
    }

    inner class TabOfCategoryPagerAdapter(fm: FragmentManager, private val categories: ArrayList<CategoryDTO>) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        override fun getItem(position: Int): Fragment {
            val fragment = DressroomCategoryFragment()
            return fragment
        }

        override fun getCount(): Int {
            return categories.size
        }

        override fun getPageTitle(position: Int): CharSequence {
            return categories[position].name.toString()
        }
    }
}