package com.cinematch.app.model

data class RecommendRequest(
    val mood: String,
    val energy: String,
    val company: String,
    val length: String,
    val industry: String
)

data class Book(
    val title: String,
    val author: String,
    val description: String,
    val cover: String
)

data class Movie(
    val title: String,
    val genres: String,
    val rating: Double,
    val industry: String,
    val book: Book?
)

data class RecommendResponse(
    val success: Boolean,
    val mood: String,
    val confidence: Int,
    val count: Int,
    val movies: List<Movie>
)