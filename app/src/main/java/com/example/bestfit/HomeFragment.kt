package com.example.bestfit

import android.accounts.Account
import android.os.Bundle
import android.view.*
import androidx.appcompat.view.menu.MenuView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.*
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.bestfit.model.AccountDTO
import com.example.bestfit.model.ItemDTO
import com.example.bestfit.viewmodel.DataViewModel
import com.example.bestfit.viewmodel.HomeFragmentViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.item_dressroom.view.*
import kotlinx.android.synthetic.main.item_profile.view.*
import java.util.*
import kotlin.collections.ArrayList

class HomeFragment : Fragment() {
    private val dataViewModel: DataViewModel by activityViewModels()
    private lateinit var viewModel: HomeFragmentViewModel

    private lateinit var profileRecyclerViewAdapter: ProfileRecyclerViewAdapter
    private lateinit var profileLayoutManager: RecyclerView.LayoutManager
    private lateinit var itemRecyclerViewAdapter: ItemRecyclerViewAdapter
    private lateinit var itemLayoutManager: RecyclerView.LayoutManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        initAccountDTOObserver()
        initToolbar(view)
        initProfileRecyclerView(view)
        initItemRecyclerViewAdapter(view)

        return view
    }

    private fun initAccountDTOObserver() {
        val accountDTOObserver = Observer<AccountDTO> { accountDTO ->
            initFollowAccountDTOsObserver() // 일단 이렇게 해놨는데 어카운트 초기화되고 팔로잉 get 하는 방법 생각해보기
            initFollowItemDTOsObserver()
        }

        dataViewModel.accountDTO.observe(viewLifecycleOwner, accountDTOObserver)
    }

    private fun initFollowAccountDTOsObserver() {
        dataViewModel.getFollowingAccountDTOs()

        val followingAccountDTOsObserver = Observer<ArrayList<AccountDTO>> { following ->
            profileRecyclerViewAdapter.submitList(following.map { it.copy() })

        }

        dataViewModel.followingAccountDTOs.observe(viewLifecycleOwner, followingAccountDTOsObserver)
    }

    private fun initFollowItemDTOsObserver() {
        viewModel = ViewModelProvider(this).get(HomeFragmentViewModel::class.java)

        val followingItemDTOsObserver = Observer<ArrayList<ItemDTO>> { itemDTOs ->
            itemRecyclerViewAdapter.submitList(itemDTOs.map { it.copy() })
        }

        viewModel.followerItemDTOs.observe(viewLifecycleOwner, followingItemDTOsObserver)
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
        profileLayoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        view.fragment_home_profile_recyclerview.adapter = profileRecyclerViewAdapter
        view.fragment_home_profile_recyclerview.layoutManager = profileLayoutManager
    }

    private fun initItemRecyclerViewAdapter(view: View) {
        itemRecyclerViewAdapter = ItemRecyclerViewAdapter()
        itemLayoutManager = GridLayoutManager(context, 2)

        view.fragment_home_item_recyclerview.adapter = itemRecyclerViewAdapter
        view.fragment_home_item_recyclerview.layoutManager = itemLayoutManager
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

                itemView.setOnClickListener {
                    viewModel.getItemDTOs(accountDTO)
                    println("submit")
                }
            }

        }
    }

    inner class DiffItemCallback : DiffUtil.ItemCallback<ItemDTO>() {
        override fun areItemsTheSame(
            oldItem: ItemDTO,
            newItem: ItemDTO
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: ItemDTO,
            newItem: ItemDTO
        ): Boolean {
            return oldItem.timestamps!!.size == newItem.timestamps!!.size
        }
    }

    inner class ItemRecyclerViewAdapter: ListAdapter<ItemDTO, ItemRecyclerViewAdapter.ItemViewHolder>(DiffItemCallback()) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_dressroom, parent, false)
            return ItemViewHolder(view)
        }

        override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
            val itemDTO = getItem(position)
            holder.bind(itemDTO)
        }

        inner class ItemViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

            fun bind(itemDTO: ItemDTO) {
                itemView.item_dressroom_tv_item_name.text = itemDTO.name
            }
        }
    }



}