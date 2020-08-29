package com.example.bestfit

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.*
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.bestfit.model.ItemDTO
import com.example.bestfit.util.InitData
import com.example.bestfit.viewmodel.DataViewModel
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.fragment_dressroom.view.*

class DressroomFragment : Fragment() {
    private val dataViewModel: DataViewModel by activityViewModels()

    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val itemDTO = result.data!!.getParcelableExtra<ItemDTO>("itemDTO")!!
            dataViewModel.addItemDTO(itemDTO)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dressroom, container, false)

        initToolbar(view)
        initTabAdapter(view)

        return view
    }

    private fun initToolbar(view: View) {
        view.fragment_dressroom_toolbar.inflateMenu(R.menu.menu_fragment_dressroom)
        view.fragment_dressroom_toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_fragment_dressroom_add -> {
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
        view.fragment_dressroom_viewpager.offscreenPageLimit = InitData.categoryDTOs.size
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

            bundle.putInt("categoryIndex", position)

            fragment.arguments = bundle
            return fragment
        }
    }
}