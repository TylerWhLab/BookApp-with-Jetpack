package com.kjk.booksearchapp.ui.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.kjk.booksearchapp.databinding.FragmentBookBinding
import com.kjk.booksearchapp.ui.viewmodel.BookViewModel
import dagger.hilt.android.AndroidEntryPoint

// SearchFragment에서 클릭(상세보기)하면 나오는 웹뷰
@AndroidEntryPoint // 의존성 주입 가능한 scope로 만들저무
class BookFragment : Fragment() {
    private var _binding: FragmentBookBinding? = null
    private val binding get() = _binding!!

    // args(전달 받은 값) 초기화
    private val args by navArgs<BookFragmentArgs>()

    // Room
    // private lateinit var bookSearchViewModel: BookSearchViewModel // No Hilt DI
    // Hilt DI, by activityViewModels로 뷰모델 생성(=주입 하는 것)
    // private val bookSearchViewModel by activityViewModels<BookSearchViewModel>() // viewModel 분리 전

    // viewModel을 화면별로 분리(관심사로 분리)
    private val bookViewModel by viewModels<BookViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBookBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Room, activity가 뷰모델 생성
        // activity에서 뷰모델 생성하던 작업을 Hilt가 하게되므로 여기는 주석 => 모든 fragment에 동일하게 적용
        // bookSearchViewModel = (activity as MainActivity).bookSearchViewModel

        // 전달받은 값을 웹뷰에 매핑
        val book = args.book
        binding.webview.apply {
            webViewClient = WebViewClient()
            settings.javaScriptEnabled = true
            loadUrl(book.url)
        }

        // Room, 버튼 클릭 시 전달 받은 책 정보를 save
        binding.fabFavorite.setOnClickListener {
            bookViewModel.saveBook(book)
            Snackbar.make(view, "Book has saved", Snackbar.LENGTH_SHORT).show()
        }

    }

    // 라이프사이클에 따른 웹뷰 동작 정의
    // http://orhanobut.github.io/effective-android/ 37 참고
    override fun onPause() {
        binding.webview.onPause()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        binding.webview.onResume()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}