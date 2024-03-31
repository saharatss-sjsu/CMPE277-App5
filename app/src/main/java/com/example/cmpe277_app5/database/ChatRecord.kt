package com.example.cmpe277_app5.database

import android.os.Build
import android.provider.BaseColumns
import androidx.annotation.RequiresApi
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class ChatRecord(val sequenceNo:String, val datetime: LocalDateTime, val isPrompt: Boolean, val content: String) {
    override fun toString(): String {
        return "<ChatRecord $sequenceNo, $datetime, ${if(isPrompt) "PROMPT" else "RESPONSE"}, \"$content\">"
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun datetimeToString(): String {
        return datetime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    }
}

interface DBEntry : BaseColumns {
    val TABLE_NAME: String
    val COLUMN_NAME_SEQUENCE_NO: String
    val COLUMN_NAME_DATETIME: String
    val COLUMN_NAME_CONTENT: String
}

object ChatRecordContract {
    object PromptEntry : DBEntry {
        override val TABLE_NAME = "prompt"
        override val COLUMN_NAME_SEQUENCE_NO = "sequence_no"
        override val COLUMN_NAME_DATETIME    = "datetime"
        override val COLUMN_NAME_CONTENT     = "prompt"
    }
    object ResponseEntry : DBEntry {
        override val TABLE_NAME = "response"
        override val COLUMN_NAME_SEQUENCE_NO = "sequence_no"
        override val COLUMN_NAME_DATETIME    = "datetime"
        override val COLUMN_NAME_CONTENT     = "response"
    }
}
