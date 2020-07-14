package com.example.bestfit

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.bestfit.model.AccountDTO
import com.example.bestfit.model.ItemDTO
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_detail.view.*
import kotlinx.android.synthetic.main.fragment_dressroom.view.*

class DetailFragment : Fragment() {
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_detail, container, false)
        setHasOptionsMenu(true)

        initToolbar(view)

        initDetailFragment(view)

        return view
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
//                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//                imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)

                val mainActivity: MainActivity = activity!! as MainActivity
                mainActivity.changeFragment(null, null, true)

                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun initToolbar(view: View) {
        val mainActivity: MainActivity = activity!! as MainActivity
        mainActivity.setToolbar(view.fragment_detail_toolbar, true)
    }

    private fun initDetailFragment(view : View) {
        val itemDTO: ItemDTO = arguments?.getParcelable("itemDTO")!!
        val uid = arguments?.getString("uid")!!

        db.collection("accounts").document(uid).get().addOnCompleteListener {task ->
            if(task.isSuccessful) {
                val accountDTO = task.result!!.toObject(AccountDTO::class.java)!!

                view.fragment_detail_tv_user_name.text = accountDTO.nickname
                view.fragment_detail_tv_user_size.text = accountDTO.height.toString() + " cm / " + accountDTO.weight.toString() + " kg"
            }
        }

        view.fragment_detail_tv_item_name.text = itemDTO.name
        Glide.with(view).load(itemDTO.images[0]).apply(RequestOptions().centerCrop()).into(view.fragment_detail_iv_item)
    }
}