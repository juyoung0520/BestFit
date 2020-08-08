package com.example.bestfit

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.bestfit.model.AccountDTO
import com.example.bestfit.model.ItemDTO
import com.example.bestfit.util.InitData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_detail.view.*
import kotlinx.android.synthetic.main.fragment_dressroom.view.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.fragment_menu1.view.*
import kotlinx.android.synthetic.main.fragment_search.view.*

class SearchFragment : Fragment() {
    private val auth = FirebaseAuth.getInstance()
    private val currentUid = auth.currentUser!!.uid
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)

        setHasOptionsMenu(true)

        initToolbar(view)

        return view
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                val mainActivity: MainActivity = requireActivity() as MainActivity
                mainActivity.changeFragment(null, null, true)

                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun initToolbar(view: View) {
        val mainActivity: MainActivity = requireActivity() as MainActivity
        mainActivity.setToolbar(view.fragment_search_toolbar, true)
    }
}