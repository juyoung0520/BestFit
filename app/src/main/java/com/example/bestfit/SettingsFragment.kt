package com.example.bestfit

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.bestfit.model.AccountDTO
import com.example.bestfit.model.ItemDTO
import com.example.bestfit.util.InitData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_detail.view.*
import kotlinx.android.synthetic.main.fragment_dressroom.view.*
import kotlinx.android.synthetic.main.fragment_settings.view.*

class SettingsFragment : Fragment() {
    private val auth = FirebaseAuth.getInstance()
    private val currentUid = auth.currentUser!!.uid
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        setHasOptionsMenu(true)

        initToolbar(view)

        initMenuFragment(view)

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
        mainActivity.setToolbar(view.fragment_settings_toolbar)
    }

    private fun initMenuFragment(view : View) {
        db.collection("accounts").document(currentUid).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val accountDTO = task.result!!.toObject(AccountDTO::class.java)!!

                if (accountDTO.photo.isNullOrEmpty())
                    view.fragment_settings_iv_profile.setImageResource(R.drawable.ic_profile_photo)
                else
                    Glide.with(view).load(accountDTO.photo).apply(RequestOptions().centerCrop()).into(view.fragment_settings_iv_profile)

                  view.fragment_settings_tv_nickname.text = accountDTO.nickname
//                view.fragment_settings_tv_user_height.text = accountDTO.height.toString() + " cm"
//                view.fragment_settings_tv_user_weight.text = accountDTO.weight.toString() + " kg"
                  view.fragment_settings_tv_user_size.text = accountDTO.height.toString() + " cm / " + accountDTO.weight.toString() + " kg"

                val top = InitData.getSizeString("01", accountDTO.topId!!)
                val bottom = InitData.getSizeString("03", accountDTO.bottomId!!)
                val shoes = InitData.getSizeString("04", accountDTO.shoesId!!)
                view.fragment_settings_tv_user_detail_size.text = "TOP " + top + " / BOTTOM "+ bottom + " / SHOES " + shoes

                view.fragment_settings_tv_message.text = accountDTO.message
            }
        }
    }

//    auth.signOut()
//
//    startActivity(Intent(this, SignInActivity::class.java))
//    finish()
}