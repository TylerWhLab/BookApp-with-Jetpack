package com.kjk.booksearchapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.kjk.booksearchapp.data.model.Book
import com.kjk.booksearchapp.databinding.ItemBookPreviewBinding

// 뷰 홀더(BookSearchViewHolder)를 재이용하는 어댑터
class BookSearchAdapter : ListAdapter<Book, BookSearchViewHolder>(BookDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookSearchViewHolder {
        return BookSearchViewHolder(
            ItemBookPreviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    // 데이터 바인딩
    override fun onBindViewHolder(holder: BookSearchViewHolder, position: Int) {
        val book = currentList[position]
        holder.bind(book)

        // 리스트뷰 클릭해서 상세(웹뷰로 구현한 bookfragment)보기
        holder.itemView.setOnClickListener {
            onItemClickListener?.let { it(book) }
        }
    }

    // 리스트뷰 클릭해서 상세(웹뷰로 구현한 bookfragment)보기
    private var onItemClickListener: ((Book) -> Unit)? = null
    fun setOnItemClickListener(listener: (Book) -> Unit) {
        onItemClickListener = listener
    }

    // DiffUtil 작동을 위한 콜백
    companion object {
        private val BookDiffCallback = object : DiffUtil.ItemCallback<Book>() {
            override fun areItemsTheSame(oldItem: Book, newItem: Book): Boolean {
                return oldItem.isbn == newItem.isbn
            }

            override fun areContentsTheSame(oldItem: Book, newItem: Book): Boolean {
                return oldItem == newItem
            }
        }
    }

}