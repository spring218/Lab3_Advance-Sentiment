package com.example.text_classification.data.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.text_classification.data.api.NewsApiService
import com.example.text_classification.data.model.Article
import retrofit2.HttpException
import java.io.IOException

enum class NewsType {
    EVERYTHING,
    TOP_HEADLINES
}

class NewsPagingSource(
    private val api: NewsApiService,
    private val apiKey: String,
    private val newsType: NewsType = NewsType.EVERYTHING,
    private val query: String = "travel",
    private val from: String = "2025-04-01",
    private val sortBy: String = "publishedAt",
    private val country: String? = null,
    private val sources: String? = null,
    private val category: String? = null
) : PagingSource<Int, Article>() {
    
    private val TAG = "NewsPagingSource"

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Article> {
        val page = params.key ?: 1
        
        Log.d(TAG, "Loading page $page for ${if (newsType == NewsType.EVERYTHING) "everything endpoint" else "top-headlines endpoint"}")
        
        return try {
            Log.d(TAG, "Making API request for page $page")
            val response = when (newsType) {
                NewsType.EVERYTHING -> {
                    Log.d(TAG, "API Request: everything - query=$query, from=$from, sortBy=$sortBy, page=$page")
                    api.getEverything(
                        query = query,
                        from = from,
                        sortBy = sortBy,
                        page = page,
                        apiKey = apiKey
                    )
                }
                NewsType.TOP_HEADLINES -> {
                    Log.d(TAG, "API Request: top-headlines - country=$country, sources=$sources, category=$category, page=$page")
                    api.getTopHeadlines(
                        country = country,
                        sources = sources,
                        category = category,
                        page = page,
                        apiKey = apiKey
                    )
                }
            }
            
            Log.d(TAG, "API response received: status=${response.status}, totalResults=${response.totalResults}, articles=${response.articles.size}")
            
            LoadResult.Page(
                data = response.articles,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (response.articles.isEmpty()) null else page + 1
            )
        } catch (e: IOException) {
            Log.e(TAG, "Network error: ${e.message}", e)
            LoadResult.Error(e)
        } catch (e: HttpException) {
            Log.e(TAG, "HTTP error ${e.code()}: ${e.message()}", e)
            LoadResult.Error(e)
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error: ${e.message}", e)
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Article>): Int? {
        return state.anchorPosition
    }
}
