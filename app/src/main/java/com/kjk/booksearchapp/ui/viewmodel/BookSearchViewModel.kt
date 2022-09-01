package com.kjk.booksearchapp.ui.viewmodel

//@HiltViewModel // 현재 뷰모델을 의존성 주입 가능한 Scope로 만든다.
//class BookSearchViewModel @Inject constructor(
//    /* @Inject constructor 붙여서 모듈에서 만들어준 BookSearchRepository, WorkManager 객체 주입 */
//    private val bookSearchRepository: BookSearchRepository,
//    private val workManager: WorkManager,
//    private val savedStateHandle: SavedStateHandle, // viewmodel만으론 앱 재시작 시 데이터를 유지할 수 없기 때문에 savedstate 추가
//    // SavedStateHandle는 모듈 설정 없이도 자동 주입된다.
//) : ViewModel() {
//
//    // 수정 가능한 MutableLiveData
//    private val _searchResult = MutableLiveData<SearchResponse>()
//
//    // 수정할 수 없는 live data
//    val searchResult: LiveData<SearchResponse> get() = _searchResult
//
//    // repository에서 정의된 함수 searchBooks를 코루틴 내부에서 사용하는 함수
//    // corutine으로(비동기) request
//    fun searchBooks(query: String) = viewModelScope.launch(Dispatchers.IO) {
//        // val response = bookSearchRepository.searchBooks(query, "accuracy", 1, 15)
//        val response = bookSearchRepository.searchBooks(query, getSortMode(), 1, 15) // DataStore
//        if (response.isSuccessful) {
//            response.body()?.let { body ->
//                // _searchResult 에 response 저장 후, 최종 결과는 searchResult에 저장
//                _searchResult.postValue(body)
//            }
//        }
//    }
//
//    // Room method
//    // suspend 함수는 viewModelScope 안에서 실행, viewModelScope의 기본 디스패처가 Main이기 때문에
//    // 여기서는 File IO를 수행하는 IO로 변경하여 사용
//    fun saveBook(book: Book) = viewModelScope.launch(Dispatchers.IO) {
//        bookSearchRepository.insertBooks(book)
//    }
//
//    fun deleteBook(book: Book) = viewModelScope.launch(Dispatchers.IO) {
//        bookSearchRepository.deleteBooks(book)
//    }
//
//    // LiveDate
//    // val favoriteBooks: LiveData<List<Book>> = bookSearchRepository.getFavoriteBooks()
//
//    // Flow
//    // val favoriteBooks: Flow<List<Book>> = bookSearchRepository.getFavoriteBooks()
//
//    // stateFlow : stateIn 사용하여 flow type을 stateFlow로 변환
//    val favoriteBooks: StateFlow<List<Book>> = bookSearchRepository.getFavoriteBooks()
//        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), listOf())
//    // stateIn(scope, 구독 시작하는 시작 시점, 반환타입이 List(StateFlow<List<Book>>)이므로 listOf())
//
//
//    // SavedState
//    var query = String()
//        set(value) {
//            field = value
//            savedStateHandle.set(SAVE_STATE_KEY, value) // savedState에 저장(검색어가 변경되면 바로 반영)
//        }
//
//    // viewModel 초기화
//    init {
//        query = savedStateHandle.get<String>(SAVE_STATE_KEY) ?: "" // savedstate에서 값을 가져오는데, 없다면 ""
//    }
//    // 뷰모델에 savedStateHandle 생성자를 추가했기 때문에, 뷰모델 프로바이더도 변경
//
//    companion object {
//        private const val SAVE_STATE_KEY = "query"
//        private const val WORKER_KEY = "cache_worker" // WorkManager 작업에 TAG로 사용할 WORKER_KEY
//    }
//
//
//    // DataStore
//    fun saveSortMode(value: String) = viewModelScope.launch(Dispatchers.IO) {
//        // file io 이므로 Dispatchers.IO에서 실행되도록 지정
//        bookSearchRepository.saveSortMode(value)
//    }
//
//    suspend fun getSortMode() = withContext(Dispatchers.IO) {
//        bookSearchRepository.getSortMode().first()
//        // first() : 전체 데이터 스트림을 구독할 필요없이(설정 화면이므로), Flow에서 단일 스트링만 가져오기
//        // withContext 내부 코드는 반드시 값을 반환하고 종료
//    }
//
//    // WorkManager DataStore
//    fun saveCacheDeleteMode(value: Boolean) = viewModelScope.launch(Dispatchers.IO) {
//        bookSearchRepository.saveCacheDeleteMode(value)
//    }
//
//    suspend fun getCacheDeleteMode() = withContext(Dispatchers.IO) {
//        bookSearchRepository.getCacheDeleteMode().first()
//    }
//
//
//    // Paging Room
//    val favoritePagingBooks: StateFlow<PagingData<Book>> =
//        bookSearchRepository.getFavoritePagingBooks() // Response에 cachedIn을 붙여서 코루틴이 데이터스트림을 캐시하고 공유 가능하게 만든다.
//            .cachedIn(viewModelScope)
//            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), PagingData.empty())
//    // UI에서 감시해야하는 데이터라 stateIn을 써서 stateFlow로 만들어줌
//
//    // Paging Retrofit, 페이징된 데이터 사용
//    private val _searchPagingResult = MutableStateFlow<PagingData<Book>>(PagingData.empty())
//    val searchPagingResult: StateFlow<PagingData<Book>> = _searchPagingResult.asStateFlow()
//
//    // searchBooksPaging 응답을 StateFlow로 표시하기 위해서
//    // _searchPagingResult을 MutableStateFlow 타입으로 준비
//    fun searchBooksPaging(query: String) {
//        viewModelScope.launch {
//            bookSearchRepository.searchBooksPaging(query, getSortMode())
//                .cachedIn(viewModelScope)
//                .collect {
//                    _searchPagingResult.value = it
//                }
//            // 실제 갱신은 _searchPagingResult에서 이뤄지지만, UI에는 searchPagingResult가 출력된다.
//        }
//    }
//
//
//    // WorkManager, SettingsFragment.kt > saveSettings() 에서 호출
//    fun setWork() {
//
//        // 수행 조건
//        val constraints = Constraints.Builder()
//            .setRequiresCharging(true) // 충전 중
//            .setRequiresBatteryNotLow(true) // 배터리 잔량이 적지 않을 때
//            .build()
//
//        val workRequest =
//            PeriodicWorkRequestBuilder<CacheDeleteWorker>(15, TimeUnit.MINUTES) // 15분에 1번 수행
//                .setConstraints(constraints) // 위 제약 추가
//                .build()
//
//        workManager.enqueueUniquePeriodicWork( // enqueueUniquePeriodicWork : 작업 중복 등록 방지
//            WORKER_KEY, ExistingPeriodicWorkPolicy.REPLACE, workRequest // 작업 큐에 전달
//        )
//    }
//
//    // WORKER_KEY 라는 이름을 가진 작업을 찾아 삭제
//    fun deleteWork() = workManager.cancelUniqueWork(WORKER_KEY)
//
//    // 현재 작업 큐 내부에서 WORKER_KEY 이름을 가진 작업의 현재 상태를 LiveData로 반환
//    fun getWorkStatus(): LiveData<MutableList<WorkInfo>> =
//        workManager.getWorkInfosForUniqueWorkLiveData(WORKER_KEY)
//
//
//}
//
//// viewModel은 생성시 초기값을 전달받을 수 없기 때문에 factory 필요 => BookSearchViewModelProviderFactory