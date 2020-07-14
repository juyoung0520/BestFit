package com.example.bestfit

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import kotlinx.android.synthetic.main.fragment_add_item_second.view.*
import kotlinx.android.synthetic.main.fragment_add_item_third.view.*


class AddItemThirdFragment  : Fragment() {
    lateinit var fragmentView: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentView = inflater.inflate(R.layout.fragment_add_item_third, container, false)

        setHasOptionsMenu(true)

        test(fragmentView)

        return fragmentView
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.menu_add_item, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_add_item_action_add -> {
                submitAddItem()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun submitAddItem() {
        val addItemActivity = activity as AddItemActivity
        addItemActivity.submitAddItem()
    }

    private fun test(view: View) {
        println(context)
        val button = MaterialButton(context!!, null, R.attr.materialButtonOutlinedStyle)
        button.text = "TEST"
        view.fragment_add_item_third_group_btn.addView(button, -1, ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT))
    }
}