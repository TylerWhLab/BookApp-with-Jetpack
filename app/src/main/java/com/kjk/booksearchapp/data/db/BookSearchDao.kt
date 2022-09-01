package com.kjk.booksearchapp.data.db

import androidx.paging.PagingSource
import androidx.room.*
import com.kjk.booksearchapp.data.model.Book
import kotlinx.coroutines.flow.Flow

@Dao
interface BookSearchDao {

    // OnConflictStrategy.REPLACE : 입력값이 DB에 이미 있으면 UPDATE
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBook(book: Book)

    @Delete
    suspend fun deleteBook(book: Book)
    // suspend : 시간이 걸리는 작업은 코루틴안에서 비동기 처리해야하기 때문에 suspend 붙여줌

    @Query("SELECT * FROM books")
    fun getFavoriteBooks(): Flow<List<Book>>

    // Paging
    @Query("SELECT * FROM books")
    fun getFavoritePagingBooks(): PagingSource<Int, Book>
}