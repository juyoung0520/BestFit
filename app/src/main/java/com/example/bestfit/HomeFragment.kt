package com.example.bestfit

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_home.view.*

class HomeFragment : Fragment() {
    private val auth = FirebaseAuth.getInstance()
    private val currentUid = auth.currentUser!!.uid
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        initToolbar(view)

//        childFragmentManager.addOnBackStackChangedListener {
//            println(childFragmentManager.backStackEntryCount)
//            if (childFragmentManager.backStackEntryCount == 0)
//                view.fragment_home_toolbar.visibility = View.VISIBLE
//        }

        return view
    }

    private fun initToolbar(view: View) {
        view.fragment_home_toolbar.inflateMenu(R.menu.menu_fragment_home)
        view.fragment_home_toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_fragment_home_search -> {
                    val action = HomeFragmentDirections.actionToSearchFragment()
                    findNavController().navigate(action)

                    true
                }
                else -> {
                    false
                }
            }
        }
    }
}