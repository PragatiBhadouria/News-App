import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.moengagenewsapp.data.Article
import com.example.moengagenewsapp.databinding.ListItemNewsBinding

class NewsAdapter(private val context: Context, private val articles: List<Article>) :
    RecyclerView.Adapter<NewsAdapter.ArticleViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemNewsBinding.inflate(inflater, parent, false)
        return ArticleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = articles[position]
        holder.bind(article)
    }

    override fun getItemCount(): Int {
        return articles.size
    }

    inner class ArticleViewHolder(private val binding: ListItemNewsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val article = articles[adapterPosition]
                openArticleInBrowser(article.url)
            }
        }

        fun bind(article: Article) {
            Glide.with(context)
                .load(article.urlToImage)
                .into(binding.articleImage)

            if (article.author == null || article.author.equals("null", ignoreCase = true)) {
                binding.articleAuthor.visibility = View.GONE
            } else {
                binding.articleAuthor.text = article.author
                binding.articleAuthor.visibility = View.VISIBLE
            }

            binding.articleTitle.text = article.title
            binding.articleDescription.text = article.description
            binding.articleDateTime.text = article.publishedAt
        }

        private fun openArticleInBrowser(articleUrl: String) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(articleUrl))
            context.startActivity(intent)
        }

    }
}
