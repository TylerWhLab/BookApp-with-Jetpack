package com.kjk.booksearchapp.ui.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.kjk.booksearchapp.R
import com.kjk.booksearchapp.databinding.FragmentSettingsBinding
import com.kjk.booksearchapp.ui.viewmodel.SettingsViewModel
import com.kjk.booksearchapp.util.Sort
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingsFragment : Fragment() {
    // viewBinding 인식
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    //    private lateinit var bookSearchViewModel: BookSearchViewModel // No Hilt DI
    // Hilt DI, by activityViewModels로 뷰모델 생성(=주입 하는 것)
    // private val bookSearchViewModel by activityViewModels<BookSearchViewModel>() // viewModel 분리 전

    // viewModel을 화면별로 분리(관심사로 분리)
    private val settingsViewModel by viewModels<SettingsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // viewBinding 지정
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // MainActivity에서 뷰모델 받기
        // activity에서 뷰모델 생성하던 작업을 Hilt가 하게되므로 여기는 주석 => 모든 fragment에 동일하게 적용
        // bookSearchViewModel = (activity as MainActivity).bookSearchViewModel

        saveSettings()
        loadSettings()
        showWorkStatus() // WorkManager
    }

    // 체크된 버튼 값 받아 DataStore에 저장
    private fun saveSettings() {
        binding.rgSort.setOnCheckedChangeListener { _, checkedId ->
            val value = when (checkedId) {
                R.id.rb_accuracy -> Sort.ACCURACY.value
                R.id.rb_latest -> Sort.LATEST.value
                else -> return@setOnCheckedChangeListener
            }
            settingsViewModel.saveSortMode(value) // DataStore에 저장
        }

        // WorkManager
        binding.swCacheDelete.setOnCheckedChangeListener { _, isChecked ->
            settingsViewModel.saveCacheDeleteMode(isChecked)
            if (isChecked) { // 버튼이 눌렸으면
                settingsViewModel.setWork()
            } else {
                settingsViewModel.deleteWork() // 작업을 삭제
            }
        }
    }

    // DataStore 값 가져와 라디오 버튼에 적용
    private fun loadSettings() {
        lifecycleScope.launch {
            val buttonId = when (settingsViewModel.getSortMode()) {
                Sort.ACCURACY.value -> R.id.rb_accuracy
                Sort.LATEST.value -> R.id.rb_latest
                else -> return@launch
            }
            binding.rgSort.check(buttonId)
        }

        // WorkManager, 캐시 버튼에 활성 여부를 반영
        lifecycleScope.launch {
            val mode = settingsViewModel.getCacheDeleteMode()
            binding.swCacheDelete.isChecked = mode
        }

    }

    // 반환받은 라이브데이터 작업상태를 표시
    private fun showWorkStatus() {
        settingsViewModel.getWorkStatus().observe(viewLifecycleOwner) { workInfo ->
            Log.d("WorkManager", workInfo.toString())
            if (workInfo.isEmpty()) { // observe로 구독하는데 처음엔 값이 없음
                binding.tvWorkStatus.text = "No works"
            } else {
                binding.tvWorkStatus.text = workInfo[0].state.toString() // WorkManager 현재 상태
            }
        }
    }

    override fun onDestroyView() {
        // 필요 없으면 viewBinding null 처리
        _binding = null
        super.onDestroyView()
    }
}