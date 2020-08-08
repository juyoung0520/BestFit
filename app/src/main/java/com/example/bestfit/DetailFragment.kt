package com.example.bestfit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.bestfit.model.AccountDTO
import com.example.bestfit.model.ItemDTO
import com.example.bestfit.util.InitData
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_detail.view.*
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*

class DetailFragment : Fragment() {
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_detail, container, false)
        setHasOptionsMenu(true)

        initToolbar(view)

        initDetailFragment(view)

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
        mainActivity.setToolbar(view.fragment_detail_toolbar, true)
    }

    private fun initDetailFragment(view : View) {
        val itemDTO: ItemDTO = arguments?.getParcelable("itemDTO")!!

        db.collection("accounts").document(itemDTO.uid!!).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val accountDTO = task.result!!.toObject(AccountDTO::class.java)!!

                if (accountDTO.photo.isNullOrEmpty())
                    view.fragment_detail_iv_profile.setImageResource(R.drawable.ic_profile_photo)
                else
                    Glide.with(view).load(accountDTO.photo).apply(RequestOptions().centerCrop()).into(view.fragment_detail_iv_profile)

                view.fragment_detail_tv_nickname.text = accountDTO.nickname
                view.fragment_detail_tv_user_size.text = accountDTO.height.toString() + " cm / " + accountDTO.weight.toString() + " kg"

                val top = InitData.getSizeString("01", accountDTO.topId!!)
                val bottom = InitData.getSizeString("03", accountDTO.bottomId!!)
                val shoes = InitData.getSizeString("04", accountDTO.shoesId!!)
                view.fragment_detail_tv_user_detail_size.text = "Top $top / Bottom $bottom / Shoes $shoes"
            }
        }

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
    }
}