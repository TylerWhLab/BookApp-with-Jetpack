package com.kjk.booksearchapp.di

import com.kjk.booksearchapp.data.repository.BookSearchRepository
import com.kjk.booksearchapp.data.repository.BookSearchRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Singleton
    @Binds // BookSearchRepository는 인터페이스이므로 Binds로 Hilt가 의존성 객체를 생성할 수 있도록 설정
    abstract fun bindBookSearchRepository(
        bookSearchRepositoryImpl: BookSearchRepositoryImpl,
    ): BookSearchRepository
}