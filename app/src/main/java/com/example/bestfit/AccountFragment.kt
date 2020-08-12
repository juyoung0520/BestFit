package com.example.bestfit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.setFragmentResult
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.bestfit.model.ItemDTO
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_account.*
import kotlinx.android.synthetic.main.fragment_account.view.*

class AccountFragment : Fragment() {
    private val auth = FirebaseAuth.getInstance()
    private val currentUid = auth.currentUser!!.uid
    private var uid : String ?= null
    private val db = FirebaseFirestore.getInstance()
    private val tabArray : ArrayList<String> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragment = inflater.inflate(R.layout.fragment_account, container, false)
        uid = arguments?.getString("uid")

        initTab(fragment)

        return fragment
    }

    private fun initTab(view : View) {
        view.fragment_account_tab.newTab()
        tabArray.add("드레스룸")
        view.fragment_account_tab.newTab()
        tabArray.add("게시글")

        initAdapter(view)
        initItem(view)
    }

    private fun initAdapter(view: View) {
        view.fragment_account_viewpager.adapter = TapOfPostPagerAdapter(requireActivity())
        TabLayoutMediator(view.fragment_account_tab, view.fragment_account_viewpager) { tab, position ->
            tab.text = tabArray[position]
        }.attach()
    }

    private fun initItem(view: View) {
        val itemDTOs : ArrayList<ItemDTO> = arrayListOf()

        db.collection("accounts").document(uid!!).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                if (task.result!!["items"] == null) {
                    initAdapter(view)

                    return@addOnCompleteListener
                }
                val items = task.result!!["items"] as ArrayList<String>
                var cnt = 0

                for (itemId in items) {
                    db.collection("items").document(itemId).get().addOnCompleteListener {
                        if (task.isSuccessful) {
                            val itemDTO = task.result!!.toObject(ItemDTO::class.java)
                            itemDTOs.add(itemDTO!!)
                            cnt += 1

                            if (cnt >= items.size) {
                                itemDTOs.sortByDescending { itemDTOs -> itemDTO.timestamp }

                                val bundle = Bundle()
                                bundle.putParcelableArrayList("itemDTOs", itemDTOs)

                                setFragmentResult("itemDTOs.0", bundle)
                            }
                        }
                    }
                }
            }
        }
    }

    inner class TapOfPostPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

        override fun getItemCount(): Int {
            return tabArray.size
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