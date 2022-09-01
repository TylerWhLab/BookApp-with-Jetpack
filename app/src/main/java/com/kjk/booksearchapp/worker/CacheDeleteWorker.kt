package com.kjk.booksearchapp.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

// 백그라운드 작업 내용을 담은 class
@HiltWorker // 워커가 Hilt 의존성을 주입받을 수 있도록 만든다.
class CacheDeleteWorker @AssistedInject constructor(
    @Assisted context: Context, // @Assisted 이용해 주입
    @Assisted workerParams: WorkerParameters,
    private val cacheDeleteResult: String
    /* AppModule 내 provideCacheDeleteResult() 함수의 return 값을 이렇게 주입받음
    * 워커는 싱글턴 컴포넌트안에 설치된 의존성만을 주입받을 수 있다. AppModule은 @InstallIn(싱글톤컴포넌트) 이므로 이렇게 주입받을 수 있는 것
    * */
) : Worker(context, workerParams) { // Worker 상속

    override fun doWork(): Result { // doWork 내 백그라운드 작업 정의
        return try {
//            Log.d("WorkManager", "Cache has successfully deleted") // 작업 했다 치고
            Log.d("WorkManager", cacheDeleteResult)
            Result.success()
        } catch (exception: Exception) {
            exception.printStackTrace()
            Result.failure()
        }
    }
}