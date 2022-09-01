package com.kjk.booksearchapp.data.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.kjk.booksearchapp.data.api.BookSearchApi
import com.kjk.booksearchapp.data.model.Book
import com.kjk.booksearchapp.util.Constants.PAGING_SIZE
import retrofit2.HttpException
import java.io.IOException

class BookSearchPagingSource(
    private val api: BookSearchApi, /* Hilt DI, BookSearchRepositoryImpl 로부터 Retrofit 인스턴스 전달 받음 */
    private val query: String,
    private val sort: String,
) : PagingSource<Int, Book>() { // PagingSource 상속<페이지번호타입, 데이터타입>

    // 페이저가 데이터를 호출할때마다 load 호출
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Book> {
        return try {
            // key값을 페이지번호에 넣음
            val pageNumber = params.key ?: STARTING_PAGE_INDEX

            // 페이지번호를 retrofit에 넣어 호출
            val response = api.searchBooks(query, sort, pageNumber, params.loadSize)

            // endOfPaginationReached => 데이터의 끝
            val endOfPaginationReached = response.body()?.meta?.isEnd!!

            val data = response.body()?.documents!!

            // 1페이지라면 이전 페이지 null
            val prevKey = if (pageNumber == STARTING_PAGE_INDEX) null else pageNumber - 1
            // 데이터가 더 이상 없으면 nextKey = null
            val nextKey = if (endOfPaginationReached) {
                null
            } else {
                // initial load size = 3 * NETWORK_PAGE_SIZE
                // ensure we're not requesting duplicating items, at the 2nd request
                pageNumber + (params.loadSize / PAGING_SIZE)
            }

            // Response 내 이전 페이지, 다음 페이지 키값을 LoadResult에 담아 반환
            LoadResult.Page(
                data = data,
                prevKey = prevKey,
                nextKey = nextKey,
            )
        } catch (exception: IOException) {
            LoadResult.Error(exception)
        } catch (exception: HttpException) {
            LoadResult.Error(exception)
        }
    }

    // 페이지 갱신 시 호출, 최근 접근한 페이지(state.anchorPosition)의 이전,다음 페이지 key 반환
    override fun getRefreshKey(state: PagingState<Int, Book>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    // 키 초기값은 null이라 시작 페이지를 1로 지정
    companion object {
        const val STARTING_PAGE_INDEX = 1
    }
}