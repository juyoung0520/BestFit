
package com.example.bestfit

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.bestfit.model.ItemDTO
import com.example.bestfit.viewmodel.AddItemActivityViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_add_item.*
import kotlinx.android.synthetic.main.fragment_add_item_first.view.*
import kotlinx.android.synthetic.main.fragment_add_item_fourth.view.*
import kotlinx.android.synthetic.main.fragment_add_item_second.view.*
import kotlinx.android.synthetic.main.fragment_add_item_third.view.*
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class AddItemActivity : AppCompatActivity() {
    private lateinit var viewModel: AddItemActivityViewModel

    private val auth = FirebaseAuth.getInstance()
    private val currentUid = auth.currentUser!!.uid
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val fragments = arrayListOf<Fragment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_item)

        initViewModel()
        initToolbar()
        initViewPager()
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this).get(AddItemActivityViewModel::class.java)

        val initializedObserver = Observer<Boolean> { initialized ->
            if (initialized) {
                val tempItemDTO = intent.getParcelableExtra<ItemDTO>("tempItemDTO")
                if (tempItemDTO == null)
                    viewModel.setTempItemDTO(ItemDTO())
                else {
                    activity_add_item_tv_toolbar_title.text = "아이템 수정"
                    viewModel.setTempItemDTO(tempItemDTO)
                }
            }
        }

        val itemDTOObserver = Observer<ItemDTO> { itemDTO ->
            val tempItemDTO = intent.getParcelableExtra<ItemDTO>("tempItemDTO")
            if (tempItemDTO == null)
                setResult(RESULT_OK, Intent().putExtra("itemDTO", itemDTO))
            else
                setResult(RESULT_OK, Intent().putExtra("tempItemDTO", tempItemDTO))

            finish()
        }

        viewModel.initialized.observe(this, initializedObserver)
        viewModel.itemDTO.observe(this, itemDTOObserver)
    }

    private fun initToolbar() {
        activity_add_item_toolbar.setNavigationOnClickListener {
            changeViewPage(true)
        }
    }

    private fun initViewPager() {
        activity_add_item_viewpager.offscreenPageLimit = 3
        activity_add_item_viewpager.adapter = AddItemFragmentPagerAdapter(this, 4)
        activity_add_item_viewpager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(activity_add_item_viewpager.windowToken, 0)

                when (position) {
                    0 -> {
                        activity_add_item_toolbar.setNavigationIcon(R.drawable.ic_close)
                        activity_add_item_toolbar.menu.clear()
                    }
                    1, 2 -> {
                        activity_add_item_toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
                        activity_add_item_toolbar.menu.clear()
                    }
                    3 -> {
                        activity_add_item_toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
                        activity_add_item_toolbar.inflateMenu(R.menu.menu_activity_add_item)
                        activity_add_item_toolbar.setOnMenuItemClickListener { item ->
                            when (item.itemId) {
                                R.id.menu_activity_add_item_submit -> {
                                    if (emptyCheckAddItem())
                                        submitAddItem()

                                    true
                                }
                                else -> false
                            }
                        }
                    }
                }
            }
        })
        activity_add_item_indicator.setViewPager(activity_add_item_viewpager)
    }

    inner class AddItemFragmentPagerAdapter(
        fragmentActivity: FragmentActivity,
        private val fragmentSize: Int
    ) : FragmentStateAdapter(fragmentActivity) {
        override fun getItemCount(): Int {
            return fragmentSize
        }

        override fun createFragment(position: Int): Fragment {
            val fragment = when (position) {
                0 -> AddItemFirstFragment()
                1 -> AddItemSecondFragment()
                2 -> AddItemThirdFragment()
                3 -> AddItemFourthFragment()
                else -> Fragment()
            }

            fragments.add(fragment)
            return fragment
        }
    }

    fun changeViewPage(isPrev: Boolean, position: Int = 1) {
        var newPosition = activity_add_item_viewpager.currentItem
        if (isPrev)
            newPosition -= position
        else
            newPosition += position

        if (newPosition < 0) {
            finish()
            return
        }
        else if (newPosition > activity_add_item_viewpager.adapter!!.itemCount)
            return

        activity_add_item_viewpager.currentItem = newPosition
    }

    private fun changeViewPage(position: Int) {
        if (position < 0) {
            finish()
            return
        }
        else if (position > activity_add_item_viewpager.adapter!!.itemCount)
            return

        activity_add_item_viewpager.currentItem = position
    }

    fun submitAddItem() {
        val tempItemDTO = viewModel.tempItemDTO.value!!
        if (tempItemDTO.id != null) {
            submitModifyItem()

            return
        }

        val firstFragment = fragments[0] as AddItemFirstFragment
        val firstFragmentView = firstFragment.fragmentView
        val secondFragment = fragments[1] as AddItemSecondFragment
        val secondFragmentView = secondFragment.fragmentView
        val thirdFragment = fragments[2] as AddItemThirdFragment
        val thirdFragmentView = thirdFragment.fragmentView
        val fourthFragment = fragments[3] as AddItemFourthFragment
        val fourthFragmentView = fourthFragment.fragmentView

        val itemDTO = ItemDTO()
        itemDTO.timestamps!!.add(System.currentTimeMillis())
        itemDTO.uid = currentUid
        itemDTO.categoryId = tempItemDTO.categoryId
        itemDTO.subCategoryId = tempItemDTO.subCategoryId
//        itemDTO.brandId
        itemDTO.name = secondFragmentView.fragment_add_item_second_text_item_name.text.toString()
        itemDTO.sizeFormatId = tempItemDTO.sizeFormatId
        itemDTO.sizeId = tempItemDTO.sizeId
        itemDTO.sizeReview = tempItemDTO.sizeReview
        itemDTO.ratingReview = fourthFragmentView.fragment_add_item_fourth_rating.rating
        itemDTO.review = fourthFragmentView.fragment_add_item_fourth_text_review.text.toString()
        itemDTO.searchKeywords = getSearchKeywords(itemDTO.name.toString())

        val imageUris = arrayListOf<Uri>()
        for (image in firstFragment.itemImages) {
            val uri = FileProvider.getUriForFile(this, "com.jinu.imagepickerlib.fileprovider", File(image))
            imageUris.add(uri)
        }

        viewModel.submitAddItem(itemDTO, imageUris)
    }

    private fun submitModifyItem() {
        val secondFragment = fragments[1] as AddItemSecondFragment
        val secondFragmentView = secondFragment.fragmentView
        val fourthFragment = fragments[3] as AddItemFourthFragment
        val fourthFragmentView = fourthFragment.fragmentView

        val tempItemDTO = viewModel.tempItemDTO.value!!
        tempItemDTO.timestamps!!.add(System.currentTimeMillis())
        tempItemDTO.name = secondFragmentView.fragment_add_item_second_text_item_name.text.toString()
        tempItemDTO.ratingReview = fourthFragmentView.fragment_add_item_fourth_rating.rating
        tempItemDTO.review = fourthFragmentView.fragment_add_item_fourth_text_review.text.toString()
        tempItemDTO.searchKeywords = getSearchKeywords(tempItemDTO.name.toString())

//        val imageUris = arrayListOf<Uri>()
//        for (image in firstFragment.itemImages) {
//            val uri = FileProvider.getUriForFile(this, "com.jinu.imagepickerlib.fileprovider", File(image))
//            imageUris.add(uri)
//        }

        viewModel.submitModifyItem(tempItemDTO)
    }

    private fun getSearchKeywords(itemName: String): ArrayList<String> {
        var nameString = itemName.toLowerCase(Locale.ROOT)
        val words = nameString.split(" ")
        val keywords = ArrayList<String>()

        for (word in words) {
            var appendString = ""

            for (position in nameString.indices) {
                appendString += nameString[position]
                keywords.add(appendString)
            }

            nameString = nameString.replace("$word ", "")
        }

        return keywords
    }

    fun emptyCheckAddItem(): Boolean {
        val firstFragment = fragments[0] as AddItemFirstFragment
        val firstFragmentView = firstFragment.fragmentView
        val secondFragment = fragments[1] as AddItemSecondFragment
        val secondFragmentView = secondFragment.fragmentView
        val thirdFragment = fragments[2] as AddItemThirdFragment
        val thirdFragmentView = thirdFragment.fragmentView
        val fourthFragment = fragments[3] as AddItemFourthFragment
        val fourthFragmentView = fourthFragment.fragmentView

        // firstFragment
        if (viewModel.tempItemDTO.value!!.categoryId == null || viewModel.tempItemDTO.value!!.subCategoryId == null) {
            changeViewPage(0)
            firstFragmentView.fragment_add_item_first_error_category.visibility = View.VISIBLE

            return false
        }

        if (firstFragment.itemImages.size == 0) {
            changeViewPage(0)
            firstFragmentView.fragment_add_item_first_error_image.visibility = View.VISIBLE

            return false
        }

        // secondFragment
        // tempitemdto로 변경 필요
        if (secondFragmentView.fragment_add_item_second_actv_brand.text.isNullOrEmpty()) {
            changeViewPage(1)
            secondFragmentView.fragment_add_item_second_error_brand.visibility = View.VISIBLE

            return false
        }

        if (secondFragmentView.fragment_add_item_second_text_item_name.text.isNullOrEmpty()) {
            changeViewPage(1)
            secondFragmentView.fragment_add_item_second_error_item_name.visibility = View.VISIBLE

            return false
        }

        // thirdFragment
        if (viewModel.tempItemDTO.value!!.sizeFormatId == null || viewModel.tempItemDTO.value!!.sizeId == null) {
            changeViewPage(2)
            thirdFragmentView.fragment_add_item_third_error_size.visibility = View.VISIBLE

            return false
        }

        if (viewModel.tempItemDTO.value!!.sizeReview == null) {
            changeViewPage(2)
            thirdFragmentView.fragment_add_item_third_error_size_review.visibility = View.VISIBLE

            return false
        }

        // fourthFragment
        if (fourthFragmentView.fragment_add_item_fourth_rating.rating.equals(0.toFloat())) {
            fourthFragmentView.fragment_add_item_fourth_error_rating.visibility = View.VISIBLE

            return false
        }

        if (fourthFragmentView.fragment_add_item_fourth_text_review.text.isNullOrEmpty()) {
            fourthFragmentView.fragment_add_item_fourth_error_review.visibility = View.VISIBLE

            return false
        }

        return true
    }
}