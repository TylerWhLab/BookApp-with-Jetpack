package com.kjk.booksearchapp.ui.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.kjk.booksearchapp.R
import com.kjk.booksearchapp.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint // Hilt 사용을 위해 붙인 어노테이션
class MainActivity : AppCompatActivity() {

    // viewBinding 설정, databinding보다 빠름
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    // viewModel 초기화
    // lateinit var bookSearchViewModel: BookSearchViewModel // fragment 별 뷰모델을 생성했기 때문에 주석

    // Navigation 설정
    private lateinit var navController: NavController

    // 상단 바
//    private lateinit var appBarConfiguration: AppBarConfiguration

    // DataStore를 싱클톤 객체로 생성
    // Hilt DI 사용하므로 의존성 객체를 초기화하던 부분은 모두 주석
    // private val Context.dataStore by preferencesDataStore(DATASTORE_NAME)

    // workManager 객체 전달
    // Hilt DI 사용하므로 의존성 객체를 초기화하던 부분은 모두 주석
    // private val workManager = WorkManager.getInstance(application)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root) // viewBinding 설정

        // Jetpack Navigation 안쓰면 이런식으로 쓴다.
        // setupBottomNavigationView()

        // 앱 최초 실행 시에만 SearchFragment 표시, 액티비티 재생성 시 첫 번째 화면 표시할 필요 없기 때문
//        if (savedInstanceState == null) { // 앱 최초 실행인 경우
//            binding.bottomNavigationView.selectedItemId = R.id.fragment_search
//        }

        // Jetpack Navigation
        setupJetpackNavigation()


        // viewModel 초기화
        // Hilt DI 사용하므로 의존성 객체를 초기화하던 부분은 모두 주석
//        val database = BookSearchDatabase.getInstance(this) // Room
//        val bookSearchRepository = BookSearchRepositoryImpl(database, dataStore)
//        val factory = BookSearchViewModelProviderFactory(bookSearchRepository, workManager, this)
//        bookSearchViewModel = ViewModelProvider(this, factory)[BookSearchViewModel::class.java]


    }

    // Navigation 작동에 필요한 설정
    private fun setupJetpackNavigation() {
        // Navigation Controller Instance
        val host = supportFragmentManager
            .findFragmentById(R.id.booksearch_nav_host_fragment) as NavHostFragment? ?: return
        navController = host.navController

        // setupWithNavController 사용하여 bottomNavigationView와 navController 연결 => Navigation 작동
        binding.bottomNavigationView.setupWithNavController(navController)

        // 상단 바 text를 프레그먼트별로 지정
//        appBarConfiguration = AppBarConfiguration(
//            setOf(
//                R.id.fragment_search, R.id.fragment_favorite, R.id.fragment_settings
//                // 모든 프레그먼트를 top level fragment로 지정하여 뒤로가기 버튼(search books로 이동) 제거
//            )
//        )
//        setupActionBarWithNavController(navController, appBarConfiguration)
    }

//    override fun onSupportNavigateUp(): Boolean {
//        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
//    }


    // 하단 네비게이션 기능 구현(fragment로 이동) => Jetpack Navigation 안쓰면 이런식으로 쓴다.
//    private fun setupBottomNavigationView() {
//        binding.bottomNavigationView.setOnItemSelectedListener { item ->
//            when (item.itemId) {
//                R.id.fragment_search -> {
//                    supportFragmentManager.beginTransaction()
//                        .replace(R.id.frame_layout, SearchFragment())
//                        .commit()
//                    true
//                }
//                R.id.fragment_favorite -> {
//                    supportFragmentManager.beginTransaction()
//                        .replace(R.id.frame_layout, FavoriteFragment())
//                        .commit()
//                    true
//                }
//                R.id.fragment_settings -> {
//                    supportFragmentManager.beginTransaction()
//                        .replace(R.id.frame_layout, SettingsFragment())
//                        .commit()
//                    true
//                }
//                else -> false
//
//            }
//        }
//    }

}