package com.example.moengagenewsapp.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.moengagenewsapp.data.Article
import com.example.moengagenewsapp.databinding.ViewPagerListItemBinding

class NewsViewPagerAdapter(private val context: Context, private val articles:  List<Article>)
    : RecyclerView.Adapter<NewsViewPagerAdapter.NewsPagerViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewPagerAdapter.NewsPagerViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ViewPagerListItemBinding.inflate(inflater,parent, false)
        return NewsPagerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NewsViewPagerAdapter.NewsPagerViewHolder, position: Int) {
        val article = articles[position]
        holder.bind(article)
    }

    override fun getItemCount(): Int {
        return articles.size
    }

    inner class NewsPagerViewHolder(private val binding: ViewPagerListItemBinding) : RecyclerView.ViewHolder(binding.root){

        init {
            binding.root.setOnClickListener {
                val article = articles[adapterPosition]
                openArticleInBrowser(article.url)
            }
        }

        fun bind(article: Article){
            Glide.with(context)
                .load(article.urlToImage)
                .into(binding.articleImage)

            binding.articleTitle.text = article.title
            binding.articleDescription.text = article.description

        }
    }

    private fun openArticleInBrowser(articleUrl: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(articleUrl))
        context.startActivity(intent)
    }

}