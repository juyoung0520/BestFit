package com.example.bestfit

import android.Manifest
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.example.bestfit.model.ItemDTO
import com.example.bestfit.util.ImagePicker
import com.example.bestfit.viewmodel.DataViewModel
import com.google.android.material.transition.MaterialElevationScale
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.qingmei2.rximagepicker.core.RxImagePicker
import com.qingmei2.rximagepicker.entity.Result
import com.qingmei2.rximagepicker_extension.MimeType
import com.qingmei2.rximagepicker_extension_zhihu.ZhihuConfigurationBuilder
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.item_dressroom.view.*

class HomeFragment : Fragment() {
    private val dataViewModel: DataViewModel by activityViewModels()

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
        test()

        return view
    }

    private fun initToolbar(view: View) {
        view.fragment_home_toolbar.inflateMenu(R.menu.menu_fragment_home)
        view.fragment_home_toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_fragment_home_search -> {
                    dataViewModel.testAddItem()
//                    val action = HomeFragmentDirections.actionToSearchFragment()
//                    findNavController().navigate(action)

                    true
                }
                else -> {
                    false
                }
            }
        }
    }

    private fun test() {
        dataViewModel.accountDTO.observe(viewLifecycleOwner, { accountDTO ->
            test2()
        })
    }

    private fun test2() {
        dataViewModel.items.observe(viewLifecycleOwner, { items ->
            println(items)
        })
    }
}