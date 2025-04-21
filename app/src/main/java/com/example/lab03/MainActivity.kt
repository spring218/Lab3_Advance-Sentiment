package com.example.text_classification

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.text_classification.data.sentimentanalysis.SentimentAnalyzer
import com.example.text_classification.databinding.ActivityMainBinding
import com.example.text_classification.ui.NewsPagingAdapter
import com.example.text_classification.ui.NewsViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: NewsViewModel
    private lateinit var adapter: NewsPagingAdapter
    private lateinit var analyzer: SentimentAnalyzer
    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        Log.d(TAG, "onCreate: Initializing MainActivity")
        
        try {
            Log.d(TAG, "Setting up SentimentAnalyzer")
            analyzer = SentimentAnalyzer(this)
            
            Log.d(TAG, "Setting up NewsPagingAdapter")
            adapter = NewsPagingAdapter(analyzer)
            
            Log.d(TAG, "Setting up RecyclerView")
            binding.recyclerView.layoutManager = LinearLayoutManager(this)
            binding.recyclerView.adapter = adapter
            
            Log.d(TAG, "Setting up NewsViewModel")
            viewModel = NewsViewModel()
            
            Log.d(TAG, "Loading default news")
            // Default load everything endpoint
            loadDefaultNews()
            
            // Set title to indicate current source
            updateTitle("Default News")
            
        } catch (e: Exception) {
            Log.e(TAG, "Error in onCreate: ${e.message}", e)
            Toast.makeText(this, "Error initializing app: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
    
    private fun updateTitle(source: String) {
        binding.titleText.text = source
    }
    
    private fun loadDefaultNews() {
        try {
            Log.d(TAG, "Loading default news flow")
            lifecycleScope.launch {
                try {
                    viewModel.pagedNews.collectLatest { pagingData ->
                        Log.d(TAG, "Submitting default news data to adapter")
                        adapter.submitData(pagingData)
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error collecting default news: ${e.message}", e)
                    Toast.makeText(this@MainActivity, "Error loading news: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
            updateTitle("Default News")
        } catch (e: Exception) {
            Log.e(TAG, "Error in loadDefaultNews: ${e.message}", e)
            Toast.makeText(this, "Error loading news: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun loadTopHeadlinesUS() {
        try {
            Log.d(TAG, "Loading US top headlines")
            lifecycleScope.launch {
                try {
                    viewModel.getTopHeadlinesByCountry("us").collectLatest { pagingData ->
                        Log.d(TAG, "Submitting US headlines data to adapter")
                        adapter.submitData(pagingData)
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error collecting US headlines: ${e.message}", e)
                    Toast.makeText(this@MainActivity, "Error loading US headlines: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
            updateTitle("US Headlines")
        } catch (e: Exception) {
            Log.e(TAG, "Error in loadTopHeadlinesUS: ${e.message}", e)
            Toast.makeText(this, "Error loading US headlines: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun loadBBCNews() {
        try {
            Log.d(TAG, "Loading BBC News")
            lifecycleScope.launch {
                try {
                    viewModel.getTopHeadlinesBySources("bbc-news").collectLatest { pagingData ->
                        Log.d(TAG, "Submitting BBC News data to adapter")
                        adapter.submitData(pagingData)
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error collecting BBC News: ${e.message}", e)
                    Toast.makeText(this@MainActivity, "Error loading BBC News: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
            updateTitle("BBC News")
        } catch (e: Exception) {
            Log.e(TAG, "Error in loadBBCNews: ${e.message}", e)
            Toast.makeText(this, "Error loading BBC News: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun loadAppleNews() {
        try {
            Log.d(TAG, "Loading Apple News")
            lifecycleScope.launch {
                try {
                    viewModel.getEverythingByQuery("Apple").collectLatest { pagingData ->
                        Log.d(TAG, "Submitting Apple News data to adapter")
                        adapter.submitData(pagingData)
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error collecting Apple News: ${e.message}", e)
                    Toast.makeText(this@MainActivity, "Error loading Apple News: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
            updateTitle("Apple News")
        } catch (e: Exception) {
            Log.e(TAG, "Error in loadAppleNews: ${e.message}", e)
            Toast.makeText(this, "Error loading Apple News: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        Log.d(TAG, "Creating options menu")
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.d(TAG, "Option item selected: ${item.title}")
        return when (item.itemId) {
            R.id.menu_everything -> {
                loadDefaultNews()
                true
            }
            R.id.menu_us_headlines -> {
                loadTopHeadlinesUS()
                true
            }
            R.id.menu_bbc_news -> {
                loadBBCNews()
                true
            }
            R.id.menu_apple_news -> {
                loadAppleNews()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}