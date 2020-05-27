package com.example.bestfit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_dressroom.view.*
import kotlinx.android.synthetic.main.item_dressroom_category.view.*

class FragmentDressroom : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dressroom, container, false)

        setTabOfCategory(view)

        activity!!.supportFragmentManager.beginTransaction().add(R.id.fragment_dressroom_layout_frame, FragmentDressroomCategory())
            .commit()

        return view
    }

    fun setTabOfCategory(view: View) {
        val category = arrayListOf<String>()
        category.add("아우터")
        category.add("상의")
        category.add("하의")
        category.add("신발")

        for (title in category) {
            val tab = view.fragment_dressroom_tab.newTab().setText(title)
            view.fragment_dressroom_tab.addTab(tab)
        }
    }

    inner class DressroomCategoryRecyclerViewAdapter(val category: ArrayList<String>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_dressroom_category, parent, false)

            return CustomViewHolder(view)
        }

        inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view)

        override fun getItemCount(): Int {
            return category.size
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val view = (holder as CustomViewHolder).itemView

            println("bind")
            view.item_dressroom_category_tv_title.text = category[position]
        }

    }
}