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
//                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//                imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)

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
        val uid = arguments?.getString("uid")!!

        db.collection("accounts").document(uid).get().addOnCompleteListener {task ->
            if(task.isSuccessful) {
                val accountDTO = task.result!!.toObject(AccountDTO::class.java)!!

                if (accountDTO.photo.isNullOrEmpty())
                    view.fragment_detail_iv_profile.setImageResource(R.drawable.ic_profile_photo)
                else
                    Glide.with(view).load(accountDTO.photo).apply(RequestOptions().centerCrop()).into(view.fragment_detail_iv_profile)

                view.fragment_detail_tv_user_name.text = accountDTO.nickname
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

        view.fragment_detail_tv_item_name.text = itemDTO.name
        view.fragment_detail_tv_item_size.text = "${InitData.getSizeFormatString(itemDTO.sizeFormatId!!)} SIZE ${InitData.getSizeString(itemDTO.sizeFormatId!!, itemDTO.sizeId!!)}"

        when (itemDTO.sizeReview) {
            0 -> view.fragment_detail_group_size_review.check(view.fragment_detail_btn_s.id)
            1 -> view.fragment_detail_group_size_review.check(view.fragment_detail_btn_m.id)
            2 -> view.fragment_detail_group_size_review.check(view.fragment_detail_btn_l.id)
        }

        view.fragment_detail_tv_review.text = itemDTO.review
    }
}