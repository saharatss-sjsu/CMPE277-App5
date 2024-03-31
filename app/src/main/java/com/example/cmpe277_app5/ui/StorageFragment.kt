package com.example.cmpe277_app5.ui

import ChatRecordRow
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.cmpe277_app5.database.ChatDbHelper
import com.example.cmpe277_app5.database.ChatRecord
import com.example.cmpe277_app5.databinding.FragmentStorageBinding

class StorageFragment : Fragment() {

    private var _binding: FragmentStorageBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var chatRecords = mutableListOf<ChatRecord>()

    @RequiresApi(Build.VERSION_CODES.O)
    private fun databaseLoad() {
        val dbHelper = ChatDbHelper(requireContext())
        chatRecords = dbHelper.readAllRecords()
        Log.d("main", "SQLite read: $chatRecords")
        val listView = binding.listView
        listView.removeAllViews()
        val context = requireContext()
        chatRecords.forEach { record ->
            listView.addView(ChatRecordRow(context).apply {
                chatRecord = record
            })
        }
        if(chatRecords.isEmpty()){
            binding.textNoRecord.visibility = View.VISIBLE
        }else{
            binding.textNoRecord.visibility = View.GONE
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentStorageBinding.inflate(inflater, container, false)
        val root: View = binding.root

        databaseLoad()

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}