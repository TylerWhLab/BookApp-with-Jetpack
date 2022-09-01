package com.kjk.booksearchapp.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.room.Room
import androidx.work.WorkManager
import com.kjk.booksearchapp.data.api.BookSearchApi
import com.kjk.booksearchapp.data.db.BookSearchDatabase
import com.kjk.booksearchapp.util.Constants.BASE_URL
import com.kjk.booksearchapp.util.Constants.DATASTORE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module // 의존객체를 담는 Hilt 모듈
@InstallIn(SingletonComponent::class)
object AppModule { // 앱 전체에서 사용할 모듈이므로 싱글톤 컴포넌트에 설치

    /* Retrofit */
    // 로깅에 사용하는 okHttpClient를 주입하는 의존객체
    @Singleton
    @Provides // app 내 어디든 필요한 곳에 주입할 수 있게됨
    fun provideOkHttpClient(): OkHttpClient {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.BODY)
        return OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .build()
    }

    // Retrofit 객체를 작성하는 의존객체
    @Singleton
    @Provides // app 내 어디든 필요한 곳에 주입할 수 있게됨
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create())
            .client(okHttpClient)
            .baseUrl(BASE_URL)
            .build()
    }

    // BookSearchApi 서비스 객체를 작성하는 의존객체
    @Singleton
    @Provides // app 내 어디든 필요한 곳에 주입할 수 있게됨
    fun provideApiService(retrofit: Retrofit): BookSearchApi {
        return retrofit.create(BookSearchApi::class.java)
    }


    /* Room */
    // BookSearchDatabase를 만드는 의존객체(메서드)
    // BookSearchDatabase.kt 내 companion object(객체를 싱글톤으로 수동생성하는 코드) 대체
    @Singleton
    @Provides // app 내 어디든 필요한 곳에 주입할 수 있게됨
    fun provideBookSearchDatabase(@ApplicationContext context: Context): BookSearchDatabase =
        Room.databaseBuilder(
            context.applicationContext,
            BookSearchDatabase::class.java,
            "favorite-books"
        ).build()


    /* DataStore */
    @Singleton
    @Provides // app 내 어디든 필요한 곳에 주입할 수 있게됨
    fun providePreferencesDataStore(@ApplicationContext context: Context): DataStore<Preferences> =

        // PreferenceDataStoreFactory 통해 싱글톤 객체 생성
        PreferenceDataStoreFactory.create(
            produceFile = { context.preferencesDataStoreFile(DATASTORE_NAME) }
        )
    // MainActivity에서 by preferencesDataStore로 하던 객체 생성 작업을 그대로 정의한 것
    // 객체 생성에 필요한 context는 @ApplicationContext으로 주입


    /* WorkManager */
    @Singleton
    @Provides // app 내 어디든 필요한 곳에 주입할 수 있게됨
    fun provideWorkManager(@ApplicationContext context: Context): WorkManager =
        WorkManager.getInstance(context)
    // getInstance로 싱글톤 객체 생성
    // @ApplicationContext으로 context 주입

    // Hilt DI, 워커 내부(CacheDeleteWorker)에 주입할 의존성 정의
    @Singleton
    @Provides // app 내 어디든 필요한 곳에 주입할 수 있게됨
    fun provideCacheDeleteResult(): String = "Cache has deleted by Hilt"
}