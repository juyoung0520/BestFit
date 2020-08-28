package com.example.bestfit

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.addCallback
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.bestfit.model.AccountDTO
import com.example.bestfit.model.ItemDTO
import com.example.bestfit.util.InitData
import com.example.bestfit.viewmodel.DetailFragmentViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_add_item.*
import kotlinx.android.synthetic.main.fragment_account.view.*
import kotlinx.android.synthetic.main.fragment_detail.view.*
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class DetailFragment : Fragment() {
    private lateinit var viewModel: DetailFragmentViewModel

    private val args: DetailFragmentArgs by navArgs()
    private lateinit var itemDTO: ItemDTO

    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val itemDTO = result.data!!.getParcelableExtra<ItemDTO>("tempItemDTO")!!
            this.itemDTO = itemDTO

            viewModel.notifyItemDTOModified()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_detail, container, false)
        itemDTO = args.itemDTO

        initViewModel(view)
        initToolbar(view)
        initScrollView(view)

        return view
    }

    private fun initViewModel(view: View) {
        viewModel = ViewModelProvider(this, DetailFragmentViewModel.Factory(itemDTO.uid!!, itemDTO.id!!)).get(DetailFragmentViewModel::class.java)

        // 여기다가 dibsitems 옵저버 하나 생성
        // 여기다가 dibs count 옵저버 하나 생성
        // 옵저버 안에서 각각 ui (하트 이미지랑 찜 숫자) 바꾸는 함수 호출!
        // 사랑해 팟팅!

        val accountDTOObserver = Observer<AccountDTO> { accountDTO ->
            initDetailFragment(view, accountDTO)
        }

        val initDibsObserver = Observer<Boolean> { initDibs ->
            changeDibs(view, initDibs)
        }

        val dibsObserver = Observer<Int> { dibs ->
            getDibs(view, dibs)
        }

        viewModel.accountDTO.observe(viewLifecycleOwner, accountDTOObserver)
        viewModel.initDibs.observe(viewLifecycleOwner, initDibsObserver)
        viewModel.dibs.observe(viewLifecycleOwner, dibsObserver)
    }

    private fun initToolbar(view: View) {
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            findNavController().navigateUp()
        }

        view.fragment_detail_toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        view.fragment_detail_toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }
        view.fragment_detail_toolbar.inflateMenu(R.menu.menu_fragment_detail)
        view.fragment_detail_toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_fragment_detail_modify_item -> {
                    val intent = Intent(context, AddItemActivity::class.java).putExtra("tempItemDTO", itemDTO)
                    startForResult.launch(intent)
                    true
                }
                else -> false
            }
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
        }, 5)
    }

    private fun initDetailFragment(view: View, accountDTO: AccountDTO) {
        if (accountDTO.photo.isNullOrEmpty())
            view.fragment_detail_iv_profile.setImageResource(R.drawable.ic_profile_photo)
        else {
            Glide.with(view).load(accountDTO.photo).apply(RequestOptions().centerCrop()).into(view.fragment_detail_iv_profile)
        }

        view.fragment_detail_collapsingtoolbarlayout.title = "${accountDTO.nickname}님의\n${itemDTO.name}"
        view.fragment_detail_tv_nickname.text = accountDTO.nickname
        view.fragment_detail_tv_user_size.text = accountDTO.height.toString() + " cm / " + accountDTO.weight.toString() + " kg"

        val top = InitData.getSizeString("01", accountDTO.topId!!)
        val bottom = InitData.getSizeString("03", accountDTO.bottomId!!)
        val shoes = InitData.getSizeString("04", accountDTO.shoesId!!)
        //view.fragment_detail_tv_user_detail_size.text = "Top $top / Bottom $bottom / Shoes $shoes"

        if (!itemDTO.images.isNullOrEmpty()) {
            // 이미지 뷰페이저를 누르고 스크롤하면 툴바에 스크롤 상태 적용이 안됨. -> 안되는 만큼 스크롤이 전부 다 내려가지 않음. (툴바 확장된 크기만큼?)
            view.fragment_detail_viewpager_image.adapter = ImagePagerAdapter(itemDTO.images!!)
            view.fragment_detail_indicator_image.setViewPager(view.fragment_detail_viewpager_image)
        }

        view.fragment_detail_tv_category.text = "${InitData.getCategoryString(itemDTO.categoryId!!)} > ${InitData.getSubCategoryString(
            itemDTO.categoryId!!,
            itemDTO.subCategoryId!!
        )}"
        view.fragment_detail_tv_item_name.text = itemDTO.name

        val review = when (itemDTO.sizeReview) {
            0 -> "작아요"
            1 -> "잘 맞아요"
            2 -> "커요"
            else -> null
        }

        view.fragment_detail_tv_item_size.text = "${InitData.getSizeString(itemDTO.sizeFormatId!!, itemDTO.sizeId!!)} / $review"
        view.fragment_detail_rating.rating = itemDTO.ratingReview!!
        view.fragment_detail_tv_review.text = itemDTO.review
        view.fragment_detail_tv_date.text = SimpleDateFormat("yyyy.MM.dd", Locale.KOREA).format(
            itemDTO.timestamps!![0]
        )
        view.fragment_detail_tv_dibs.text = "찜 ${itemDTO.dibs}"

        view.fragment_detail_iv_profile.setOnClickListener {
            val action = DetailFragmentDirections.actionToAccountFragment(itemDTO.uid!!)
            findNavController().navigate(action)
        }

        restoreScrollPosition(view)
    }

    private fun changeDibs(view: View, initDibs: Boolean) {
        if (initDibs)
            view.fragment_detail_btn_dibs.isChecked = true

        view.fragment_detail_btn_dibs.setOnCheckedChangeListener { compoundButton, b ->
            if (!b) {
                view.fragment_detail_btn_dibs.isChecked = false
                viewModel.removeDibs()

            } else {
                view.fragment_detail_btn_dibs.isChecked = true
                viewModel.addDibs()
            }
        }
    }

    private fun getDibs(view:View, dibs: Int) {
        view.fragment_detail_tv_dibs.text = "찜 $dibs"
    }

    inner class ImagePagerAdapter(private val images: ArrayList<String>) : RecyclerView.Adapter<RecyclerView.ViewHolder>()  {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val iv = ImageView(context)
            iv.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

            return ImageViewHolder(iv)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val viewHolder = holder as ImageViewHolder
            val view = viewHolder.itemView as ImageView

            viewHolder.bind(view, images[position])
        }

        override fun getItemCount(): Int {
            return images.size
        }

        inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            fun bind(view: ImageView, image: String) {
                Glide.with(view)
                    .load(image)
                    .apply(RequestOptions().centerCrop())
                    .into(view)
            }
        }
    }
}