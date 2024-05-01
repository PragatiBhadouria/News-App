package com.example.moengagenewsapp

import NewsAdapter
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moengagenewsapp.data.Article
import com.example.moengagenewsapp.databinding.ActivityMainBinding
import com.example.moengagenewsapp.networking.NetworkCheck
import com.example.moengagenewsapp.networking.RemoteAPI
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging

@RequiresApi(Build.VERSION_CODES.M)
class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    lateinit var adapter: NewsAdapter
    private var articles = mutableListOf<Article>()
    lateinit var binding: ActivityMainBinding
    lateinit var remoteAPI: RemoteAPI

    private val networkCheck by lazy {
        NetworkCheck(getSystemService(ConnectivityManager::class.java))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(getLayoutInflater())
        setContentView(binding.getRoot())

        setSupportActionBar(binding.toolbar)

        FirebaseApp.initializeApp(this)


        FirebaseMessaging.getInstance().token
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                    return@OnCompleteListener
                }

                // Get the FCM token
                val token = task.result

                // Log and display the token
                Log.d(TAG, "FCM token: $token")
                // You can use the token as needed, for example, to send it to your server
            })

        remoteAPI = RemoteAPI()

        adapter = NewsAdapter(this, articles)
        binding.newsList.adapter = adapter
        binding.newsList.layoutManager = LinearLayoutManager(this)

        fetchNews()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_sort_old_to_new -> {
                sortArticlesByOldToNew()
                true
            }
            R.id.action_sort_new_to_old -> {
                sortArticlesByNewToOld()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun sortArticlesByOldToNew() {
        articles.sortBy { it.publishedAt }
        adapter.notifyDataSetChanged()
    }

    private fun sortArticlesByNewToOld() {
        articles.sortByDescending { it.publishedAt }
        adapter.notifyDataSetChanged()
    }

    private fun fetchNews() {
        // Check network availability before making the request
        if (networkCheck.hasValidInternetConn()) {
            // Make API call to fetch data
            remoteAPI.getFact(
                onSuccess = { articlesList ->
                    // Update the RecyclerView with the new data
                    //articles.clear()
                    Log.e("NetworkError", articlesList.size.toString())

                    runOnUiThread {
                        articles.addAll(articlesList)
                        adapter.notifyDataSetChanged()
                    }
                },
                onError = { errorMessage ->
                    // Handle error case
                    runOnUiThread {

                        Log.e("NetworkError", errorMessage)
                        Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }
            )
        } else {
            Toast.makeText(applicationContext, "Network not Available", Toast.LENGTH_SHORT)
                .show()
        }
    }


}
