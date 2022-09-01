package com.kjk.booksearchapp.data.model


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/*
* JSON 데이터 파싱을 위한 DTO
* plugin : JSON To Kotlin Class
* dependency : moshi
* @Json => @field:Json 으로 변경 필요
* */

/*
이 파일 생성할 때
new > kotlin data class file from JSON 선택
Response 에제 하나 복붙, format 버튼(뷰티파이)
Advanced
    property - val, non-nullable
    annotation - moshi
생성
응답의 계층 별 class 생성되므로 Book, Meta 는 자동생성된것
*/

@JsonClass(generateAdapter = true)
data class SearchResponse(
    @field:Json(name = "documents")
    val documents: List<Book>,
    @field:Json(name = "meta")
    val meta: Meta
)