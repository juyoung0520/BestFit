package com.example.bestfit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.bestfit.model.ItemDTO
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_detail.view.*

class DetailFragment : Fragment() {
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_detail, container, false)

        initDetailFragment(view)

        return view
    }

    fun initDetailFragment(view : View) {
        val itemDTO : ItemDTO = arguments?.getParcelable("itemDTO")!!
        val uid =  arguments?.getString("uid")

        db.collection("accounts").document(uid!!).get().addOnCompleteListener {task ->
            if(task.isSuccessful) {
                val account = task.result!!

                view.fragment_detail_tv_user_name.text = account["nickname"].toString()
                view.fragment_detail_tv_user_size.text = account["height"].toString() + " cm / " + account["weight"].toString() + " kg"
            }
        }
        view.fragment_detail_tv_item_name.text = itemDTO.name
        Glide.with(view).load(itemDTO.images[0]).apply(RequestOptions().centerCrop()).into(view.fragment_detail_iv_item)
    }
}