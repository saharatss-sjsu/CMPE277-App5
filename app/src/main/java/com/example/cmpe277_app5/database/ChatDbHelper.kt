package com.example.cmpe277_app5.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Build
import android.provider.BaseColumns
import android.util.Log
import androidx.annotation.RequiresApi

import com.example.cmpe277_app5.database.ChatRecordContract.PromptEntry
import com.example.cmpe277_app5.database.ChatRecordContract.ResponseEntry
import java.time.LocalDateTime

class ChatDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "ChatRecordDb.db"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE ${PromptEntry.TABLE_NAME} (" +
                "${BaseColumns._ID} INTEGER PRIMARY KEY," +
                "${PromptEntry.COLUMN_NAME_SEQUENCE_NO} CHAR(64)," +
                "${PromptEntry.COLUMN_NAME_DATETIME} CHAR(64)," +
                "${PromptEntry.COLUMN_NAME_CONTENT} CHAR(1024))")
        db.execSQL("CREATE TABLE ${ResponseEntry.TABLE_NAME} (" +
                "${BaseColumns._ID} INTEGER PRIMARY KEY," +
                "${ResponseEntry.COLUMN_NAME_SEQUENCE_NO} CHAR(64)," +
                "${ResponseEntry.COLUMN_NAME_DATETIME} CHAR(64)," +
                "${ResponseEntry.COLUMN_NAME_CONTENT} CHAR(4096))")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS ${PromptEntry.TABLE_NAME}")
        db.execSQL("DROP TABLE IF EXISTS ${ResponseEntry.TABLE_NAME}")
        onCreate(db)
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun writeRecord(record: ChatRecord): Long {
        val db = writableDatabase
        if (record.isPrompt) {
            val values = ContentValues().apply {
                put(PromptEntry.COLUMN_NAME_SEQUENCE_NO, record.sequenceNo)
                put(PromptEntry.COLUMN_NAME_DATETIME, record.datetimeToString())
                put(PromptEntry.COLUMN_NAME_CONTENT, record.content)
            }
            return db.insert(PromptEntry.TABLE_NAME, null, values)
        } else {
            val values = ContentValues().apply {
                put(ResponseEntry.COLUMN_NAME_SEQUENCE_NO, record.sequenceNo)
                put(PromptEntry.COLUMN_NAME_DATETIME, record.datetimeToString())
                put(ResponseEntry.COLUMN_NAME_CONTENT, record.content)
            }
            return db.insert(ResponseEntry.TABLE_NAME, null, values)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun readAllRecords(): MutableList<ChatRecord> {
        val db = readableDatabase
        val items = mutableListOf<ChatRecord>()

        mutableListOf(PromptEntry, ResponseEntry).forEach { dbEntry ->
            val projection = arrayOf(
                BaseColumns._ID,
                dbEntry.COLUMN_NAME_SEQUENCE_NO,
                dbEntry.COLUMN_NAME_DATETIME,
                dbEntry.COLUMN_NAME_CONTENT
            )

            val cursor = db.query(
                dbEntry.TABLE_NAME, // The table to query
                projection,    // The array of columns to return (pass null to get all)
                null,  // The columns for the WHERE clause (null means no WHERE clause, so all rows are returned)
                null,  // The values for the WHERE clause
                null,  // don't group the rows
                null,  // don't filter by row groups
                null   // The sort order (ascending or descending)
            )

            with(cursor) {
                while (moveToNext()) {
                    val itemId = getLong(getColumnIndexOrThrow(BaseColumns._ID))
                    items.add(ChatRecord(
                        getString(getColumnIndexOrThrow(dbEntry.COLUMN_NAME_SEQUENCE_NO)),
                        LocalDateTime.parse(getString(getColumnIndexOrThrow(dbEntry.COLUMN_NAME_DATETIME))),
                        dbEntry::class == PromptEntry::class,
                        getString(getColumnIndexOrThrow(dbEntry.COLUMN_NAME_CONTENT))
                    ))
                }
            }
            cursor.close()
        }



        return items
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun deleteRecord(record: ChatRecord) {
        val db = writableDatabase

        var deletedRowCount = 0
        if (record.isPrompt){
            val selection = "${PromptEntry.COLUMN_NAME_SEQUENCE_NO} = ? AND ${PromptEntry.COLUMN_NAME_DATETIME} = ?"
            val selectionArgs = arrayOf(record.sequenceNo, record.datetimeToString())
            deletedRowCount = db.delete(PromptEntry.TABLE_NAME, selection, selectionArgs)
        } else {

        }
        if (deletedRowCount > 0) {
            Log.d("main", "SQLite delete: Successfully $deletedRowCount records")
        } else {
            Log.d("main", "SQLite delete: No records deleted")
        }

    }
}
