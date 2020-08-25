package com.example.bestfit

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.*
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.bestfit.model.ItemDTO
import com.example.bestfit.util.InitData
import com.example.bestfit.viewmodel.DressroomFragmentViewModel
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_dressroom.view.*

class DressroomFragment : Fragment() {
    private lateinit var viewModel: DressroomFragmentViewModel

    private val auth = FirebaseAuth.getInstance()
    private val currentUid = auth.currentUser!!.uid

    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val itemDTO = result.data!!.getParcelableExtra<ItemDTO>("itemDTO")!!
            viewModel.addItemDTO(itemDTO)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dressroom, container, false)

        initViewModel()
        initToolbar(view)
        initTabAdapter(view)

        return view
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
////            initItem()
//        }
//    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this).get(DressroomFragmentViewModel::class.java)

        val initObserver = Observer<Boolean> { isInit ->
            if (isInit) {
                // 로딩 여기다가 넣음 될 듯??
//                initDressroomFragment()
            }
        }

        viewModel.isInitialized.observe(viewLifecycleOwner, initObserver)
    }

    private fun initToolbar(view: View) {
        view.fragment_dressroom_toolbar.inflateMenu(R.menu.menu_fragment_dressroom)
        view.fragment_dressroom_toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_fragment_dressroom_add -> {
//                    startActivityForResult(Intent(activity, AddItemActivity::class.java), 1)
                    startForResult.launch(Intent(context, AddItemActivity::class.java))

                    true
                }
                else -> {
                    false
                }
            }
        }
    }

    private fun initTabAdapter(view: View) {
        view.fragment_dressroom_viewpager.adapter = TabOfCategoryPagerAdapter()
        TabLayoutMediator(view.fragment_dressroom_tab, view.fragment_dressroom_viewpager) { tab, position ->
            tab.text = InitData.categoryDTOs[position].name
        }.attach()
    }

    inner class TabOfCategoryPagerAdapter : FragmentStateAdapter(this) {
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