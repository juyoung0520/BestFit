package com.example.bestfit.datasource

import androidx.paging.DataSource
import androidx.paging.ItemKeyedDataSource
import com.example.bestfit.model.ItemDTO
import com.google.firebase.firestore.FirebaseFirestore

class ItemDTODataSource: ItemKeyedDataSource<Long, ItemDTO>() {
    private val db = FirebaseFirestore.getInstance()
    private val QUERY_ITEM_CNT: Long = 5

    class Factory: DataSource.Factory<Long, ItemDTO>() {
        override fun create(): DataSource<Long, ItemDTO> {
            return ItemDTODataSource()
        }
    }

    override fun getKey(item: ItemDTO): Long {
        return item.timestamps!!.first()
    }

    override fun loadInitial(
        params: LoadInitialParams<Long>,
        callback: LoadInitialCallback<ItemDTO>
    ) {
    }

    override fun loadAfter(params: LoadParams<Long>, callback: LoadCallback<ItemDTO>) {
        TODO("Not yet implemented")
    }

    override fun loadBefore(params: LoadParams<Long>, callback: LoadCallback<ItemDTO>) {
        TODO("Not yet implemented")
    }

    private fun fetchAfter() {

    }

}