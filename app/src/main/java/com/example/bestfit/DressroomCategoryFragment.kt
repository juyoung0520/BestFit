package com.example.bestfit

import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.bestfit.model.ItemDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_dressroom_category.view.*
import kotlinx.android.synthetic.main.item_add_item_image.view.*
import kotlinx.android.synthetic.main.item_dressroom.view.*
import java.io.File

class DressroomCategoryFragment : Fragment() {
    private val auth = FirebaseAuth.getInstance()
    private val currentUid = auth.currentUser!!.uid
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dressroom_category, container, false)

        view.fragment_dressroom_category_recyclerview.setHasFixedSize(true)
        view.fragment_dressroom_category_recyclerview.adapter = ItemRecyclerViewAdapter()
        view.fragment_dressroom_category_recyclerview.layoutManager = GridLayoutManager(activity, 3)

        while (view.fragment_dressroom_category_recyclerview.itemDecorationCount > 0)
            view.fragment_dressroom_category_recyclerview.removeItemDecorationAt(0)

        view.fragment_dressroom_category_recyclerview.addItemDecoration(ItemDecoration())

        return view
    }

    inner class ItemRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        private val itemDTOs = arrayListOf<ItemDTO>()

        init {
            val allItemDTOs = arguments?.getParcelableArrayList<ItemDTO>("itemDTOs")

            if (allItemDTOs != null) {
                itemDTOs.addAll(allItemDTOs)
                notifyDataSetChanged()
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_dressroom, parent, false)

            return CustomViewHolder(view)
        }

        inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view)

        override fun getItemCount(): Int {
            return itemDTOs.size
        }

        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val view = (holder as CustomViewHolder).itemView

            if (itemDTOs[position].images.size > 0) {
                view.item_dressroom_iv_item.clipToOutline = true
                Glide.with(view).load(itemDTOs[position].images[0]).apply(
                    RequestOptions().placeholder(R.color.img_loding_placeholder)
                        .error(R.color.image_loading_error_color).centerCrop()
                ).into(view.item_dressroom_iv_item)
            }

            view.item_dressroom_tv_item_name.text = itemDTOs[position].name
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

            val lp = view.layoutParams as GridLayoutManager.LayoutParams
            val spanIndex = lp.spanIndex

            val space = dpToPx(10)

//            if (position == 0 || position == 1 || position == 2)
//                outRect.bottom = space
//            else if (position == itemCount - 3 || position == itemCount - 2 || position == itemCount - 1)
//                outRect.top = space
//            else
//            {
//                outRect.bottom = space
//                outRect.top = space
//            }

            when (spanIndex) {
                0 -> {
                    outRect.left = dpToPx(6)
                    outRect.right = dpToPx(2)
                }
                1 -> {
                    outRect.left = dpToPx(4)
                    outRect.right = dpToPx(4)
                }
                2 -> {
                    outRect.left = dpToPx(2)
                    outRect.right = dpToPx(6)
                }
            }
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