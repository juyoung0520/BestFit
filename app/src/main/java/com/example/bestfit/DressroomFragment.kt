package com.example.bestfit

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.bestfit.model.CategoryDTO
import com.example.bestfit.model.ItemDTO
import com.example.bestfit.util.InitData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_dressroom.view.*
import kotlin.concurrent.timer


class DressroomFragment : Fragment() {
    private val auth = FirebaseAuth.getInstance()
    private val currentUid = auth.currentUser!!.uid
    private val db = FirebaseFirestore.getInstance()
    private lateinit var fragmentView: View
    private val itemDTOs: ArrayList<ArrayList<ItemDTO>> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentView = inflater.inflate(R.layout.fragment_dressroom, container, false)

        setHasOptionsMenu(true)
        initToolbar(fragmentView)

        initTab(fragmentView)

        return fragmentView
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            initItem(fragmentView)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.menu_dressroom, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_dressroom_action_add -> {
                startActivityForResult(Intent(activity, AddItemActivity::class.java), 1)
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun initToolbar(view: View) {
        val mainActivity: MainActivity = activity!! as MainActivity
        mainActivity.setToolbar(view.fragment_dressroom_toolbar)
    }

    private fun initTab(view: View) {
        timer(period = 300) {
            if (InitData.initialization) {
                val categoryDTOs = InitData.categoryDTOs

                activity!!.runOnUiThread {
                    view.fragment_dressroom_tab.removeAllTabs()

                    for (i in categoryDTOs)
                        view.fragment_dressroom_tab.addTab(view.fragment_dressroom_tab.newTab())

                    initItem(view)
                    cancel()
                }
            }
        }
    }

    private fun initItem(view: View) {
        itemDTOs.clear()

        for (i in InitData.categories)
            itemDTOs.add(arrayListOf())

        db.collection("accounts").document(currentUid).get().addOnCompleteListener {task ->
            if(task.isSuccessful) {
                if(task.result!!["items"] == null)
                    return@addOnCompleteListener

                val items = task.result!!["items"] as ArrayList<String>
                var cnt = 0

                for (itemId in items) {
                    db.collection("items").document(itemId).get().addOnCompleteListener {task ->
                        if(task.isSuccessful) {
                            val itemDTO = task.result!!.toObject(ItemDTO::class.java)!!
                            val categoryIndex = InitData.getCategoryIndex(itemDTO.categoryId!!)

                            itemDTOs[0].add(itemDTO)
                            itemDTOs[categoryIndex].add(itemDTO)
                            cnt += 1

                            if (cnt >= items.size) {
                                for (itemDTO in itemDTOs)
                                    itemDTO.sortByDescending { itemDTO -> itemDTO.timestamp }

                                initAdapter()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun initAdapter() {
        fragmentView.fragment_dressroom_viewpager.adapter = TabOfCategoryPagerAdapter(childFragmentManager, InitData.categoryDTOs)
        fragmentView.fragment_dressroom_tab.setupWithViewPager(fragmentView.fragment_dressroom_viewpager)
    }

    inner class TabOfCategoryPagerAdapter(fm: FragmentManager, private val categoryDTOs: ArrayList<CategoryDTO>) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        override fun getItem(position: Int): Fragment {
            val fragment = DressroomCategoryFragment()
            val bundle = Bundle()

            bundle.putParcelableArrayList("itemDTOs", itemDTOs[position])
            fragment.arguments = bundle

            return fragment
        }

        override fun getCount(): Int {
            return categoryDTOs.size
        }

        override fun getPageTitle(position: Int): CharSequence {
            return categoryDTOs[position].name!!
        }
    }
}