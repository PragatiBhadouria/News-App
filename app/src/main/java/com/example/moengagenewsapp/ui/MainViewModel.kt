package com.example.moengagenewsapp.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.moengagenewsapp.data.Article
import com.example.moengagenewsapp.networking.RemoteAPI

class MainViewModel(val remoteAPI: RemoteAPI) : ViewModel()  {

    private val _articles = MutableLiveData<List<Article>>()
    val articles: LiveData<List<Article>>
        get() = _articles

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String>
        get() = _errorMessage

    private val map = HashMap<String,Int>()

    fun fetchNews(hasInternet: Boolean) {
        if (hasInternet) {
            remoteAPI.getFact(
                onSuccess = { articles ->
                    val uniqueArticles = filterUniqueArticles(articles)
                    _articles.postValue(uniqueArticles)
                   // _articles.postValue(articles)
                },
                onError = { errorMessage ->
                    _errorMessage.postValue(errorMessage)
                }
            )
        } else {
            _errorMessage.postValue("Network not Available")
        }
    }

    private fun filterUniqueArticles(articles : List<Article>):List<Article> {
        val uniqueArticles = mutableListOf<Article>()
        map.clear() // Clear the HashMap before updating it
        for (article in articles) {
            // Normalize the article URL to remove the protocol (http/https) before comparing
            val normalizedUrl = normalizeUrl(article.url)
            // Check if the normalized URL is already in the map
            if (!map.containsKey(normalizedUrl)) {
                // If not, add it to the map and to the list of unique articles
                map[normalizedUrl] = uniqueArticles.size // Store the position in the unique list
                uniqueArticles.add(article)
            }
        }
        return uniqueArticles
    }

    private fun normalizeUrl(url: String): String {
        // Remove the protocol (http/https) from the URL
        return url.replaceFirst("^https?://".toRegex(), "")
    }

     fun sortArticlesByOldToNew(articles : List<Article>) {
         val sortedArticle = articles.sortedBy { it.publishedAt }
         _articles.value = sortedArticle
    }

     fun sortArticlesByNewToOld(articles : List<Article>) {
         val sortedArticle = articles.sortedByDescending { it.publishedAt }
         _articles.value = sortedArticle

    }


}