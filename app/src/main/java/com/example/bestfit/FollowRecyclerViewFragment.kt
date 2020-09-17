package com.example.bestfit

import android.accounts.Account
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.*
import com.example.bestfit.model.AccountDTO
import com.example.bestfit.viewmodel.DataViewModel
import com.example.bestfit.viewmodel.FollowFramgentViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_dressroom_category.view.*
import kotlinx.android.synthetic.main.item_follow.view.*

class FollowRecyclerViewFragment: Fragment() {
    private lateinit var viewModel: FollowFramgentViewModel
    private val dataViewModel: DataViewModel by activityViewModels()

    private lateinit var followRecyclerViewAdapter: FollowRecyclerViewAdapter
    private lateinit var layoutManager: RecyclerView.LayoutManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentView =  inflater.inflate(R.layout.fragment_dressroom_category, container, false)

        initFollowViewModel(fragmentView)
        initRecyclerView(fragmentView)

        return fragmentView
    }

    private fun initFollowViewModel(view: View) {
        viewModel = ViewModelProvider(requireParentFragment()).get(FollowFramgentViewModel::class.java)

        val accountDTOsObserver = Observer<ArrayList<AccountDTO>> { accountDTOs ->
            followRecyclerViewAdapter.submitList(accountDTOs.map { it.copy() })
        }

        if (requireArguments().getString("follow") == "er")
            viewModel.followerAccountDTOs.observe(viewLifecycleOwner, accountDTOsObserver)
        else
            viewModel.followingAccountDTOs.observe(viewLifecycleOwner, accountDTOsObserver)
    }

    private fun initDataViewModel(view: View) {

        val accountDTOObserver = Observer<AccountDTO> { accountDTO ->
            
        }

        dataViewModel.accountDTO.observe(viewLifecycleOwner, accountDTOObserver)
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
            }
        }
    }
}