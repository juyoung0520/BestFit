package com.example.bestfit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.bestfit.model.AccountDTO
import com.example.bestfit.util.InitData
import com.example.bestfit.viewmodel.DetailFragmentViewModel
import androidx.lifecycle.Observer
import com.example.bestfit.model.ItemDTO
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_detail.view.*
import java.text.SimpleDateFormat
import java.util.*

class DetailFragment : Fragment() {
    private lateinit var viewModel: DetailFragmentViewModel

    private val args: DetailFragmentArgs by navArgs()
    private lateinit var itemDTO: ItemDTO

    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_detail, container, false)

        initViewModel(view)
        initToolbar(view)

        return view
    }

    private fun initViewModel(view: View) {
        viewModel = ViewModelProvider(this).get(DetailFragmentViewModel::class.java)

        if (!viewModel.isInitialized()) {
            viewModel.setInitializedState(true)

            initScrollView(view)

            itemDTO = args.itemDTO
            viewModel.getAccountDTO(itemDTO.uid!!)
        }

        val accountDTOObserver = Observer<AccountDTO> { accountDTO ->
            initDetailFragment(view, accountDTO)
        }

        viewModel.accountDTO.observe(viewLifecycleOwner, accountDTOObserver)
    }

    private fun initToolbar(view: View) {
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            findNavController().navigateUp()
        }

        view.fragment_detail_toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        view.fragment_detail_toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun initScrollView(view: View) {
        view.fragment_detail_scrollview.setOnScrollChangeListener { v: NestedScrollView?, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int ->
            viewModel.setScrollPosition(scrollY)
        }
    }

    private fun restoreScrollPosition(view: View) {
        view.fragment_detail_scrollview.postDelayed({
            val y = viewModel.getScrollPosition()
            if (y == 0)
                view.fragment_detail_appbarlayout.setExpanded(true, false)
            else
                view.fragment_detail_appbarlayout.setExpanded(false, false)

            view.fragment_detail_scrollview.scrollTo(0, y)
            initScrollView(view)
        }, 10)
    }

    private fun initDetailFragment(view : View, accountDTO: AccountDTO) {
        if (accountDTO.photo.isNullOrEmpty())
            view.fragment_detail_iv_profile.setImageResource(R.drawable.ic_profile_photo)
        else
            Glide.with(view).load(accountDTO.photo).apply(RequestOptions().centerCrop()).into(view.fragment_detail_iv_profile)

        view.fragment_detail_tv_nickname.text = accountDTO.nickname
        view.fragment_detail_tv_user_size.text = accountDTO.height.toString() + " cm / " + accountDTO.weight.toString() + " kg"

        val top = InitData.getSizeString("01", accountDTO.topId!!)
        val bottom = InitData.getSizeString("03", accountDTO.bottomId!!)
        val shoes = InitData.getSizeString("04", accountDTO.shoesId!!)
        //view.fragment_detail_tv_user_detail_size.text = "Top $top / Bottom $bottom / Shoes $shoes"

        if (itemDTO.images.isNotEmpty()) {
            Glide.with(view).load(itemDTO.images[0]).apply(RequestOptions().centerCrop())
                .into(view.fragment_detail_iv_item)
        }

        view.fragment_detail_tv_category.text = "${InitData.getCategoryString(itemDTO.categoryId!!)} > ${InitData.getSubCategoryString(itemDTO.categoryId!!, itemDTO.subCategoryId!!)}"
        view.fragment_detail_tv_item_name.text = itemDTO.name

        val review = when (itemDTO.sizeReview) {
            0 -> "작아요"
            1 -> "잘 맞아요"
            2 -> "커요"
            else -> null
        }

        view.fragment_detail_tv_item_size.text = "${InitData.getSizeString(itemDTO.sizeFormatId!!, itemDTO.sizeId!!)} / $review"
        view.fragment_detail_tv_review.text = itemDTO.review
        view.fragment_detail_tv_date.text = SimpleDateFormat("yyyy.MM.dd", Locale.KOREA).format(itemDTO.timestamp)

        view.fragment_detail_iv_profile.setOnClickListener {
            val action = DetailFragmentDirections.actionToAccountFragment(itemDTO.uid!!)
            findNavController().navigate(action)
        }

        restoreScrollPosition(view)
    }
}