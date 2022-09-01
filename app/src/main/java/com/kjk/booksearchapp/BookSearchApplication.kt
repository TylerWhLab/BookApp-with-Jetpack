package com.kjk.booksearchapp

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class BookSearchApplication : Application(), Configuration.Provider { // Configuration.Provider를 구현

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    // getWorkManagerConfiguration() 함수를 정의하면
    // 워커 클래스가 HiltWorkerFactory를 통해 생성됨
    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
    }
}