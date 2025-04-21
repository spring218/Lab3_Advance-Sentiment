package com.example.text_classification.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.text_classification.R
import com.example.text_classification.data.model.Article
import com.example.text_classification.data.sentimentanalysis.SentimentAnalyzer
import com.example.text_classification.databinding.ItemNewsBinding

class NewsPagingAdapter(
    private val analyzer: SentimentAnalyzer
) : PagingDataAdapter<Article, NewsPagingAdapter.NewsViewHolder>(COMPARATOR) {

    inner class NewsViewHolder(val binding: ItemNewsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(article: Article) {
            binding.title.text = article.title
            binding.description.text = article.description
            binding.description.text = article.description
            binding.sentiment.text = analyzer.analyze(article.title ?: "")
            Glide.with(binding.imageView)
                .load(article.urlToImage)
                .placeholder(R.drawable.default_img) // hiển thị trong lúc loading
                .error(R.drawable.default_img)
                .into(binding.imageView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val binding = ItemNewsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NewsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    companion object {
        private val COMPARATOR = object : DiffUtil.ItemCallback<Article>() {
            override fun areItemsTheSame(oldItem: Article, newItem: Article) =
                oldItem.url == newItem.url

            override fun areContentsTheSame(oldItem: Article, newItem: Article) =
                oldItem == newItem
        }
    }
}
