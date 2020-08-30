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
import androidx.fragment.app.activityViewModels
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
import com.example.bestfit.viewmodel.DataViewModel
import com.example.bestfit.viewmodel.DetailFragmentViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_detail.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class DetailFragment : Fragment() {
    private val args: DetailFragmentArgs by navArgs()

    private val dataViewModel: DataViewModel by activityViewModels()
    private lateinit var viewModel: DetailFragmentViewModel

    private val auth = FirebaseAuth.getInstance()
    private val currentUid = auth.currentUser!!.uid

    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val itemDTO = result.data!!.getParcelableExtra<ItemDTO>("tempItemDTO")!!

            dataViewModel.changeItemDTO(viewModel.itemDTO.value!!, itemDTO)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_detail, container, false)

        initViewModel(view)
        initToolbar(view)
        initScrollView(view)

        return view
    }

    private fun initViewModel(view: View) {
        viewModel = ViewModelProvider(this).get(DetailFragmentViewModel::class.java)

        if (viewModel.itemDTO.value == null)
            viewModel.setItemDTO(args.itemDTO)

        val accountDTOObserver = Observer<AccountDTO> { accountDTO ->
            initDetailFragment(view, accountDTO)
        }

        val itemDTOObserver = Observer<ItemDTO> { itemDTO ->
            initDetailFragment(view, itemDTO)
        }

        val allItemDTOsObserver = Observer<ArrayList<ArrayList<ItemDTO>>> { allItemDTOs ->
            if (view.fragment_detail_viewpager_image.adapter == null) {
                initDetailFragment(view, viewModel.itemDTO.value!!)
                return@Observer
            }

            allItemDTOs[0].forEach { itemDTO ->
                if (itemDTO.id == viewModel.itemDTO.value!!.id && itemDTO.timestamps!!.size != viewModel.itemDTO.value!!.timestamps!!.size) {
                    viewModel.setItemDTO(itemDTO)

                    view.fragment_detail_collapsingtoolbarlayout.title =
                        "${dataViewModel.accountDTO.value!!.nickname}님의\n${itemDTO.name}"
                    initDetailFragment(view, itemDTO)
                }
            }
        }

        val dibsItemDTOsObserver = Observer<ArrayList<ItemDTO>> { dibsItemDTOs ->
            dibsItemDTOs.forEach { itemDTO ->
                if (itemDTO.id == viewModel.itemDTO.value!!.id) {
                    changeDibsState(view, true, itemDTO.dibs!!)
                    return@Observer
                }
            }

            val removedDibs = dataViewModel.removedDibsItems[viewModel.itemDTO.value!!.id]
            if (removedDibs != null)
                changeDibsState(view, false, removedDibs)
        }

        if (viewModel.itemDTO.value!!.uid == currentUid) {
            dataViewModel.accountDTO.observe(viewLifecycleOwner, accountDTOObserver)
            dataViewModel.allItemDTOs.observe(viewLifecycleOwner, allItemDTOsObserver)
        }
        else {
            viewModel.accountDTO.observe(viewLifecycleOwner, accountDTOObserver)
            viewModel.itemDTO.observe(viewLifecycleOwner, itemDTOObserver)
        }

        dataViewModel.dibsItemDTOs.observe(viewLifecycleOwner, dibsItemDTOsObserver)
    }

    private fun initToolbar(view: View) {
        view.fragment_detail_toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        view.fragment_detail_toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }
        view.fragment_detail_toolbar.inflateMenu(R.menu.menu_fragment_detail)
        view.fragment_detail_toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_fragment_detail_modify_item -> {
                    val intent = Intent(context, AddItemActivity::class.java).putExtra("tempItemDTO", viewModel.itemDTO.value!!)
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
        }, 5)
    }

    private fun changeDibsState(view: View, isChecked: Boolean, dibs: Int) {
        view.fragment_detail_btn_dibs.isChecked = isChecked
        view.fragment_detail_tv_dibs.text = "찜 $dibs"
    }

    private fun initDetailFragment(view: View, accountDTO: AccountDTO) {
        view.fragment_detail_collapsingtoolbarlayout.title = "${accountDTO.nickname}님의\n${viewModel.itemDTO.value!!.name}"

        if (accountDTO.photo.isNullOrEmpty())
            view.fragment_detail_iv_profile.setImageResource(R.drawable.ic_profile_photo)
        else {
            Glide.with(view).load(accountDTO.photo).apply(RequestOptions().centerCrop()).into(view.fragment_detail_iv_profile)
        }

        view.fragment_detail_tv_nickname.text = accountDTO.nickname
        view.fragment_detail_tv_user_size.text = accountDTO.height.toString() + " cm / " + accountDTO.weight.toString() + " kg"

        val top = InitData.getSizeString("01", accountDTO.topId!!)
        val bottom = InitData.getSizeString("03", accountDTO.bottomId!!)
        val shoes = InitData.getSizeString("04", accountDTO.shoesId!!)
        //view.fragment_detail_tv_user_detail_size.text = "Top $top / Bottom $bottom / Shoes $shoes"
    }

    private fun initDetailFragment(view: View, itemDTO: ItemDTO) {
        if (!itemDTO.images.isNullOrEmpty()) {
            // 이미지 뷰페이저를 누르고 스크롤하면 툴바에 스크롤 상태 적용이 안됨. -> 안되는 만큼 스크롤이 전부 다 내려가지 않음. (툴바 확장된 크기만큼?)
            view.fragment_detail_viewpager_image.adapter = ImagePagerAdapter(itemDTO.images!!)
            view.fragment_detail_indicator_image.setViewPager(view.fragment_detail_viewpager_image)
        }

        changeDibsState(view, dataViewModel.containsDibsItem(itemDTO.id!!), itemDTO.dibs!!)

        view.fragment_detail_btn_dibs.setOnClickListener {
            // 이거 너무 빠르게 못 누르게 딜레이를 줘야할듯.
            if (view.fragment_detail_btn_dibs.isChecked)
                dataViewModel.addDibsItem(itemDTO.id!!)
            else
                dataViewModel.removeDibsItem(itemDTO.id!!)
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

        view.fragment_detail_iv_profile.setOnClickListener {
            val action = DetailFragmentDirections.actionToAccountFragment(itemDTO.uid!!)
            findNavController().navigate(action)
        }

        restoreScrollPosition(view)
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