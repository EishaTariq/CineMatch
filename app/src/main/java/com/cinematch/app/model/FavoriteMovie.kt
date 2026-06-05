package com.cinematch.app.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoriteMovie(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val genres: String,
    val rating: Double,
    val industry: String,
    val bookTitle: String? = null,
    val bookAuthor: String? = null
)