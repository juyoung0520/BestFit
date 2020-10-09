package com.example.bestfit

import android.accounts.Account
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.bestfit.model.AccountDTO
import com.example.bestfit.viewmodel.AccountFragmentViewModel
import com.example.bestfit.viewmodel.DataViewModel
import com.example.bestfit.viewmodel.FollowFramgentViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_account.view.*
import kotlinx.android.synthetic.main.fragment_dressroom_category.view.*
import kotlinx.android.synthetic.main.item_follow.view.*

class FollowRecyclerViewFragment: Fragment() {
    private lateinit var viewModel: FollowFramgentViewModel
    private val dataViewModel: DataViewModel by activityViewModels()

    private lateinit var followRecyclerViewAdapter: FollowRecyclerViewAdapter
    private lateinit var layoutManager: RecyclerView.LayoutManager

    private val auth = FirebaseAuth.getInstance()
    private val currentUid = auth.currentUser!!.uid

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentView =  inflater.inflate(R.layout.fragment_dressroom_category, container, false)

        initViewModel(fragmentView)
        initRecyclerView(fragmentView)

        return fragmentView
    }

    private fun initViewModel(view: View) {
        val uid = requireArguments().getString("uid")

        if (uid != currentUid) {
            viewModel = ViewModelProvider(requireParentFragment()).get(FollowFramgentViewModel::class.java)
            initFollowingObserver(uid!!)
        }
        initAccountDTOsObserver(uid == currentUid)
    }

    private fun initAccountDTOsObserver(isMine: Boolean) {
        val accountDTOsObserver = Observer<ArrayList<AccountDTO>> { accountDTOs ->
            println("!!submit!!")
            followRecyclerViewAdapter.submitList(accountDTOs.map { it.copy() })
        }

        when (requireArguments().getString("follow")) {
            "er" -> {
                if (isMine) dataViewModel.followerAccountDTOs.observe(viewLifecycleOwner, accountDTOsObserver)
                else viewModel.followerAccountDTOs.observe(viewLifecycleOwner, accountDTOsObserver)
            }
            "ing" -> {
                if (isMine) dataViewModel.followingAccountDTOs.observe(viewLifecycleOwner, accountDTOsObserver)
                else viewModel.followingAccountDTOs.observe(viewLifecycleOwner, accountDTOsObserver)
            }
        }
    }

    private fun initFollowingObserver(uid: String) {
        val followingAccountDTOsObserver = Observer<ArrayList<AccountDTO>> {
            val accountDTO = dataViewModel.getAccountDTO()

            if (accountDTO.following!!.contains(uid)) {
                viewModel.addFollower(accountDTO)
            } else{
                viewModel.removeFollower(accountDTO)
            }

        }
        dataViewModel.followingAccountDTOs.observe(viewLifecycleOwner, followingAccountDTOsObserver)
    }


    private fun initRecyclerView(view: View) {
        followRecyclerViewAdapter = FollowRecyclerViewAdapter()
        layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        view.fragment_dressroom_category_recyclerview.adapter = followRecyclerViewAdapter
        view.fragment_dressroom_category_recyclerview.layoutManager = layoutManager
    }

    inner class DiffFollowCallback: DiffUtil.ItemCallback<AccountDTO>() {
        override fun areItemsTheSame(oldItem: AccountDTO, newItem: AccountDTO): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: AccountDTO, newItem: AccountDTO): Boolean {
            return oldItem.nickname == newItem.nickname
        }

    }

    inner class FollowRecyclerViewAdapter: ListAdapter<AccountDTO, FollowRecyclerViewAdapter.FollowViewHolder>(DiffFollowCallback()) {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FollowViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_follow, parent, false)
            return FollowViewHolder(view)
        }

        override fun onBindViewHolder(holder: FollowViewHolder, position: Int) {
            val accountDTO = getItem(position)
            holder.bind(accountDTO)
        }


        inner class FollowViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
            fun bind(accountDTO: AccountDTO) {
                itemView.item_follow_tv_nickname.text = accountDTO.nickname

                if (accountDTO.photo.isNullOrEmpty())
                    itemView.item_follow_iv_profile.setImageResource(R.drawable.ic_profile_120)
                else
                    Glide.with(itemView).load(accountDTO.photo).apply(RequestOptions().centerCrop()).into(itemView.item_follow_iv_profile)

            }
        }
    }
}