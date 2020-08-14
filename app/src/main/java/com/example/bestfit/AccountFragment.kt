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
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_account.*
import kotlinx.android.synthetic.main.fragment_account.view.*
import kotlinx.android.synthetic.main.fragment_detail.view.*
import kotlin.math.abs

class AccountFragment : Fragment() {
    private val auth = FirebaseAuth.getInstance()
    private val currentUid = auth.currentUser!!.uid
    private val db = FirebaseFirestore.getInstance()
    private val tabArray: ArrayList<String> = arrayListOf()
    private lateinit var uid: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_account, container, false)
        uid = requireArguments().getString("uid")!!

        initToolbar(view)
        initTab(view)

        return view
    }

    private fun initToolbar(view: View) {
        view.fragment_account_appbarlayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { p0, p1 ->
            if (abs(p1) - p0.totalScrollRange == 0) {
                view.fragment_account_toolbar_title.visibility = View.VISIBLE

            }
            else if (p1 == 0) {
                view.fragment_account_toolbar_title.visibility = View.GONE
            }
        })

        view.fragment_account_toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        view.fragment_account_toolbar.setNavigationOnClickListener {
            val mainActivity: MainActivity = requireActivity() as MainActivity
            mainActivity.changeFragment(null, null, true)
        }
    }

    private fun initTab(view : View) {
        tabArray.add("드레스룸")
        tabArray.add("게시글")

        initTabAdapter(view)
        initItem()
    }

    private fun initItem() {
        val itemDTOs : ArrayList<ItemDTO> = arrayListOf()

        db.collection("accounts").document(uid).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                if (task.result!!["items"] == null)
                    return@addOnCompleteListener

                val items = task.result!!["items"] as ArrayList<String>
                var cnt = 0

                for (itemId in items) {
                    db.collection("items").document(itemId).get().addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val itemDTO = task.result!!.toObject(ItemDTO::class.java)!!
                            itemDTOs.add(itemDTO)

                            cnt += 1

                            if (cnt >= items.size) {
                                itemDTOs.sortByDescending { itemDTO -> itemDTO.timestamp }

                                val bundle = Bundle()
                                bundle.putParcelableArrayList("itemDTOs", itemDTOs)

                                setFragmentResult("itemDTOs.${uid}.-1", bundle)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun initTabAdapter(view: View) {
        view.fragment_account_viewpager.adapter = TabPagerAdapter(requireActivity())
        TabLayoutMediator(view.fragment_account_tab, view.fragment_account_viewpager) { tab, position ->
            tab.text = tabArray[position]
        }.attach()
    }

    inner class TabPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
        override fun getItemCount(): Int {
            return tabArray.size
        }

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> {
                    val fragment = DressroomCategoryFragment()
                    val bundle = Bundle()

                    bundle.putString("uid", uid)
                    bundle.putInt("position", -1)

                    fragment.arguments = bundle
                    fragment
                }
                1 -> {
                    Fragment()
                }
                else -> Fragment()
            }
        }

    }

}