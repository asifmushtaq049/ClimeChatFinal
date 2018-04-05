package com.climesoft.climechat.model

import android.database.Cursor



/**
 * Created by Asif on 6/4/2017.
 */
class Group(val id : Long, val name: String, val lastMessage : String, val lastActivity : Long, val date : Long){
    companion object{
        fun fromCursor(cursor : Cursor) : Group{
            val id = cursor.getLong(cursor.getColumnIndexOrThrow("id"))
            val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
            val lastActivity = cursor.getLong(cursor.getColumnIndexOrThrow("last_activity"))
            val date = cursor.getLong(cursor.getColumnIndexOrThrow("date"))
            return Group(id, name, "", lastActivity, date)
        }
    }
}