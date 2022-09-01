package com.kjk.booksearchapp.ui.adapter

import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.kjk.booksearchapp.data.model.Book
import com.kjk.booksearchapp.databinding.ItemBookPreviewBinding

// book data - item_book_preview.xml layout을 연결하는 뷰 홀더 클래스
// 추가로 이 뷰 홀더를 재이용하는 어댑터도 작성해야함
class BookSearchViewHolder(
    private val binding: ItemBookPreviewBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(book: Book) {
        val author = book.authors.toString().removeSurrounding("[", "]") // list type이라 [] 제거
        val publisher = book.publisher
        val date = if (book.datetime.isNotEmpty()) book.datetime.substring(
            0,
            10
        ) else "" // null 방어 & 10 자리만(2000-00-00)

        // data를 각각의 view에 binding
        itemView.apply {
            binding.ivArticleImage.load(book.thumbnail)
            binding.tvTitle.text = book.title
            binding.tvAuthor.text = "$author | $publisher"
            binding.tvDatetime.text = date
        }
    }
}