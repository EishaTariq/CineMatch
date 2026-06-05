package com.cinematch.app.ui

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.cinematch.app.R
class MoodActivity : AppCompatActivity() {

    private var selectedMood = "Happy"
    private var selectedEnergy = "Medium"
    private var selectedCompany = "Solo"
    private var selectedLength = "Any"
    private var selectedIndustry = "Any"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mood)
        supportActionBar?.hide()

        setupSpinners()

        findViewById<Button>(R.id.btnFind).setOnClickListener {
            val intent = Intent(this, ResultsActivity::class.java).apply {
                putExtra("mood", selectedMood)
                putExtra("energy", selectedEnergy)
                putExtra("company", selectedCompany)
                putExtra("length", selectedLength)
                putExtra("industry", selectedIndustry)
            }
            startActivity(intent)
        }

        findViewById<Button>(R.id.btnFavorites).setOnClickListener {
            startActivity(Intent(this, FavoritesActivity::class.java))
        }
    }

    private fun setupSpinners() {
        setupSpinner(R.id.spinnerMood,
            arrayOf("Sad","Chill","Neutral","Happy","Excited")) { selectedMood = it }
        setupSpinner(R.id.spinnerEnergy,
            arrayOf("Exhausted","Low","Medium","High","Hyper")) { selectedEnergy = it }
        setupSpinner(R.id.spinnerCompany,
            arrayOf("Solo","Partner","Friends","Family")) { selectedCompany = it }
        setupSpinner(R.id.spinnerLength,
            arrayOf("Any","Short (<90 min)","Long (>120 min)")) { selectedLength = it }
        setupSpinner(R.id.spinnerIndustry,
            arrayOf("Any","Hollywood","Bollywood","Lollywood")) { selectedIndustry = it }
    }

    private fun setupSpinner(id: Int, items: Array<String>, onSelect: (String) -> Unit) {
        val spinner = findViewById<Spinner>(id)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p: AdapterView<*>?, v: android.view.View?, pos: Int, id: Long) {
                onSelect(items[pos])
            }
            override fun onNothingSelected(p: AdapterView<*>?) {}
        }
    }
}