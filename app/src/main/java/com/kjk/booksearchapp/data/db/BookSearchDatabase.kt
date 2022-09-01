package com.kjk.booksearchapp.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.kjk.booksearchapp.data.model.Book

// Room에서 사용할 엔티티와 db 버전 등
@Database(
    entities = [Book::class],
    version = 1,
    exportSchema = false
)

// 컨버터 등록해두면 Room이 컨버터가 필요한 상황에 알아서 컨버터 사용
@TypeConverters(OrmConverter::class)
// Room 스펙에 따라 abstract로 만들어 준것
abstract class BookSearchDatabase : RoomDatabase() {

    // Room에서 사용할 Dao 지정
    abstract fun bookSearchDao(): BookSearchDao

    // 싱글톤, DB객체는 생성 비용이 크므로 중복 생성 방지
    // Hilt에서 구현하였으므로 주석(AppModule)
//    companion object {
//        @Volatile
//        private var INSTANCE: BookSearchDatabase? = null
//
//        private fun buildDatabase(context: Context): BookSearchDatabase =
//            Room.databaseBuilder(
//                context.applicationContext,
//                BookSearchDatabase::class.java,
//                "favorite-books"
//            ).build()
//
//        fun getInstance(context: Context): BookSearchDatabase =
//            INSTANCE ?: synchronized(this) {
//                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
//            }
//    }
}