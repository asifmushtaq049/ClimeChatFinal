package com.climesoft.climechat.model

import android.database.Cursor

/**
 * Created by Asif on 6/16/2017.
 */

class Notification(val id : Long, val body : String, val phone : String, val date : Long, var response : Int, val groupId : Long){
    companion object{
        fun fromCursor(cursor : Cursor) : Notification{
            val id = cursor.getLong(cursor.getColumnIndexOrThrow("id"))
            val body = cursor.getString(cursor.getColumnIndexOrThrow("body"))
            val phone = cursor.getString(cursor.getColumnIndexOrThrow("phone"))
            val date = cursor.getLong(cursor.getColumnIndexOrThrow("date"))
            val response = cursor.getInt(cursor.getColumnIndexOrThrow("response"))
            val groupId = cursor.getLong(cursor.getColumnIndexOrThrow("gid"))
            return Notification(id, body, phone, date, response, groupId)
        }
    }
}