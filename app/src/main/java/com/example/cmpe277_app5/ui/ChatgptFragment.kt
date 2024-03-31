package com.example.cmpe277_app5.ui

import ChatRecordRow
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.RECEIVER_EXPORTED
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.view.marginBottom
import androidx.fragment.app.Fragment
import com.example.cmpe277_app5.CallOpenAIService
import com.example.cmpe277_app5.database.ChatDbHelper
import com.example.cmpe277_app5.database.ChatRecord
import com.example.cmpe277_app5.databinding.FragmentChatgptBinding
import org.json.JSONObject
import org.w3c.dom.Text
import java.time.LocalDateTime

class ChatgptFragment : Fragment() {

    private var _binding: FragmentChatgptBinding? = null

    private val binding get() = _binding!!

    private val messages = mutableListOf<ChatRecord>()


    @RequiresApi(Build.VERSION_CODES.O)
    private fun databaseSave() {
        val context = requireContext()
        context.deleteDatabase("ChatRecordDb.db")
        messages.forEach { message ->
            ChatDbHelper(context).writeRecord(message)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatgptBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val context = requireContext()

        val textInput = binding.textInput

        val progressBar = binding.progressBar
        progressBar.visibility = View.GONE

        val sendButton = binding.buttonSend
        sendButton.apply {
            setOnClickListener {
                val message = textInput.text.toString()
                textInput.setText("")
                messages.add(ChatRecord("", LocalDateTime.now(), true, message))
                val intent = Intent(context, CallOpenAIService::class.java)
                intent.putExtra("prompt", message)
                intent.putExtra("prompt", message)
                context.startService(intent)
                updateListView()

                val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
                inputMethodManager?.hideSoftInputFromWindow(view?.windowToken, 0)

                sendButton.visibility  = View.GONE
                textInput.visibility   = View.GONE
                progressBar.visibility = View.VISIBLE
            }
        }

        val filter = IntentFilter("cmpe277.app5.UPDATE_ACTIVITY")
        context.registerReceiver(updateReceiver, filter, RECEIVER_EXPORTED)

        binding.buttonSave.setOnClickListener {
            databaseSave()
        }

        updateListView()

        return  root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateListView(){
        if (_binding == null) return
        val listView = binding.listView
        listView.removeAllViews()

        try {
            val context = requireContext()
            messages.forEach { record ->
                listView.addView(ChatRecordRow(context).apply {
                    chatRecord = record
                })
            }
        } catch (e: Exception) {
            Log.w("main", "OpenAI: updating view error: $e")
        }

    }

    private val updateReceiver = object : BroadcastReceiver() {
        @RequiresApi(Build.VERSION_CODES.O)
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getStringExtra("id")
            val message = intent?.getStringExtra("response")
            Log.d("main", "OpenAI: response = $message")
            messages.add(ChatRecord(id.toString(), LocalDateTime.now(), false, message.toString()))
            updateListView()

            binding.buttonSend.visibility  = View.VISIBLE
            binding.textInput.visibility   = View.VISIBLE
            binding.progressBar.visibility = View.GONE
        }
    }
}