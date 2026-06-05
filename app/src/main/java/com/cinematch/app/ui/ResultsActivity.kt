package com.cinematch.app.ui

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.cinematch.app.R
import com.cinematch.app.api.RetrofitClient
import com.cinematch.app.model.Movie
import com.cinematch.app.model.RecommendRequest
import kotlinx.coroutines.launch
import com.cinematch.app.model.AppDatabase
import com.cinematch.app.model.FavoriteMovie

class ResultsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results)
        supportActionBar?.hide()

        val mood     = intent.getStringExtra("mood") ?: "Happy"
        val energy   = intent.getStringExtra("energy") ?: "Medium"
        val company  = intent.getStringExtra("company") ?: "Solo"
        val length   = intent.getStringExtra("length") ?: "Any"
        val industry = intent.getStringExtra("industry") ?: "Any"

        fetchRecommendations(mood, energy, company, length, industry)

        findViewById<Button>(R.id.btnBack).setOnClickListener { finish() }
    }

    private fun fetchRecommendations(
        mood: String, energy: String, company: String,
        length: String, industry: String
    ) {
        val loading  = findViewById<ProgressBar>(R.id.progressBar)
        val container = findViewById<LinearLayout>(R.id.moviesContainer)
        val tvMood   = findViewById<TextView>(R.id.tvMood)
        val tvError  = findViewById<TextView>(R.id.tvError)

        loading.visibility = View.VISIBLE
        container.removeAllViews()

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.recommend(
                    RecommendRequest(mood, energy, company, length, industry)
                )
                loading.visibility = View.GONE

                if (response.isSuccessful && response.body() != null) {
                    val data = response.body()!!
                    tvMood.text = "${getMoodEmoji(data.mood)} ${data.mood} Mood · ${data.confidence}% confidence"
                    tvError.visibility = View.GONE
                    data.movies.forEach { movie ->
                        addMovieCard(container, movie)
                    }
                } else {
                    showError(tvError, loading)
                }
            } catch (e: Exception) {
                loading.visibility = View.GONE
                showError(tvError, loading)
            }
        }
    }

    private fun showError(tvError: TextView, loading: ProgressBar) {
        loading.visibility = View.GONE
        tvError.visibility = View.VISIBLE
        tvError.text = "Could not connect to server. Check your internet connection."
    }

    private fun addMovieCard(container: LinearLayout, movie: Movie) {
        val view = layoutInflater.inflate(R.layout.item_movie, container, false)

        view.findViewById<TextView>(R.id.tvTitle).text = movie.title
        view.findViewById<TextView>(R.id.tvGenres).text = movie.genres
        view.findViewById<TextView>(R.id.tvRating).text = "⭐ ${movie.rating}"
        view.findViewById<TextView>(R.id.tvIndustry).text = movie.industry

        val bookSection = view.findViewById<LinearLayout>(R.id.bookSection)
        val bookCover   = view.findViewById<android.widget.ImageView>(R.id.ivBookCover)
        val btnFav      = view.findViewById<android.widget.ImageButton>(R.id.btnFavorite)

        val db  = AppDatabase.getDatabase(this)
        val dao = db.favoriteDao()

        // Check if already favorite
        lifecycleScope.launch {
            val isFav = dao.isFavorite(movie.title)
            btnFav.setImageResource(
                if (isFav) android.R.drawable.btn_star_big_on
                else android.R.drawable.btn_star_big_off
            )
        }

        btnFav.setOnClickListener {
            lifecycleScope.launch {
                val isFav = dao.isFavorite(movie.title)
                if (isFav) {
                    dao.removeFavorite(FavoriteMovie(
                        title = movie.title, genres = movie.genres,
                        rating = movie.rating, industry = movie.industry,
                        bookTitle = movie.book?.title, bookAuthor = movie.book?.author
                    ))
                    btnFav.setImageResource(android.R.drawable.btn_star_big_off)
                    android.widget.Toast.makeText(this@ResultsActivity,
                        "Removed from favorites", android.widget.Toast.LENGTH_SHORT).show()
                } else {
                    dao.addFavorite(FavoriteMovie(
                        title = movie.title, genres = movie.genres,
                        rating = movie.rating, industry = movie.industry,
                        bookTitle = movie.book?.title, bookAuthor = movie.book?.author
                    ))
                    btnFav.setImageResource(android.R.drawable.btn_star_big_on)
                    android.widget.Toast.makeText(this@ResultsActivity,
                        "Added to favorites ⭐", android.widget.Toast.LENGTH_SHORT).show()
                }
            }
        }

        if (movie.book != null) {
            bookSection.visibility = View.VISIBLE
            view.findViewById<TextView>(R.id.tvBookTitle).text = movie.book.title
            view.findViewById<TextView>(R.id.tvBookAuthor).text = "by ${movie.book.author}"
            view.findViewById<TextView>(R.id.tvBookDesc).text = movie.book.description
            com.bumptech.glide.Glide.with(this)
                .load(movie.book.cover)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_gallery)
                .into(bookCover)
        } else {
            bookSection.visibility = View.GONE
        }

        container.addView(view)
    }

    private fun getMoodEmoji(mood: String) = when(mood) {
        "Happy"      -> "😊"
        "Thrilled"   -> "⚡"
        "Dreamy"     -> "🌙"
        "Scared"     -> "👻"
        "Thoughtful" -> "🧠"
        "Chill"      -> "☕"
        else         -> "🎬"
    }
}