package com.example.moengagenewsapp.networking

import android.util.Log
import com.example.moengagenewsapp.data.Article
import com.example.moengagenewsapp.data.Source
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.URL

class RemoteAPI {

    private val BASE_URL = "https://candidate-test-data-moengage.s3.amazonaws.com/Android/news-api-feed/staticResponse.json"

   fun getFact(onSuccess: (List<Article>) -> Unit, onError: (String) -> Unit) {
        GlobalScope.launch(Dispatchers.IO) {
            val connection = URL(BASE_URL).openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.setRequestProperty("ContentType", "application/json")
            connection.setRequestProperty("Accept", "application/json")
            connection.connectTimeout = 15000
            connection.readTimeout = 15000
            connection.doInput = true

            try {
                if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                    throw IOException("HTTP error code: ${connection.responseCode}")
                }

                val inputStream = connection.inputStream
                val reader = BufferedReader(InputStreamReader(inputStream))
                val response = StringBuilder()
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    response.append(line)
                }
                reader.close()

                val jsonResponse = JSONObject(response.toString())
                val articlesJsonArray = jsonResponse.getJSONArray("articles")

                val articles = mutableListOf<Article>()

                for (i in 0 until articlesJsonArray.length()) {
                    val articleJsonObject = articlesJsonArray.getJSONObject(i)
                    val sourceJsonObject = articleJsonObject.getJSONObject("source")
                    val source = Source(
                        id = sourceJsonObject.optString("id"),
                        name = sourceJsonObject.optString("name")
                    )
                    val article = Article(
                        source = source,
                        author = articleJsonObject.getString("author"),
                        title = articleJsonObject.getString("title"),
                        description = articleJsonObject.getString("description"),
                        url = articleJsonObject.getString("url"),
                        urlToImage = articleJsonObject.getString("urlToImage"),
                        publishedAt = articleJsonObject.getString("publishedAt"),
                        content = articleJsonObject.getString("content")
                    )
                    articles.add(article)
                }
                onSuccess(articles)
                Log.e("success call", articles.size.toString())

            } catch (e: Exception) {
                onError(e.message ?: "An error occurred")
                Log.e("errorcall", e.message.toString())

            } finally {
                connection.disconnect()
            }
        }
    }
}