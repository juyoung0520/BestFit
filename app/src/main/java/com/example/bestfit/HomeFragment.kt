package com.example.bestfit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.createViewModelLazy
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.PagedList
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.*
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.bestfit.model.AccountDTO
import com.example.bestfit.model.ItemDTO
import com.example.bestfit.viewmodel.DataViewModel
import com.example.bestfit.viewmodel.HomeFragmentViewModel
import com.firebase.ui.firestore.paging.FirestorePagingAdapter
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.firebase.ui.firestore.paging.LoadingState
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.item_dressroom.view.*
import kotlinx.android.synthetic.main.item_profile.view.*
import kotlinx.android.synthetic.main.item_recommend.view.*


class HomeFragment : Fragment() {
    private val dataViewModel: DataViewModel by activityViewModels()
    private lateinit var viewModel: HomeFragmentViewModel
    private lateinit var options: FirestorePagingOptions<ItemDTO>

    private lateinit var followProfileRecyclerViewAdapter: FollowProfileRecyclerViewAdapter
    private lateinit var followProfileLayoutManager: RecyclerView.LayoutManager
    private lateinit var followItemRecyclerViewAdapter: FollowItemRecyclerViewAdapter
    private lateinit var followItemLayoutManager: RecyclerView.LayoutManager
    private lateinit var itemPagedListAdapter: ItemDTOPagedListAdapter
    private lateinit var itemLayoutManager: RecyclerView.LayoutManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        isInitializedAccountDTOObserver()
        initFollowItemDTOsObserver()
        //initItemDTOsObserver()

        initToolbar(view)
        //initScrollView(view)
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

        dataViewModel.isInitializedAccountDTO.observe(
            viewLifecycleOwner,
            isInitializedAccountDTOObserver
        )
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

//    private fun initItemDTOsObserver() {
//
//        val itemDTOsObserver = Observer<PagedList<ItemDTO>> { itemDTOs ->
//            itemPagedListAdapter.submitList(itemDTOs)
//        }
//
//        viewModel.pagedList.observe(viewLifecycleOwner, itemDTOsObserver)
//    }

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

//    private fun initScrollView(view: View) {
//        view.fragment_home_scrollview.setOnScrollChangeListener(object : NestedScrollView.OnScrollChangeListener {
//            override fun onScrollChange(
//                v: NestedScrollView?,
//                scrollX: Int,
//                scrollY: Int,
//                oldScrollX: Int,
//                oldScrollY: Int
//            ) {
//                if (v!!.getChildAt(v.childCount -1) != null) {
//                    if ((scrollY >= (v.getChildAt(v.childCount - 1).measuredHeight - v.measuredHeight)) &&
//                        scrollY > oldScrollY) {
//                        println("end scroll")
//                        viewModel.getAllItemDTOs()
//                    }
//                }
//            }
//
//        })
//    }

    private fun initFollowProfileRecyclerView(view: View) {
        followProfileRecyclerViewAdapter = FollowProfileRecyclerViewAdapter()
        followProfileLayoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.HORIZONTAL,
            false
        )

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
        options = FirestorePagingOptions.Builder<ItemDTO>()
            .setLifecycleOwner(viewLifecycleOwner)
            .setQuery(viewModel.getQuery(), viewModel.getConfing(), ItemDTO::class.java)
            .build()

        itemPagedListAdapter = ItemDTOPagedListAdapter()
        itemLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        view.fragment_home_recommend_item_recyclerview.adapter = itemPagedListAdapter
        view.fragment_home_recommend_item_recyclerview.layoutManager = itemLayoutManager
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

    inner class FollowProfileRecyclerViewAdapter: ListAdapter<AccountDTO, FollowProfileRecyclerViewAdapter.FollowProfileViewHolder>(
        DiffProfileCallback()
    ){

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FollowProfileViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(
                R.layout.item_profile,
                parent,
                false
            )
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
                    Glide.with(itemView).load(accountDTO.photo).apply(RequestOptions().centerCrop()).into(
                        itemView.item_profile_iv_profile
                    )

                if (position == 0)
                    viewModel.getItemDTOs(accountDTO)

                itemView.setOnClickListener {
                    viewModel.getItemDTOs(accountDTO)
                }
            }

        }
    }

    inner class FollowItemRecyclerViewAdapter: ListAdapter<ItemDTO, FollowItemRecyclerViewAdapter.FollowItemViewHolder>(
        DiffItemCallback()
    ) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FollowItemViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(
                R.layout.item_dressroom,
                parent,
                false
            )
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

    inner class ItemDTOPagedListAdapter:
       FirestorePagingAdapter<ItemDTO, ItemDTOPagedListAdapter.ItemViewHolder>(options) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recommend, parent, false)
            return ItemViewHolder(view)
        }

        override fun onBindViewHolder(p0: ItemViewHolder, p1: Int, p2: ItemDTO) {
            p0.bind(p2, p1)
        }

        override fun onLoadingStateChanged(state: LoadingState) {
            super.onLoadingStateChanged(state)

            when (state) {
                LoadingState.LOADING_MORE -> {

                }

                LoadingState.LOADING_INITIAL -> {

                }

                LoadingState.LOADED -> {

                }

                LoadingState.FINISHED -> {

                }

                LoadingState.ERROR -> {

                }
            }
        }

        inner class ItemViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

            fun bind(itemDTO: ItemDTO, position: Int) {
                itemView.item_recommend_tv_item_name.text = itemDTO.name

                println("bind " + position)
            }
        }
    }

}