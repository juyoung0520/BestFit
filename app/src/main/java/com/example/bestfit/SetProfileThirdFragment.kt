package com.example.bestfit

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.google.android.gms.dynamic.IFragmentWrapper
import kotlinx.android.synthetic.main.fragment_set_profile_third.view.*

class SetProfileThirdFragment : Fragment() {
    lateinit var fragmentView: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentView = inflater.inflate(R.layout.fragment_set_profile_third, container, false)

        // 아무것도 없음 ㅋ.ㅋ

        return fragmentView
    }
}