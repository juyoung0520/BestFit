package com.example.bestfit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bestfit.model.ItemDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_dressroom_category.view.*
import kotlinx.android.synthetic.main.item_dressroom.view.*

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

        view.fragment_dressroom_category_recyclerview.adapter = ItemRecyclerViewAdapter()
        view.fragment_dressroom_category_recyclerview.layoutManager = GridLayoutManager(activity, 3)

        return view
    }

    inner class ItemRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        private val itemDTOs = arrayListOf<ItemDTO>()

        init {
            db.collection("accounts").document(currentUid).get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (task.result!!["items"] == null)
                        return@addOnCompleteListener

                    val items = task.result!!["items"] as ArrayList<String>
                    var cnt = 0

                    for (itemId in items) {
                        db.collection("items").document(itemId).get().addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                itemDTOs.add(task.result!!.toObject(ItemDTO::class.java)!!)
                                cnt += 1

                                if (cnt >= items.size) {
                                    println(itemDTOs)
                                    notifyDataSetChanged()
                                }
                            }
                        }
                    }
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_dressroom, parent, false)

            val width = resources.displayMetrics.widthPixels / 3
            view.item_dressroom_layout_item.layoutParams.height = width

            return CustomViewHolder(view)
        }

        inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view)

        override fun getItemCount(): Int {
            return itemDTOs.size
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val view = (holder as CustomViewHolder).itemView

            println("bind $position")
        }
    }
}