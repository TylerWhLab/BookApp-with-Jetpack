package com.kjk.booksearchapp.data.repository

import androidx.paging.PagingData
import com.kjk.booksearchapp.data.model.Book
import com.kjk.booksearchapp.data.model.SearchResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

// response를 android app architecture에 맞게 변환
interface BookSearchRepository {

    suspend fun searchBooks(
        query: String,
        sort: String,
        page: Int,
        size: Int,
    ): Response<SearchResponse>

    // Room DB 조작하는 메서드
    suspend fun insertBooks(book: Book)

    suspend fun deleteBooks(book: Book)

    // fun getFavoriteBooks(): LiveData<List<Book>>
    fun getFavoriteBooks(): Flow<List<Book>>


    // DataStore(설정 화면)
    suspend fun saveSortMode(mode: String)

    suspend fun getSortMode(): Flow<String>

    // WorkManager 상태 저장할 DataStore
    suspend fun saveCacheDeleteMode(mode: Boolean)

    suspend fun getCacheDeleteMode(): Flow<Boolean>


    // Paging
    fun getFavoritePagingBooks(): Flow<PagingData<Book>>

    // Pager가 BookSearchPagingSource 결과를 PagingData 로 변환
    fun searchBooksPaging(query: String, sort: String): Flow<PagingData<Book>>

}