package com.nudriin.myquote

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.nudriin.myquote.databinding.ActivityListQuotesBinding
import cz.msebera.android.httpclient.Header
import org.json.JSONArray

class ListQuotesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityListQuotesBinding

    companion object {
        private val TAG = ListQuotesActivity::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListQuotesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val layoutManager = LinearLayoutManager(this)
        binding.listQuotes.layoutManager = layoutManager

        val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        binding.listQuotes.addItemDecoration(itemDecoration)

        getListQuote()
    }

    private fun getListQuote() {
        binding.progressBar.visibility = View.VISIBLE

        val client = AsyncHttpClient()
        val url = "https://quote-api.dicoding.dev/list"

        client.get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(p0: Int, p1: Array<out Header>?, p2: ByteArray?) {
                binding.progressBar.visibility = View.INVISIBLE

                val listQuote = ArrayList<String>()

                val result = String(p2!!)

                Log.d(TAG, result)

                try {
                    val jsonArr = JSONArray(result)

                    for (i in 0 until  jsonArr.length()) {
                        val jsonObj = jsonArr.getJSONObject(i)
                        val quote = jsonObj.getString("en")
                        val author = jsonObj.getString("author")
                        listQuote.add("\n$quote\n — $author\n")
                    }

                    val adapter = ListQuoteAdapter(listQuote)
                    binding.listQuotes.adapter = adapter
                } catch (e: Exception){
                    Toast.makeText(this@ListQuotesActivity, e.message, Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }
            }

            override fun onFailure(
                p0: Int,
                p1: Array<out Header>?,
                p2: ByteArray?,
                p3: Throwable?
            ) {
                binding.progressBar.visibility = View.INVISIBLE
                val errorMsg = when (p0) {
                    400 -> "$p0 : Bad Request"
                    401 -> "$p0 : Unauthorized"
                    403 -> "$p0 : Forbidden"
                    404 -> "$p0 : Not Found"
                    500 -> "$p0 : Server Error"
                    else -> "$p0 : ${p3?.message}"
                }
                Toast.makeText(this@ListQuotesActivity, errorMsg, Toast.LENGTH_SHORT).show()
            }

        })
    }
}