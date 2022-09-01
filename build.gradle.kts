// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id(Plugins.ANDROID_APPLICATION) version Versions.AGP apply false
    id(Plugins.ANDROID_LIBRARY) version Versions.AGP apply false
    id(Plugins.KOTLIN_ANDROID) version Versions.KOTLIN apply false

    // API key 숨기기 위한 plugin, API key는 local.properties에 넣음
    id(Plugins.SECRETS_GRADLE_PLUGIN) version Versions.SECRETS_GRADLE apply false

    // fragment간 data 통신
    id(Plugins.SAFEARGS) version Versions.NAVIGATION apply false

    // 박스드 타입을 프리미티브 타입으로 변환
    id(Plugins.KOTLIN_SERIALIZATION) version Versions.KOTLIN apply false
    
    // Hilt
    id(Plugins.DAGGER_HILT) version Versions.HILT apply false

}

// 그루비
//task clean(type: Delete) {
//    delete rootProject.buildDir
//}

// KTS
tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
}