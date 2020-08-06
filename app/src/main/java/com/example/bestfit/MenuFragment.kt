package com.example.bestfit

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_dressroom.view.*
import kotlinx.android.synthetic.main.fragment_menu1.view.*

class MenuFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_menu1, container, false)

        setHasOptionsMenu(true)

        initToolbar(view)

        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.menu_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_menu_action_modify -> {

                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun initToolbar(view: View) {
        val mainActivity: MainActivity = requireActivity() as MainActivity
        mainActivity.setToolbar(view.fragment_menu_toolbar)
    }
}