package com.nudriin.myquote

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.nudriin.myquote.databinding.ActivityMainBinding
import cz.msebera.android.httpclient.Header
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getRandomQuote()

        binding.btnAllQuotes.setOnClickListener {
            val intent = Intent(this@MainActivity, ListQuotesActivity::class.java)
            startActivity(intent)
        }
    }

    private fun getRandomQuote() {
        binding.progressBar.visibility = View.INVISIBLE

        // Create loopj client
        val client = AsyncHttpClient()
        val url = "https://quote-api.dicoding.dev/random"

        client.get(url, object : AsyncHttpResponseHandler(){
            override fun onSuccess(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray) {
                val result = String(responseBody) // casting to string
                Log.d(TAG, result)

                try {
                    val jsonObj = JSONObject(result)

                    val quote = jsonObj.getString("en")
                    val author = jsonObj.getString("author")

                    binding.tvQuote.text = quote
                    binding.tvAuthor.text = author
                } catch (e: Exception) {
                    Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray?,
                error: Throwable?
            ) {
                binding.progressBar.visibility = View.INVISIBLE

                val errorMsg = when (statusCode) {
                    400 -> "$statusCode : Bad Request"
                    401 -> "$statusCode : Unauthorized"
                    403 -> "$statusCode : Forbidden"
                    404 -> "$statusCode : Not Found"
                    500 -> "$statusCode : Server Error"
                    else -> "$statusCode : ${error?.message}"
                }
                Toast.makeText(this@MainActivity, errorMsg, Toast.LENGTH_SHORT).show()
            }

        })

    }
}