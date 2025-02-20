package com.example.cmpe277_app5

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.cmpe277_app5.database.ChatRecord
import java.io.BufferedWriter
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import org.json.JSONObject
import java.time.LocalDateTime

class CallOpenAIService : Service() {
    @RequiresApi(Build.VERSION_CODES.O)
    fun extractMessageContent(jsonString: String): ChatRecord? {
        val jsonObject = JSONObject(jsonString)
        val firstChoice = jsonObject.getJSONArray("choices").optJSONObject(0)
        val messageContent = firstChoice?.optJSONObject("message")?.optString("content")
        if (!messageContent.isNullOrBlank()) {
            return ChatRecord(jsonObject.getString("id"), LocalDateTime.now(), false, messageContent)
        }
        return null
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Thread {
            val url = URL("https://api.openai.com/v1/chat/completions")
            val httpURLConnection = url.openConnection() as HttpURLConnection

            val prompt = intent?.extras?.getString("prompt").toString()

            try {
                httpURLConnection.requestMethod = "POST"
                httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
                httpURLConnection.setRequestProperty("Authorization", "Bearer XXXX")
                httpURLConnection.doOutput = true
                httpURLConnection.doInput = true

                val jsonInputString = """
                      {
                           "model": "gpt-3.5-turbo",
                            "messages": [
                                {
                                    "role": "user",
                                    "content": "$prompt"
                                }
                            ],
                            "temperature": 1,
                            "max_tokens": 256,
                            "top_p": 1,
                            "frequency_penalty": 0,
                            "presence_penalty": 0
                      }
                    """.trimIndent()

                httpURLConnection.outputStream.use { os ->
                    BufferedWriter(OutputStreamWriter(os, "UTF-8")).use { writer ->
                        writer.write(jsonInputString)
                        writer.flush()
                    }
                }

                val responseCode = httpURLConnection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val response = httpURLConnection.inputStream.bufferedReader().use { it.readText() }
                    Log.d("openai", "Response: $response")
                    Log.d("openai", "extractMessageContent: ${extractMessageContent(response)}")
                    Intent("cmpe277.app5.UPDATE_ACTIVITY").also {
                        val chat = extractMessageContent(response) ?: return@Thread
                        it.putExtra("id", chat.sequenceNo)
                        it.putExtra("response", chat.content)
                        sendBroadcast(it)
                    }
                } else {
                    Log.d("openai", "Response: ERROR $responseCode")

                }
            } finally {
                httpURLConnection.disconnect()
            }
        }.start()
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

}