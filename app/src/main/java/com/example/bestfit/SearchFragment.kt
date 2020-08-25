package com.example.bestfit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.bestfit.model.ItemDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_search.view.*
import kotlinx.android.synthetic.main.item_dressroom.view.*


class SearchFragment : Fragment() {
    private val auth = FirebaseAuth.getInstance()
    private val currentUid = auth.currentUser!!.uid
    private val db = FirebaseFirestore.getInstance()
    private var itemDTOs: ArrayList<ItemDTO> = arrayListOf()
    private var resultRecyclerViewAdapter = ResultRecyclerViewAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)

        initToolbar(view)

        view.fragment_search_searchview.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                view.fragment_search_searchview.clearFocus()
                view.fragment_search_line.requestFocus()

                when (view.fragment_search_chipgroup.checkedChipId) {
                    view.fragment_search_chip_item.id -> {
                        searchItem(view.fragment_search_searchview.query.toString())
                    }
                }

                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                return false
            }
        })

        view.fragment_search_recyclerview_result.setHasFixedSize(true)
        view.fragment_search_recyclerview_result.adapter = resultRecyclerViewAdapter
        view.fragment_search_recyclerview_result.layoutManager = GridLayoutManager(activity, 2)

        return view
    }

    private fun initToolbar(view: View) {
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            findNavController().navigateUp()
        }

        view.fragment_search_toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        view.fragment_search_toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun searchItem(query: String) {
        itemDTOs.clear()

        db.collection("items").whereArrayContains("searchKeywords", query).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                if (task.result!!.isEmpty) {
                    println("empty")
                    // 검색결과가 없습니다!
                    resultRecyclerViewAdapter.notifyDataSetChanged()

                    return@addOnCompleteListener
                }

                var cnt = 0
                val resultCount = task.result!!.documents.size

                for (result in task.result!!.documents) {
                    db.collection("items").document(result.id).get().addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            itemDTOs.add(task.result!!.toObject(ItemDTO::class.java)!!)
                            cnt += 1

                            if (cnt >= resultCount)
                                resultRecyclerViewAdapter.notifyDataSetChanged()
                        }
                    }
                }
            }
        }
    }

    inner class ResultRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_dressroom, parent, false)

            return CustomViewHolder(view)
        }

        inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view)
        override fun getItemCount(): Int {
            return itemDTOs.size
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val view = (holder as CustomViewHolder).itemView

            if (itemDTOs[position].images!!.size > 0) {
                Glide.with(view).load(itemDTOs[position].images!![0]).apply(
                    RequestOptions().placeholder(R.color.img_loding_placeholder)
                        .error(R.color.image_loading_error_color).centerCrop()
                ).into(view.item_dressroom_iv_item)
            }

            view.item_dressroom_tv_item_name.text = itemDTOs[position].name
            view.setOnClickListener {
                val action = SearchFragmentDirections.actionToDetailFragment(itemDTOs[position])
                findNavController().navigate(action)
            }
        }
    }
}