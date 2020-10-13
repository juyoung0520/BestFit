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
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.bestfit.model.AccountDTO
import com.example.bestfit.model.ItemDTO
import com.example.bestfit.viewmodel.DataViewModel
import com.example.bestfit.viewmodel.HomeFragmentViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.item_dressroom.view.*
import kotlinx.android.synthetic.main.item_profile.view.*
import kotlinx.android.synthetic.main.item_recommend.view.*
import java.util.*
import kotlin.collections.ArrayList

class HomeFragment : Fragment() {
    private val dataViewModel: DataViewModel by activityViewModels()
    private lateinit var viewModel: HomeFragmentViewModel

    private lateinit var followProfileRecyclerViewAdapter: FollowProfileRecyclerViewAdapter
    private lateinit var followProfileLayoutManager: RecyclerView.LayoutManager
    private lateinit var followItemRecyclerViewAdapter: FollowItemRecyclerViewAdapter
    private lateinit var followItemLayoutManager: RecyclerView.LayoutManager
    private lateinit var itemRecyclerViewAdapter: ItemRecyclerViewAdapter
    private lateinit var itemLayoutManager: RecyclerView.LayoutManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        isInitializedAccountDTOObserver()
        initFollowItemDTOsObserver()
        initItemDTOsObserver()
        initToolbar(view)
        initFollowProfileRecyclerView(view)
        initFollowItemRecyclerView(view)
        initItemReyclerView(view)

        return view
    }

    private fun isInitializedAccountDTOObserver() {
        val isInitializedAccountDTOObserver = Observer<Boolean> { isInitialized ->
            if (isInitialized)
                initFollowAccountDTOsObserver()
        }

        dataViewModel.isInitializedAccountDTO.observe(viewLifecycleOwner, isInitializedAccountDTOObserver)
    }

    private fun initFollowAccountDTOsObserver() {
        dataViewModel.getFollowingAccountDTOs()

        val followingAccountDTOsObserver = Observer<ArrayList<AccountDTO>> { following ->
            followProfileRecyclerViewAdapter.submitList(following.map { it.copy() })
        }

        dataViewModel.followingAccountDTOs.observe(viewLifecycleOwner, followingAccountDTOsObserver)
    }

    private fun initFollowItemDTOsObserver() {
        viewModel = ViewModelProvider(this).get(HomeFragmentViewModel::class.java)

        val followingItemDTOsObserver = Observer<ArrayList<ItemDTO>> { itemDTOs ->
            followItemRecyclerViewAdapter.submitList(itemDTOs.map { it.copy() })
        }

        viewModel.followerItemDTOs.observe(viewLifecycleOwner, followingItemDTOsObserver)
    }

    private fun initItemDTOsObserver() {

        val itemDTOsObserver = Observer<ArrayList<ItemDTO>> { itemDTOs ->
           itemRecyclerViewAdapter.submitList(itemDTOs.map { it.copy() })
        }

        viewModel.itemDTOs.observe(viewLifecycleOwner, itemDTOsObserver)
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

    private fun initFollowProfileRecyclerView(view:View) {
        followProfileRecyclerViewAdapter = FollowProfileRecyclerViewAdapter()
        followProfileLayoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        view.fragment_home_follow_profile_recyclerview.adapter = followProfileRecyclerViewAdapter
        view.fragment_home_follow_profile_recyclerview.layoutManager = followProfileLayoutManager
    }

    private fun initFollowItemRecyclerView(view: View) {
        followItemRecyclerViewAdapter = FollowItemRecyclerViewAdapter()
        followItemLayoutManager = GridLayoutManager(context, 2)

        view.fragment_home_follow_item_recyclerview.adapter = followItemRecyclerViewAdapter
        view.fragment_home_follow_item_recyclerview.layoutManager = followItemLayoutManager
    }

    private fun initItemReyclerView(view: View) {
        itemRecyclerViewAdapter = ItemRecyclerViewAdapter()
        itemLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        view.fragment_home_recommend_item_recyclerview.adapter = itemRecyclerViewAdapter
        view.fragment_home_recommend_item_recyclerview.layoutManager = itemLayoutManager

        view.fragment_home_recommend_item_recyclerview.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (!view.fragment_home_recommend_item_recyclerview.canScrollVertically(-1)) {
                    // false면 끝
                    println("최상단")
                } else if (!view.fragment_home_recommend_item_recyclerview.canScrollVertically(1)) {
                    println("최하단")
                } else {
                    println("중간")
                }
            }
        })
    }

    inner class DiffProfileCallback: DiffUtil.ItemCallback<AccountDTO>() {
        override fun areItemsTheSame(oldItem: AccountDTO, newItem: AccountDTO): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: AccountDTO, newItem: AccountDTO): Boolean {
            return oldItem.nickname == newItem.nickname
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

    inner class FollowProfileRecyclerViewAdapter: ListAdapter<AccountDTO, FollowProfileRecyclerViewAdapter.FollowProfileViewHolder>(DiffProfileCallback()){

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FollowProfileViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_profile, parent, false)
            return FollowProfileViewHolder(view)
        }

        override fun onBindViewHolder(holder: FollowProfileViewHolder, position: Int) {
            val accountDTO = getItem(position)
            holder.bind(accountDTO, position)
        }

        inner class FollowProfileViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

            fun bind(accountDTO: AccountDTO, position: Int) {
                itemView.item_profile_tv_nickname.text = accountDTO.nickname

                if (accountDTO.photo.isNullOrEmpty())
                    itemView.item_profile_iv_profile.setImageResource(R.drawable.ic_profile_120)
                else
                    Glide.with(itemView).load(accountDTO.photo).apply(RequestOptions().centerCrop()).into(itemView.item_profile_iv_profile)

                if (position == 0)
                    viewModel.getItemDTOs(accountDTO)

                itemView.setOnClickListener {
                    viewModel.getItemDTOs(accountDTO)
                }
            }

        }
    }

    inner class FollowItemRecyclerViewAdapter: ListAdapter<ItemDTO, FollowItemRecyclerViewAdapter.FollowItemViewHolder>(DiffItemCallback()) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FollowItemViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_dressroom, parent, false)
            return FollowItemViewHolder(view)
        }

        override fun onBindViewHolder(holder: FollowItemViewHolder, position: Int) {
            val itemDTO = getItem(position)
            holder.bind(itemDTO)
        }

        inner class FollowItemViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

            fun bind(itemDTO: ItemDTO) {
                itemView.item_dressroom_tv_item_name.text = itemDTO.name

                Glide.with(itemView)
                    .load(itemDTO.images!![0])
                    .apply(RequestOptions().centerCrop())
                    .into(itemView.item_dressroom_iv_item)

                itemView.setOnClickListener {
                    val navController = findNavController()
                    val action = HomeFragmentDirections.actionToDetailFragment(itemDTO)

                    navController.navigate(action)
                }
            }
        }
    }

    inner class ItemRecyclerViewAdapter: ListAdapter<ItemDTO, ItemRecyclerViewAdapter.ItemViewHolder>(DiffItemCallback()) {
        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): ItemRecyclerViewAdapter.ItemViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recommend, parent, false)
            return ItemViewHolder(view)
        }

        override fun onBindViewHolder(
            holder: ItemRecyclerViewAdapter.ItemViewHolder,
            position: Int
        ) {
            val itemDTO = getItem(position)
            holder.bind(itemDTO)
        }

        inner class ItemViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

            fun bind(itemDTO: ItemDTO) {
                itemView.item_recommend_tv_item_name.text = itemDTO.name
            }
        }

    }

}