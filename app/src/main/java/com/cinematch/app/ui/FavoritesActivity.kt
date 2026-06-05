package com.cinematch.app.ui

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.cinematch.app.R
import com.cinematch.app.model.AppDatabase
import com.cinematch.app.model.FavoriteMovie
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FavoritesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorites)
        supportActionBar?.hide()

        val container = findViewById<LinearLayout>(R.id.favContainer)
        val tvEmpty   = findViewById<TextView>(R.id.tvEmpty)

        findViewById<Button>(R.id.btnBack).setOnClickListener { finish() }

        val dao = AppDatabase.getDatabase(this).favoriteDao()

        lifecycleScope.launch {
            dao.getAllFavorites().collectLatest { list ->
                container.removeAllViews()
                if (list.isEmpty()) {
                    tvEmpty.visibility = View.VISIBLE
                } else {
                    tvEmpty.visibility = View.GONE
                    list.forEach { addFavCard(container, it, dao) }
                }
            }
        }
    }

    private fun addFavCard(container: LinearLayout, movie: FavoriteMovie, dao: Any) {
        val db  = AppDatabase.getDatabase(this)
        val favDao = db.favoriteDao()

        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundResource(R.drawable.card_bg)
            setPadding(36, 30, 36, 30)
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.bottomMargin = 24
            layoutParams = params
        }

        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = android.view.Gravity.CENTER_VERTICAL
        }

        val title = TextView(this).apply {
            text = movie.title
            textSize = 15f
            setTextColor(android.graphics.Color.WHITE)
            setTypeface(null, android.graphics.Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }

        val btnRemove = ImageButton(this).apply {
            setImageResource(android.R.drawable.btn_star_big_on)
            setBackgroundColor(android.graphics.Color.TRANSPARENT)
            layoutParams = LinearLayout.LayoutParams(80, 80)
            setOnClickListener {
                lifecycleScope.launch {
                    favDao.removeFavorite(movie)
                    Toast.makeText(this@FavoritesActivity,
                        "Removed", Toast.LENGTH_SHORT).show()
                }
            }
        }

        row.addView(title)
        row.addView(btnRemove)
        card.addView(row)

        val meta = TextView(this).apply {
            text = "${movie.genres}  ⭐ ${movie.rating}  ${movie.industry}"
            textSize = 12f
            setTextColor(android.graphics.Color.parseColor("#888888"))
        }
        card.addView(meta)

        if (!movie.bookTitle.isNullOrEmpty()) {
            val bookText = TextView(this).apply {
                text = "📚 ${movie.bookTitle} by ${movie.bookAuthor}"
                textSize = 11f
                setTextColor(android.graphics.Color.parseColor("#c4b5fd"))
                setPadding(0, 16, 0, 0)
            }
            card.addView(bookText)
        }

        container.addView(card)
    }
}