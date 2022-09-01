package com.kjk.booksearchapp.data.api

// 서비스를 사용하기 위한 retrofit 인스턴스
// BookSearchApi Interface를 실행해주는 인스턴스
// Hilt(AppModule)에서 구현하였으므로 주석
//object RetrofitInstance {
//
//    private val okHttpClient: OkHttpClient by lazy {
//        val httpLoggingInterceptor = HttpLoggingInterceptor()
//            .setLevel(HttpLoggingInterceptor.Level.BODY)
//        OkHttpClient.Builder()
//            .addInterceptor(httpLoggingInterceptor)
//            .build()
//    }
//
//    private val retrofit: Retrofit by lazy {
//        Retrofit.Builder() /* 빌더 패턴으로 retrofit 객체 생성 */
//            .addConverterFactory(MoshiConverterFactory.create()) /* DTO 변환에 사용되는 Moshi를 JSON converter로 지정 */
//            .client(okHttpClient) /* retrofit client 속성에 okHttpClient interceptor를 넣어 logcat으로 패킷 모니터링, okhttp는 프록시처럼 중간에서 intercept 가능 */
//            .baseUrl(BASE_URL) /* URL 전달 */
//            .build() /* build로 객체 생성 */
//    }
//
//    // retrofit.create로 BookSearchApi 인스턴스 생성 => API 사용 준비 완료
//    val api: BookSearchApi by lazy {
//        retrofit.create(BookSearchApi::class.java)
//    }
//
//}

// object & lazy : 실제 사용되는 순간에 객체 생성(lazy), 통신에 혼선을 막기 위해 싱글톤 패턴 구현(object)
// object로 클래스를 정의하면, 싱클턴(Singleton) 패턴이 적용되어 객체가 한번만 생성되도록 함
// lazy 는 실제 사용될 때 인스턴스가 생성된다.