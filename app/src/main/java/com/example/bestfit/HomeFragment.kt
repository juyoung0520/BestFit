package com.example.bestfit

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.bestfit.model.AccountDTO
import com.example.bestfit.viewmodel.DataViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_account.*
import kotlinx.android.synthetic.main.fragment_dressroom_category.view.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.item_profile.view.*
import java.util.*

class HomeFragment : Fragment() {
    private val dataViewModel: DataViewModel by activityViewModels()

    private val auth = FirebaseAuth.getInstance()
    private val currentUid = auth.currentUser!!.uid
    private val db = FirebaseFirestore.getInstance()

    private lateinit var profileRecyclerViewAdapter: ProfileRecyclerViewAdapter
    private lateinit var layoutManager: RecyclerView.LayoutManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        initAccountDTOObserver()
        initToolbar(view)
        initProfileRecyclerView(view)

        return view
    }

    private fun initAccountDTOObserver() {
        val accountDTOObserver = Observer<AccountDTO> { accountDTO ->
            initFollowObserver() // 일단 이렇게 해놨는데 어카운트 초기화되고 팔로잉 get 하는 방법 생각해보기
        }

        dataViewModel.accountDTO.observe(viewLifecycleOwner, accountDTOObserver)
    }

    private fun initFollowObserver() {
        dataViewModel.getFollowingAccountDTOs()

        val followingAccountDTOsObserver = Observer<ArrayList<AccountDTO>> { following ->
            profileRecyclerViewAdapter.submitList(following.map { it.copy() })
        }

        dataViewModel.followingAccountDTOs.observe(viewLifecycleOwner, followingAccountDTOsObserver)
    }

    private fun initToolbar(view: View) {
        view.fragment_home_toolbar.inflateMenu(R.menu.menu_fragment_home)
        view.fragment_home_toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_fragment_home_search -> {
                    val action = HomeFragmentDirections.actionToSearchFragment()
                    findNavController().navigate(action)

                    true
                }
                else -> {
                    false
                }
            }
        }
    }

    private fun initProfileRecyclerView(view:View) {
        profileRecyclerViewAdapter = ProfileRecyclerViewAdapter()
        layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        view.fragment_home_profile_recyclerview.adapter = profileRecyclerViewAdapter
        view.fragment_home_profile_recyclerview.layoutManager = layoutManager
    }

    inner class DiffProfileCallback: DiffUtil.ItemCallback<AccountDTO>() {
        override fun areItemsTheSame(oldItem: AccountDTO, newItem: AccountDTO): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: AccountDTO, newItem: AccountDTO): Boolean {
            return oldItem.nickname == newItem.nickname
        }

    }

    inner class ProfileRecyclerViewAdapter: ListAdapter<AccountDTO, ProfileRecyclerViewAdapter.ProfileViewHolder>(DiffProfileCallback()){

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_profile, parent, false)
            return ProfileViewHolder(view)
        }

        override fun onBindViewHolder(holder: ProfileViewHolder, position: Int) {
            val accountDTO = getItem(position)
            holder.bind(accountDTO)
        }

        inner class ProfileViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

            fun bind(accountDTO: AccountDTO) {
                itemView.item_profile_tv_nickname.text = accountDTO.nickname
            }

        }
    }

}