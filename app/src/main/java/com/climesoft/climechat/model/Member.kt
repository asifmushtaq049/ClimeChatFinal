package com.climesoft.climechat.model

import android.database.Cursor

/**
 * Created by Asif on 6/12/2017.
 */
class Member(val id : Long, val phone : String, val active : Int, val cid : Int, val joinDate : Long, val gid : Long){
    companion object{
        fun fromCursor(cursor : Cursor) : Member{
            val id = cursor.getLong(cursor.getColumnIndexOrThrow("id"))
            val phone = cursor.getString(cursor.getColumnIndexOrThrow("phone"))
            val active = cursor.getInt(cursor.getColumnIndexOrThrow("active"))
            val cid = cursor.getInt(cursor.getColumnIndexOrThrow("cid"))
            val date = cursor.getLong(cursor.getColumnIndexOrThrow("join_date"))
            val gid = cursor.getLong(cursor.getColumnIndexOrThrow("gid"))
            return Member(id, phone, active, cid, date, gid)
        }
    }
}