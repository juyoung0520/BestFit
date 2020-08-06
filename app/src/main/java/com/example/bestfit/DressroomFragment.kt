package com.example.bestfit

import android.app.Activity
import android.content.Intent
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.*
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.bestfit.model.CategoryDTO
import com.example.bestfit.model.ItemDTO
import com.example.bestfit.util.InitData
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_dressroom.view.*
import kotlinx.android.synthetic.main.fragment_dressroom_category.view.*
import kotlinx.android.synthetic.main.item_dressroom.view.*
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

        initToolbar(fragmentView)

        initTab()

        return fragmentView
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            initItem()
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
        val mainActivity: MainActivity = requireActivity() as MainActivity
        mainActivity.setToolbar(view.fragment_dressroom_toolbar)
    }

    private fun initTab() {
        timer(period = 200) {
            if (InitData.initialization) {
                cancel()

                requireActivity().runOnUiThread {
                    setHasOptionsMenu(true)

                    initAdapter()
                    initItem()
                }
            }
        }
    }

    private fun initItem() {
        itemDTOs.clear()

        for (i in 0 until InitData.categories.size)
            itemDTOs.add(arrayListOf())

        db.collection("accounts").document(currentUid).get().addOnCompleteListener {task ->
            if(task.isSuccessful) {
                if(task.result!!["items"] == null) {
                    initAdapter()

                    return@addOnCompleteListener
                }

                val items = task.result!!["items"] as ArrayList<String>
                var cnt = 0

                for (itemId in items) {
                    db.collection("items").document(itemId).get().addOnCompleteListener { task ->
                        if(task.isSuccessful) {
                            val itemDTO = task.result!!.toObject(ItemDTO::class.java)!!
                            val categoryIndex = InitData.getCategoryIndex(itemDTO.categoryId!!)

                            itemDTOs[0].add(itemDTO)
                            itemDTOs[categoryIndex].add(itemDTO)
                            cnt += 1

                            if (cnt >= items.size) {
                                for (itemDTO in itemDTOs)
                                    itemDTO.sortByDescending { itemDTO -> itemDTO.timestamp }

                                for (position in 0 until InitData.categories.size) {
                                    val bundle = Bundle()
                                    bundle.putParcelableArrayList("itemDTOs", itemDTOs[position])

                                    setFragmentResult("itemDTOs.$position", bundle)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun initAdapter() {
        fragmentView.fragment_dressroom_viewpager.adapter = TabOfCategoryPagerAdapter(requireActivity())
        TabLayoutMediator(fragmentView.fragment_dressroom_tab, fragmentView.fragment_dressroom_viewpager) { tab, position ->
            tab.text = InitData.categoryDTOs[position].name
        }.attach()
    }

    inner class TabOfCategoryPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
        override fun getItemCount(): Int {
            return InitData.categoryDTOs.size
        }

        override fun createFragment(position: Int): Fragment {
            val fragment = DressroomCategoryFragment()
            val bundle = Bundle()

            bundle.putInt("position", position)
            fragment.arguments = bundle

            return fragment
        }
    }
}