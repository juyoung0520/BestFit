package com.example.bestfit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_dressroom_category.view.*
import kotlinx.android.synthetic.main.item_dressroom_category.view.*
import kotlinx.android.synthetic.main.item_dressroom_subcategory.view.*

class FragmentDressroomCategory : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dressroom_category, container, false)

        val category = arrayListOf<String>()
        category.add("가디건")
        category.add("자켓")
        category.add("코트")
        category.add("점퍼")
        category.add("야상")
        category.add("패딩")

        view.fragment_dressroom_category_recyclerview_subcategory.setHasFixedSize(true)
        view.fragment_dressroom_category_recyclerview_subcategory.adapter = DressroomCategoryRecyclerViewAdapter(category)
        view.fragment_dressroom_category_recyclerview_subcategory.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)

        return view
    }

    inner class DressroomCategoryRecyclerViewAdapter(val category: ArrayList<String>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_dressroom_subcategory, parent, false)

            return CustomViewHolder(view)
        }

        inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view)

        override fun getItemCount(): Int {
            return category.size
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val view = (holder as CustomViewHolder).itemView

            println("bind")
            view.item_dressroom_subcategory_tv_title.text = category[position]
        }

    }
}