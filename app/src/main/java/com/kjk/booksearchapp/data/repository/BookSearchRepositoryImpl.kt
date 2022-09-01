package com.kjk.booksearchapp.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.kjk.booksearchapp.data.api.BookSearchApi
import com.kjk.booksearchapp.data.db.BookSearchDatabase
import com.kjk.booksearchapp.data.model.Book
import com.kjk.booksearchapp.data.model.SearchResponse
import com.kjk.booksearchapp.data.repository.BookSearchRepositoryImpl.PreferencesKeys.SORT_MODE
import com.kjk.booksearchapp.util.Constants.PAGING_SIZE
import com.kjk.booksearchapp.util.Sort
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

// BookSearchRepository 구현하는 class
@Singleton // 의존성 주입 가능한 Scope로 지정
class BookSearchRepositoryImpl @Inject constructor( // AppModule 내 Retrofit, Room, DataStore 의존성 주입
    // @Inject constructor를 붙여서 생성자 인자로 넣는 인스턴스들(아래 3개)은 Hilt가 주입
    private val db: BookSearchDatabase, /* Room */
    private val dataStore: DataStore<Preferences>, /* DataStore 설정화면 */
    private val api: BookSearchApi /* Hilt DI */
) : BookSearchRepository {
    override suspend fun searchBooks(
        query: String,
        sort: String,
        page: Int,
        size: Int
    ): Response<SearchResponse> {
        return api.searchBooks(query, sort, page, size) // retrofit api에 searchBooks 실행하고, 결과를 반환
    }

    override suspend fun insertBooks(book: Book) {
        db.bookSearchDao().insertBook(book)
    }

    override suspend fun deleteBooks(book: Book) {
        db.bookSearchDao().deleteBook(book)
    }

    override fun getFavoriteBooks(): Flow<List<Book>> {
        return db.bookSearchDao().getFavoriteBooks()
    }


    // DataStore
    private object PreferencesKeys {
        // 저장 및 불러오기에 사용할 키
        // 타입 지정하기 위해 stringPreferencesKey 사용(키가 스트링이다)
        val SORT_MODE = stringPreferencesKey("sort_mode")

        // WorkManager 상태 저장할 DataStore
        val CACHE_DELETE_MODE = booleanPreferencesKey("cache_delete_mode")
    }

    // 저장 작업은 코루틴 안에서 이뤄져야하기 때문에 suspend
    override suspend fun saveSortMode(mode: String) {
        // prefs: 전달 받은 mode 값을 edit 블록 안에서 저장
        dataStore.edit { prefs ->
            prefs[SORT_MODE] = mode
        }
    }

    override suspend fun getSortMode(): Flow<String> {
        return dataStore.data /* 파일에 접근하기 위해 data 메소드 사용 */
            .catch { exception -> /* 파일 접근 실패에 대비해 catch로 예외처리 */
                if (exception is IOException) {
                    exception.printStackTrace()
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { prefs -> /* 키를 전달하여 flow 반환 받음 */
                prefs[SORT_MODE] ?: Sort.ACCURACY.value // 기본값은 ACCURACY
            }
    }

    // WorkManager 상태 저장할 DataStore
    override suspend fun saveCacheDeleteMode(mode: Boolean) {
        dataStore.edit { prefs ->
            prefs[PreferencesKeys.CACHE_DELETE_MODE] = mode
        }
    }

    // WorkManager 상태 저장할 DataStore
    override suspend fun getCacheDeleteMode(): Flow<Boolean> {
        return dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    exception.printStackTrace()
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { prefs ->
                prefs[PreferencesKeys.CACHE_DELETE_MODE] ?: false
            }
    }


    // Paging(Room으로 favorite fragment 페이징)
    override fun getFavoritePagingBooks(): Flow<PagingData<Book>> {
        val pagingSourceFactory = { db.bookSearchDao().getFavoritePagingBooks() }

        // 페이징 설정
        return Pager(
            config = PagingConfig(
                pageSize = PAGING_SIZE,
                enablePlaceholders = false, // 페이징 수만큼만 출력
                maxSize = PAGING_SIZE * 3 // Pager가 메모리에 가질 수 있는 최대 항목수
            ),
            pagingSourceFactory = pagingSourceFactory // Response 넣어주기
        ).flow // return 값을 flow객체로 만들어줌
    }

    // Pager가 BookSearchPagingSource 결과를 PagingData 로 변환
    override fun searchBooksPaging(query: String, sort: String): Flow<PagingData<Book>> {
        val pagingSourceFactory = { BookSearchPagingSource(api, query, sort) }

        return Pager(
            config = PagingConfig(
                pageSize = PAGING_SIZE,
                enablePlaceholders = false,
                maxSize = PAGING_SIZE * 3
            ),
            pagingSourceFactory = pagingSourceFactory // Response는 팩토리에 넣은 다음 flow를 반환
        ).flow
    }


}