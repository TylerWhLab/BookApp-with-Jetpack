plugins {
    id(Plugins.ANDROID_APPLICATION)
    id(Plugins.KOTLIN_ANDROID)
    id(Plugins.KAPT)

    // API key 숨기기 위한 plugin, API key는 local.properties에 넣음
    id(Plugins.SECRETS_GRADLE_PLUGIN)

    // fragment간 data 통신
    id(Plugins.SAFEARGS)

    // 직렬화해주는 파설러블(Parcelable), serializer보다 빠르다.
    id(Plugins.PARCELIZE)

    // Hilt
    id(Plugins.HILT_PLUGIN)

}

android {
    compileSdk = 32

    defaultConfig {
        applicationId = "com.kjk.booksearchapp"
        minSdk = 23 // 21
        targetSdk = 32
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        named("release") {
//            minifyEnabled false
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
//        release {
//            minifyEnabled false
//            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
//        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }

//    viewBinding enable
    buildFeatures {
        viewBinding = true
    }

    // kapt가 에러 타입을 알아서 판단
    kapt {
        correctErrorTypes = true
    }

}

dependencies {

    implementation(Dependencies.CORE_KTX)
    implementation(Dependencies.APP_COMPAT)
    implementation(Dependencies.MATERIAL)
    implementation(Dependencies.CONSTRAINT_LAYOUT)
    testImplementation(Testing.JUNIT4)
    androidTestImplementation(Testing.ANDROID_JUNIT)
    androidTestImplementation(Testing.ESPRESSO_CORE)

    // Retrofit : API 사용에 필요(http request를 인터페이스로 정의해서 사용 => data.api 패키지 내 api key와 인자를 전달받아 request 수행하는 서비스 생성)
    implementation(Dependencies.RETROFIT)
    implementation(Dependencies.RETROFIT_CONVERTER_MOSHI)
    // Moshi : JSON Response 처리, JSON To DTO
    implementation(Dependencies.MOSHI)
    kapt(Dependencies.MOSHI_KAPT)
    // Okhttp : 로깅 인터셉터, retrofit 이전에 많이 사용했었다.
    implementation(Dependencies.OKHTTP)
    implementation(Dependencies.OKHTTP_LOGGING_INTERCEPTOR)


    // Lifecycle(viewModel에 필요)
    implementation(Dependencies.LIFECYCLE_VIEWMODEL_KTX)
    implementation(Dependencies.LIFECYCLE_RUNTIME_KTX)
    implementation(Dependencies.LIFECYCLE_VIEWMODEL_KTX)
    // viewmodel만으론 앱 재시작 시 데이터를 유지할 수 없기 때문에 savedstate 추가

    // Coroutine(viewModel에 필요)
    implementation(Dependencies.COROUTINE_CORE)
    implementation(Dependencies.COROUTINE_ANDROID)

    // Coil : 이미지 표시
    implementation(Dependencies.COIL)

    // Recyclerview : response를 UI에 출력
    implementation(Dependencies.RECYCLERVIEW)

    // Navigation
    implementation(Dependencies.NAVIGATION_FRAGMENT_KTX)
    implementation(Dependencies.NAVIGATION_UI_KTX)

    // Room(SQLite 안전하게 조작)
    implementation(Dependencies.ROOM_RUNTIME)
    implementation(Dependencies.ROOM_KTX)
    kapt(Dependencies.ROOM_KAPT)
    // Room Paging
    implementation(Dependencies.ROOM_PAGING)

    // Kotlin serialization
    implementation(Dependencies.KOTLIN_SERIALIZATION)

    // DataStore
    implementation(Dependencies.PREFERENCES_DATASTORE)

    // Paging
    implementation(Dependencies.PAGING)

    // WorkManager
    implementation(Dependencies.WORKMANGER)

    // Hilt
    implementation(Dependencies.DAGGER_HILT)
    kapt(Dependencies.DAGGER_HILT_KAPT)

    // ViewModel delegate
    // delegate 패턴으로 뷰모델을 초기화하기 위한 dependency
    // 팩토리 없이도 뷰모델 생성 가능하게 해줌
    implementation(Dependencies.ACTIVITY_KTX)
    implementation(Dependencies.FRAGMENT_KTX)

    // Hilt extension
    // 서비스를 관리하는 클래스이므로 다른 객체와 다르게 Hilt Extension 사용해야함
    implementation(Dependencies.HILT_EXTENSION_WORK)
    kapt(Dependencies.HILT_EXTENSION_KAPT)
}