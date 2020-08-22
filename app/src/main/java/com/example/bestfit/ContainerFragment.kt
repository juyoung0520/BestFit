package com.example.bestfit

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment

class ContainerFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val layoutResource = requireArguments().getInt("layoutResource")

        return inflater.inflate(layoutResource, container, false)
    }
}