package com.example.bestfit

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.get
import androidx.fragment.app.*
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.selection.Selection
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.bestfit.model.ItemDTO
import com.example.bestfit.util.InitData
import com.example.bestfit.viewmodel.DataViewModel
import com.example.bestfit.viewmodel.DressroomFragmentViewModel
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_add_item.*
import kotlinx.android.synthetic.main.fragment_dressroom.*
import kotlinx.android.synthetic.main.fragment_dressroom.view.*

class DressroomFragment : Fragment() {
    private val dataViewModel: DataViewModel by activityViewModels()
    private lateinit var viewModel: DressroomFragmentViewModel

    private val fragments: ArrayList<Fragment> = arrayListOf()

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

        initViewModel(view)
        initToolbar(view, viewModel.isEditMode())
        initTabAdapter(view)

        return view
    }

    private fun initViewModel(view: View) {
        viewModel = ViewModelProvider(this).get(DressroomFragmentViewModel::class.java)
    }

    private fun initToolbar(view: View, isEditMode: Boolean = false) {
        if (isEditMode)
            viewModel.targetCategoryIndex = view.fragment_dressroom_viewpager.currentItem

        view.fragment_dressroom_toolbar.menu.clear()
        view.fragment_dressroom_tab.visibility = if (isEditMode) View.GONE else View.VISIBLE
        view.fragment_dressroom_viewpager.isUserInputEnabled = !isEditMode

        viewModel.setEditMode(isEditMode)

        if (isEditMode) {
            view.fragment_dressroom_toolbar.title = "삭제할 아이템을 선택하세요"
            view.fragment_dressroom_toolbar.setNavigationIcon(R.drawable.ic_close)
            view.fragment_dressroom_toolbar.setNavigationOnClickListener {
                viewModel.targetCategoryIndex = null
                view.fragment_dressroom_viewpager.adapter = TabOfCategoryPagerAdapter()
//                notifyItemChanged(view.fragment_dressroom_viewpager.currentItem)
//                findNavController().navigate(DressroomFragmentDirections.actionDressroomFragmentSelf())
//                view.fragment_dressroom_viewpager[view.fragment_dressroom_viewpager.currentItem].invalidate()
//                    .notifyItemChanged(view.fragment_dressroom_viewpager.currentItem)

                initToolbar(view)
            }
            view.fragment_dressroom_toolbar.inflateMenu(R.menu.menu_edit_mode)
            view.fragment_dressroom_toolbar.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.menu_edit_mode_remove -> {
                        // 삭제 처리
                        view.fragment_dressroom_viewpager.adapter!!.notifyItemChanged(viewModel.targetCategoryIndex!!)
                        viewModel.targetCategoryIndex = null
                        viewModel.setSelection(null)
                        println(viewModel.getSelection())
                        initToolbar(view)

                        true
                    }
                    else -> false
                }
            }

            return
        }

        view.fragment_dressroom_toolbar.title = "나의 드레스룸"
        view.fragment_dressroom_toolbar.navigationIcon = null
        view.fragment_dressroom_toolbar.inflateMenu(R.menu.menu_fragment_dressroom)
        view.fragment_dressroom_toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_fragment_dressroom_edit -> {
                    initToolbar(view, true)

                    true
                }
                R.id.menu_fragment_dressroom_add -> {
                    startForResult.launch(Intent(context, AddItemActivity::class.java))
                    true
                }
                else -> false
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

            fragments.add(fragment)
            return fragment
        }
    }
}