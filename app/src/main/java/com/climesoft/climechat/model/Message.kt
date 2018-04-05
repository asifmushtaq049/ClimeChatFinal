package com.climesoft.climechat.model

import android.database.Cursor

/**
 * Created by Asif on 6/10/2017.
 */

class Message(val id : Long, val body : String, val sender : String, val date : Long, val seen : Int, val status : Int, val gid : Long){
    companion object{
        fun fromCursor(cursor : Cursor) : Message{
            val id = cursor.getLong(cursor.getColumnIndexOrThrow("id"))
            val body = cursor.getString(cursor.getColumnIndexOrThrow("body"))
            val sender = cursor.getString(cursor.getColumnIndexOrThrow("sender"))
            val date = cursor.getLong(cursor.getColumnIndexOrThrow("date"))
            val seen = cursor.getInt(cursor.getColumnIndexOrThrow("seen"))
            val status = cursor.getInt(cursor.getColumnIndexOrThrow("status"))
            val gid = cursor.getLong(cursor.getColumnIndexOrThrow("gid"))
            return Message(id, body, sender, date, seen, status, gid)
        }
    }
}