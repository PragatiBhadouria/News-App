package com.example.moengagenewsapp

import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moengagenewsapp.data.Article
import com.example.moengagenewsapp.databinding.ActivityMainBinding
import com.example.moengagenewsapp.networking.NetworkCheck
import com.example.moengagenewsapp.networking.RemoteAPI
import com.example.moengagenewsapp.ui.MainViewModel
import com.example.moengagenewsapp.ui.MainViewModelFactory
import com.example.moengagenewsapp.ui.NewsAdapter
import com.example.moengagenewsapp.ui.NewsViewPagerAdapter
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging

@RequiresApi(Build.VERSION_CODES.M)
class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var adapter: NewsAdapter
    private lateinit var viewPagerAdapter: NewsViewPagerAdapter
    private val articles = mutableListOf<Article>()

    private val networkCheck by lazy {
        NetworkCheck(getSystemService(ConnectivityManager::class.java))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        binding.toolbar.title = "NEWS APP"

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

        val remoteAPI = RemoteAPI()
        val viewModelFactory = MainViewModelFactory(remoteAPI)
        viewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]

        viewPagerAdapter = NewsViewPagerAdapter(this, articles)

        //initialize adapter
        adapter = NewsAdapter(this, articles)
        binding.newsList.adapter = adapter
        binding.newsList.layoutManager = LinearLayoutManager(this)

        fetchNews()

        viewModel.articles.observe(this, Observer { articles ->
            this.articles.clear()
            this.articles.addAll(articles)
            adapter.notifyDataSetChanged()
        })

        viewModel.errorMessage.observe(this, Observer { errorMessage ->
            Log.e("NetworkError", errorMessage)
            Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_SHORT).show()
        })

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_sort_old_to_new -> {
                viewModel.sortArticlesByOldToNew(articles)
                adapter.notifyDataSetChanged()
                true
            }
            R.id.action_sort_new_to_old -> {
                viewModel.sortArticlesByNewToOld(articles)
                adapter.notifyDataSetChanged()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun fetchNews() {
        if (networkCheck.hasValidInternetConn()) {
            viewModel.fetchNews(true)
        } else {
            Toast.makeText(applicationContext, "Network not Available", Toast.LENGTH_SHORT).show()
            viewModel.fetchNews(false)
        }
    }
}
