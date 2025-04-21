package com.example.text_classification.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.text_classification.data.api.NewsApiService
import com.example.text_classification.data.model.Article
import com.example.text_classification.data.paging.NewsPagingSource
import com.example.text_classification.data.paging.NewsType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class NewsViewModel : ViewModel() {
    private val apiKey = "2c5fc06bdd8e41ce9d4bed33e5a52707"
    private val TAG = "NewsViewModel"
    
    // Create logging interceptor
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    
    // Create OkHttpClient with logging and timeout
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
    
    // Create Retrofit with logging
    val api = Retrofit.Builder()
        .baseUrl("https://newsapi.org/")
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(NewsApiService::class.java)

    // Default news flow using "everything" endpoint
    val pagedNews: Flow<PagingData<Article>> = Pager(PagingConfig(pageSize = 20)) {
        Log.d(TAG, "Creating paging source for everything endpoint")
        NewsPagingSource(api, apiKey)
    }.flow
        .onStart { Log.d(TAG, "Starting to fetch news") }
        .catch { e -> 
            Log.e(TAG, "Error fetching news: ${e.message}", e)
        }
        .cachedIn(viewModelScope)
    
    // Get top headlines from a specific country
    fun getTopHeadlinesByCountry(country: String): Flow<PagingData<Article>> = 
        Pager(PagingConfig(pageSize = 20)) {
            Log.d(TAG, "Creating paging source for top headlines by country: $country")
            NewsPagingSource(
                api = api,
                apiKey = apiKey,
                newsType = NewsType.TOP_HEADLINES,
                country = country
            )
        }.flow
            .onStart { Log.d(TAG, "Starting to fetch top headlines for $country") }
            .catch { e -> 
                Log.e(TAG, "Error fetching top headlines for $country: ${e.message}", e)
            }
            .cachedIn(viewModelScope)
    
    // Get top headlines from specific sources
    fun getTopHeadlinesBySources(sources: String): Flow<PagingData<Article>> = 
        Pager(PagingConfig(pageSize = 20)) {
            Log.d(TAG, "Creating paging source for top headlines by sources: $sources")
            NewsPagingSource(
                api = api,
                apiKey = apiKey,
                newsType = NewsType.TOP_HEADLINES,
                sources = sources
            )
        }.flow
            .onStart { Log.d(TAG, "Starting to fetch top headlines for sources: $sources") }
            .catch { e -> 
                Log.e(TAG, "Error fetching top headlines for sources $sources: ${e.message}", e)
            }
            .cachedIn(viewModelScope)
    
    // Get everything by query
    fun getEverythingByQuery(query: String, from: String = "2025-04-01", sortBy: String = "publishedAt"): Flow<PagingData<Article>> = 
        Pager(PagingConfig(pageSize = 20)) {
            Log.d(TAG, "Creating paging source for query: $query, from: $from, sortBy: $sortBy")
            NewsPagingSource(
                api = api,
                apiKey = apiKey,
                newsType = NewsType.EVERYTHING,
                query = query,
                from = from,
                sortBy = sortBy
            )
        }.flow
            .onStart { Log.d(TAG, "Starting to fetch everything for query: $query") }
            .catch { e -> 
                Log.e(TAG, "Error fetching everything for query $query: ${e.message}", e)
            }
            .cachedIn(viewModelScope)
}
