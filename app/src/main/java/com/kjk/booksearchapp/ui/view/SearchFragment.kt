package com.kjk.booksearchapp.ui.view

import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.kjk.booksearchapp.databinding.FragmentSearchBinding
import com.kjk.booksearchapp.ui.adapter.BookSearchLoadStateAdapter
import com.kjk.booksearchapp.ui.adapter.BookSearchPagingAdapter
import com.kjk.booksearchapp.ui.viewmodel.SearchViewModel
import com.kjk.booksearchapp.util.Constants.SEARCH_BOOKS_TIME_DELAY
import com.kjk.booksearchapp.util.collectLatestStateFlow
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : Fragment() {
    // viewBinding 인식
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    // 메인 액티비에서 초기화한 뷰 모델 가져오기
//    private lateinit var bookSearchViewModel: BookSearchViewModel // No Hilt DI
    // Hilt DI, by activityViewModels로 뷰모델 생성(=주입 하는 것)
    // private val bookSearchViewModel by activityViewModels<BookSearchViewModel>() // viewModel 분리 전

    // viewModel을 화면별로 분리(관심사로 분리)
    private val searchViewModel by viewModels<SearchViewModel>()

    // Recyclerview 설정(no paging)
    // private lateinit var bookSearchAdapter: BookSearchAdapter

    // Recyclerview 설정(Paging)
    private lateinit var bookSearchAdapter: BookSearchPagingAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // viewBinding 지정
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 메인 액티비에서 초기화한 뷰 모델 가져오기
        // activity에서 뷰모델 생성하던 작업을 Hilt가 하게되므로 여기는 주석 => 모든 fragment에 동일하게 적용
        // bookSearchViewModel = (activity as MainActivity).bookSearchViewModel

        // Recyclerview 설정
        setupRecyclerView()

        // search edit 기능
        searchBooks()

        // BookSearchViewModel 내 searchResult 값 변경 감시(paging 미적용)
//        bookSearchViewModel.searchResult.observe(viewLifecycleOwner) { response ->
//            val books = response.documents
//            bookSearchAdapter.submitList(books)
//        }

        collectLatestStateFlow(searchViewModel.searchPagingResult) {
            bookSearchAdapter.submitData(it)
        }

    }

    // Recyclerview 설정
    private fun setupRecyclerView() {

        // paging 미적용 리사이클러뷰 어댑터
        // bookSearchAdapter = BookSearchAdapter()

        // Paging 적용 리사이클러뷰 어댑터
        bookSearchAdapter = BookSearchPagingAdapter()

        binding.rvSearchResult.apply {
            setHasFixedSize(true)
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    DividerItemDecoration.VERTICAL
                )
            )

            // 로딩 상태 없을 때 어댑터
            // adapter = bookSearchAdapter

            // BookSearchPagingAdapter 에 BookSearchLoadStateAdapter 연결하여 리사이클러뷰 헤더or푸터에 로딩상태 출력
            // 리사이클러뷰에서 어댑터를 셋업할 때, withLoadStateFooter 이용하여 연결
            adapter = bookSearchAdapter.withLoadStateFooter(
                footer = BookSearchLoadStateAdapter(bookSearchAdapter::retry)
            )

        }

        // 리스트뷰 클릭해서 상세(웹뷰로 구현한 bookfragment)보기
        bookSearchAdapter.setOnItemClickListener {
            val action = SearchFragmentDirections.actionFragmentSearchToFragmentBook(it)
            findNavController().navigate(action)
        }

    }

    // search edit 기능
    private fun searchBooks() {
        var startTime = System.currentTimeMillis()
        var endTime: Long

        // savestate에서 검색어 가져오기
        binding.etSearch.text =
            Editable.Factory.getInstance().newEditable(searchViewModel.query)

        Log.d("SearchFragment:searchBooks()", "searchBooks: ${binding.etSearch.text}")

        // xml 내 TextInputEditText(검색창) 컨트롤
        // 입력값이 들어오면 BookSearchViewModel 내 searchResult 값이 갱신, searchResult 값은 onViewCreated에서 감시
        binding.etSearch.addTextChangedListener { text: Editable? ->
            endTime = System.currentTimeMillis()
            if (endTime - startTime >= SEARCH_BOOKS_TIME_DELAY) { // 입력 후 100ms 지나면 검색
                text?.let {
                    val query = it.toString().trim()
                    if (query.isNotEmpty()) {
                        // paging 미적용
                        // bookSearchViewModel.searchBooks(query)

                        // Paging
                        searchViewModel.searchBooksPaging(query)

                        // savedstate 사용하기 위해 뷰모델에 query 변수 선언해둠
                        searchViewModel.query = query // 뷰모델에 검색어 저장
                    }
                }
            }
            startTime = endTime
        }
    }


    // Retrofit 로딩 상태 출력
    private fun setupLoadState() {
        bookSearchAdapter.addLoadStateListener { combinedLoadStates ->
            // combinedLoadStates 에는 페이징소스와 리모트미디에이터 2가지 소스의 로딩 상태를 가짐(여기서는 소스만)
            // LoadStates에는 로딩 시작시에 만들어지는 prepend, 종료 시 append, 로딩 갱신 refresh 3개의 속성을 가짐
            // combinedLoadStates를 받아온 다음 아래 처리
            val loadState = combinedLoadStates.source

            // 빈 상태 : 아이템이 1개 미만, NotLoading, endOfPaginationReached
            val isListEmpty = bookSearchAdapter.itemCount < 1
                    && loadState.refresh is LoadState.NotLoading
                    && loadState.append.endOfPaginationReached

            // No result 출력
            binding.tvEmptylist.isVisible = isListEmpty
            // 리사이클러뷰 숨김
            binding.rvSearchResult.isVisible = !isListEmpty

            binding.progressBar.isVisible = loadState.refresh is LoadState.Loading

            // 아래(에러상태)는 BookSearchLoadStateViewHolder 로 이동
//            binding.btnRetry.isVisible = loadState.refresh is LoadState.Error
//                    || loadState.append is LoadState.Error
//                    || loadState.prepend is LoadState.Error
//            val errorState: LoadState.Error? = loadState.append as? LoadState.Error
//                ?: loadState.prepend as? LoadState.Error
//                ?: loadState.refresh as? LoadState.Error
//            errorState?.let { // 에러 시 토스트
//                Toast.makeText(requireContext(), it.error.message, Toast.LENGTH_SHORT).show()
//            }
        }

        // 페이징 어댑터 갱신
//        binding.btnRetry.setOnClickListener {
//            bookSearchAdapter.retry()
//        }
    }


    override fun onDestroyView() {
        // 필요 없으면 viewBinding null 처리
        _binding = null
        super.onDestroyView()
    }
}