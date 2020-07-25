package com.example.bestfit

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.bestfit.model.CategoryDTO
import com.example.bestfit.util.InitData
import com.jinu.imagepickerlib.PhotoPickerActivity
import com.jinu.imagepickerlib.utils.YPhotoPickerIntent
import kotlinx.android.synthetic.main.fragment_add_item_first.view.*
import kotlinx.android.synthetic.main.item_add_item_image.view.*

class AddItemFirstFragment  : Fragment() {
    lateinit var fragmentView: View
    var itemImages: ArrayList<String> = arrayListOf()
    private val ADD_IMAGE_CODE = 1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentView = inflater.inflate(R.layout.fragment_add_item_first, container, false)

        fragmentView.fragment_add_item_first_layout_add.setOnClickListener {
            addImage()
        }

        fragmentView.fragment_add_item_first_btn_submit.setOnClickListener {
            submitAddItem()
        }

        initCategory(fragmentView)

        return fragmentView
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        var images: ArrayList<String>? = null
        if (resultCode == RESULT_OK && requestCode == ADD_IMAGE_CODE) {
            if (data != null)
                images = data.getStringArrayListExtra(PhotoPickerActivity.KEY_SELECTED_PHOTOS)

            if (images != null) {
                initImageReyclerview(images)
            }
        }
    }

    private fun initCategory(view: View) {
        val categoryDTOs = InitData.categoryDTOs.subList(1, InitData.categoryDTOs.lastIndex + 1)
        val categories = InitData.categories.subList(1, InitData.categories.lastIndex + 1)
        val categoryAdapter = ArrayAdapter(context!!, R.layout.item_dropdown, categories)

        view.fragment_add_item_first_actv_category.setAdapter(categoryAdapter)
        view.fragment_add_item_first_actv_category.setOnFocusChangeListener { _, b ->
            if (b)
                view.fragment_add_item_first_layout_category.hint = ""
            else if (view.fragment_add_item_first_actv_category.text.isNullOrEmpty())
                view.fragment_add_item_first_layout_category.hint = "대분류"
        }
        view.fragment_add_item_first_actv_category.setOnItemClickListener { _, _, position, _ ->
            if (view.fragment_add_item_first_layout_divider_category.visibility == View.GONE) {
                view.fragment_add_item_first_layout_divider_category.visibility = View.VISIBLE
                view.fragment_add_item_first_layout_sub_category.visibility = View.VISIBLE
            }

            view.fragment_add_item_first_actv_category.tag = categoryDTOs[position]
            view.fragment_add_item_first_actv_sub_category.text = null
            initSubCategory(view, categoryDTOs[position].sub!!)
        }
    }

    private fun initSubCategory(view: View, subCategories: ArrayList<String>) {
        val categoryAdapter = ArrayAdapter(context!!, R.layout.item_dropdown, subCategories)

        view.fragment_add_item_first_actv_sub_category.setAdapter(categoryAdapter)
        view.fragment_add_item_first_actv_sub_category.setOnFocusChangeListener { _, b ->
            if (b)
                view.fragment_add_item_first_layout_sub_category.hint = ""
            else if (view.fragment_add_item_first_actv_sub_category.text.isNullOrEmpty())
                view.fragment_add_item_first_layout_sub_category.hint = "소분류"
        }
        view.fragment_add_item_first_actv_sub_category.setOnItemClickListener { _, _, position, _ ->
            val categoryDTO = view.fragment_add_item_first_actv_category.tag as CategoryDTO
            view.fragment_add_item_first_actv_sub_category.tag = categoryDTO.subId!![position]
        }
    }

    private fun initImageReyclerview(images: ArrayList<String>) {
        val view = fragmentView

        itemImages.clear()
        itemImages.addAll(images)

        view.fragment_add_item_first_recyclerview_image.setHasFixedSize(true)
        view.fragment_add_item_first_recyclerview_image.adapter = ImageRecyclerViewAdapter(itemImages)
        view.fragment_add_item_first_recyclerview_image.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)

//        while (view.fragment_add_item_first_recyclerview_image.itemDecorationCount > 0)
//            view.fragment_add_item_first_recyclerview_image.removeItemDecorationAt(0)

//        view.fragment_add_item_first_recyclerview_image.addItemDecoration(ItemDecoration())
    }

    private fun addImage() {
        val intent = YPhotoPickerIntent(activity)
        intent.setMaxSelectCount(10)
        intent.setShowCamera(true)
        intent.setShowGif(true)
        intent.setSelectCheckBox(false)
        intent.setMaxGrideItemCount(3)
        startActivityForResult(intent, ADD_IMAGE_CODE)
    }

    private fun submitAddItem() {
        val addItemActivity = activity as AddItemActivity
        addItemActivity.changeViewPage(false)
    }

    inner class ImageRecyclerViewAdapter(private var images: ArrayList<String>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_add_item_image, parent, false)

            return CustomViewHolder(view)
        }

        inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view)

        override fun getItemCount(): Int {
            return images.size
        }

        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val view = (holder as CustomViewHolder).itemView

            view.item_add_item_image_iv.clipToOutline = true
            Glide.with(view).load(images[position]).apply(RequestOptions().centerCrop()).into(view.item_add_item_image_iv)

            view.item_add_item_image_iv_delete.setOnClickListener {
                images.removeAt(position)
                notifyItemRemoved(position)
                notifyItemRangeChanged(position, images.size)
            }
        }
    }

    inner class ItemDecoration : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            super.getItemOffsets(outRect, view, parent, state)

            val position = parent.getChildAdapterPosition(view)
            val itemCount = state.itemCount
            val space = dpToPx(5)

            if (position != itemCount - 1)
                outRect.right = space
        }

        private fun dpToPx(dp: Int): Int {
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp.toFloat(),
                resources.displayMetrics
            ).toInt()
        }
    }
}