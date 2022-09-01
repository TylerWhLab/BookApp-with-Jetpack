package com.kjk.booksearchapp.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.kjk.booksearchapp.databinding.FragmentFavoriteBinding
import com.kjk.booksearchapp.ui.adapter.BookSearchPagingAdapter
import com.kjk.booksearchapp.ui.viewmodel.FavoriteViewModel
import com.kjk.booksearchapp.util.collectLatestStateFlow
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavoriteFragment : Fragment() {

    // viewBinding 인식
    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!

    // Room data 가져오기 위해 뷰모델 사용
    // private lateinit var bookSearchViewModel: BookSearchViewModel // viewModel 분리 전

    // viewModel을 화면별로 분리(관심사로 분리)
    private val favoriteViewModel by viewModels<FavoriteViewModel>()

    // 리사이클러뷰 어댑터
//    private lateinit var bookSearchAdapter: BookSearchAdapter

    // 페이징 적용한 리사이클러뷰 어댑터
    private lateinit var bookSearchAdapter: BookSearchPagingAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // viewBinding 지정
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 뷰모델 가져오기
        // activity에서 뷰모델 생성하던 작업을 Hilt가 하게되므로 여기는 주석 => 모든 fragment에 동일하게 적용
        // bookSearchViewModel = (activity as MainActivity).bookSearchViewModel

        setupRecyclerView()
        setupTouchHelper(view)

        // 뷰모델의 favoriteBooks를 observe해서 리사이클러뷰 갱신
        // LiveData는 observe 사용
//        bookSearchViewModel.favoriteBooks.observe(viewLifecycleOwner) {
//            bookSearchAdapter.submitList(it)
//        }

        // Flow 구독
        // flow는 launch(코루틴) 안에서 collectLatest or collect 함수로 데이터를 구독해야함
//        lifecycleScope.launch {
//            bookSearchViewModel.favoriteBooks.collectLatest {
//                bookSearchAdapter.submitList(it)
//            }
//        }

        // stateFlow 구독
        // favoriteBooks를 stateFlow로 변환해서 flow 동작을 FavoriteFragment의 라이프사이클과 동기화
//        viewLifecycleOwner.lifecycleScope.launch {
//            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
//                bookSearchViewModel.favoriteBooks.collectLatest {
//                    bookSearchAdapter.submitList(it)
//                }
//            }
//        }

        // stateFlow 구독, 바로 위처럼 보일러 플레이트가 길면 확장함수 만들어(util>Extensions.kt) 사용
//        collectLatestStateFlow(bookSearchViewModel.favoriteBooks) {
//            bookSearchAdapter.submitList(it)
//        }

        // 페이징된 데이터(favoritePagingBooks) 구독
        // collectLatestStateFlow 사용하여 기존 페이징 값 캔슬하고 새 값 구독 => 페이징 제대로 작동
        collectLatestStateFlow(favoriteViewModel.favoritePagingBooks) {
            bookSearchAdapter.submitData(it)
        }

    }

    // 리사이클러뷰
    private fun setupRecyclerView() {
//        bookSearchAdapter = BookSearchAdapter()
        bookSearchAdapter = BookSearchPagingAdapter() // 페이징을 적용한 리사이클러뷰
        binding.rvFavoriteBooks.apply {
            setHasFixedSize(true)
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    DividerItemDecoration.VERTICAL
                )
            )
            adapter = bookSearchAdapter
        }

        // 전달 받은 데이터로 웹뷰 출력
        bookSearchAdapter.setOnItemClickListener {
            val action = FavoriteFragmentDirections.actionFragmentFavoriteToFragmentBook(it)
            findNavController().navigate(action)
        }
    }

    // data 삭제(왼쪽으로 스와이프하면 삭제), 스와이프에 SimpleCallback 시용
    private fun setupTouchHelper(view: View) {
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            0, ItemTouchHelper.LEFT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                // onMove는 사용하지 않으므로 return true
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                // 터치한 뷰홀더 위치
                val position = viewHolder.bindingAdapterPosition
//                // 어댑터에 전달하여 터치한 book 정보 가져오기
//                val book = bookSearchAdapter.currentList[position]
//                // 해당 책 정보 삭제
//                bookSearchViewModel.deleteBook(book)
//                Snackbar.make(view, "Book has deleted", Snackbar.LENGTH_SHORT).apply {
//                    // undo 기능
//                    setAction("Undo") {
//                        bookSearchViewModel.saveBook(book)
//                    }
//                }.show()

                // 페이징 적용
                // null일 수 있기 때문에 currentList에서 peek으로 변경
                val pagedBook = bookSearchAdapter.peek(position)

                // null 처리
                pagedBook?.let { book ->
                    favoriteViewModel.deleteBook(book)
                    Snackbar.make(view, "Book has deleted", Snackbar.LENGTH_SHORT).apply {
                        setAction("Undo") {
                            favoriteViewModel.saveBook(book)
                        }
                    }.show()
                }

            }
        }
        ItemTouchHelper(itemTouchHelperCallback).apply {
            // 리사이클러뷰와 연결하여 스와이프, 드래그 동작을 인식
            // 스와이프할 방향은 위에 ItemTouchHelper.LEFT으로 지정
            // 드래그를 사용하지 않으려면 dragDirs를 0으로 지정
            attachToRecyclerView(binding.rvFavoriteBooks)
        }
    }


    override fun onDestroyView() {
        // 필요 없으면 viewBinding null 처리
        _binding = null
        super.onDestroyView()
    }

}